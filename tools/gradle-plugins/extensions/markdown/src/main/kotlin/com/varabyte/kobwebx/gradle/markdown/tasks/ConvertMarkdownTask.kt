package com.varabyte.kobwebx.gradle.markdown.tasks

import com.varabyte.kobweb.common.lang.packageConcat
import com.varabyte.kobweb.common.text.splitCamelCase
import com.varabyte.kobweb.gradle.core.util.LoggingReporter
import com.varabyte.kobweb.gradle.core.util.getBuildScripts
import com.varabyte.kobweb.project.common.PackageUtils
import com.varabyte.kobwebx.gradle.markdown.KotlinRenderer
import com.varabyte.kobwebx.gradle.markdown.MarkdownBlock
import com.varabyte.kobwebx.gradle.markdown.MarkdownFeatures
import com.varabyte.kobwebx.gradle.markdown.MarkdownHandlers
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.io.path.invariantSeparatorsPathString

abstract class ConvertMarkdownTask @Inject constructor(markdownBlock: MarkdownBlock) :
    MarkdownTask(
        markdownBlock,
        "Convert markdown files found in the project's resources path to source code in the final project"
    ) {

    // Use changing the build script as a proxy for changing markdownBlock or kobwebBlock values.
    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    fun getBuildScripts(): List<File> = projectLayout.getBuildScripts()

    private val markdownHandlers = markdownBlock.extensions.getByType<MarkdownHandlers>()
    private val markdownFeatures = markdownBlock.extensions.getByType<MarkdownFeatures>()

    @get:Inject
    abstract val objectFactory: ObjectFactory

    @get:Input
    abstract val dependsOnMarkdownArtifact: Property<Boolean>

    @get:InputDirectory
    abstract val generatedMarkdownDir: DirectoryProperty

    @OutputDirectory
    fun getGenDir(): Provider<Directory> {
        return markdownBlock.getGenJsSrcRoot("convert").flatMap { rootDir ->
            val subDir = projectGroup.zip(pagesPackage) { group, pagesPackage ->
                PackageUtils.resolvePackageShortcut(group.toString(), pagesPackage).replace(".", "/")
            }
            rootDir.dir(subDir)
        }
    }

    @TaskAction
    fun execute() {
        getGenDir().get().asFile.clearDirectory()
        val cache = NodeCache(
            parser = markdownFeatures.createParser(),
            roots = getMarkdownRoots().get() + generatedMarkdownDir.asFileTree
        )
        val markdownFiles = getMarkdownResources().get() + objectFactory.fileTree().setDir(generatedMarkdownDir)

        markdownFiles.visit {
            if (isDirectory) return@visit

            val mdFile = file
            val packageParts = packagePartsFor(relativePath)
            val ktFileName = mdFile.nameWithoutExtension.capitalized()
            val mdPathRel = relativePath.toPath()
            val mdPathRelStr = mdPathRel.invariantSeparatorsPathString

            run {
                mdPathRelStr.split('/').dropLast(1).forEachIndexed { i, dirPart ->
                    val shouldCreatePackageMapping = dirPart.contains('_') || dirPart.splitCamelCase().size > 1
                    if (!shouldCreatePackageMapping) return@forEachIndexed

                    // If here, this means that the markdown path contains an intentional underscore or camel casing,
                    // which the Kobweb KSP processor will remove by default (see
                    // com/varabyte/kobweb/ksp/util/RouteUtils.kt), so here we add a PackageMapping to ensure that won't
                    // happen.

                    val subpackage = packageParts.subList(0, i + 1)

                    File(getGenDir().get().asFile, "${subpackage.joinToString("/")}/PackageMapping.kt")
                        // Multiple markdown files in the same folder will try to write this over and over again; we
                        // can skip after the first time
                        .takeIf { !it.exists() }
                        ?.let { mappingFile ->

                            mappingFile.parentFile.mkdirs()
                            mappingFile.writeText(
                                """
                                @file:PackageMapping("$dirPart")
                                package ${
                                    PackageUtils.resolvePackageShortcut(
                                        projectGroup.get().toString(),
                                        pagesPackage.get().packageConcat(subpackage.joinToString("."))
                                    )
                                }
                                import com.varabyte.kobweb.core.PackageMapping
                            """.trimIndent()
                            )
                        }
                }
            }


            File(
                getGenDir().get().asFile,
                mdPathRel.resolveSibling("$ktFileName.kt").invariantSeparatorsPathString
            ).let { outputFile ->
                outputFile.parentFile.mkdirs()
                val mdPackage = absolutePackageFor(packageParts)
                val funName = funNameFor(mdFile)
                val ktRenderer = KotlinRenderer(
                    cache::getRelative,
                    markdownBlock.defaultRoot.orNull?.takeUnless { it.isBlank() },
                    markdownBlock.imports.get(),
                    mdPathRelStr,
                    markdownHandlers,
                    mdPackage,
                    funName,
                    projectGroup.get().toString(),
                    dependsOnMarkdownArtifact.get(),
                    LoggingReporter(logger),
                )
                outputFile.writeText(ktRenderer.render(cache[mdFile]))
            }
        }
    }

    /**
     * Class which maintains a cache of parsed markdown content associated with their source files.
     *
     * This cache is useful because Markdown files can reference other Markdown files, meaning as we process a
     * collection of them, we might end up referencing the same file multiple times.
     *
     * Note that this cache should not be created with too long a lifetime, because users may edit Markdown files and
     * those changes should be picked up. It is intended to be used only for a single processing run across a collection
     * of markdown files and then discarded.
     *
     * @param parser The parser to use to parse markdown files.
     * @param roots A collection of root folders under which Markdown files should be considered for processing. Any
     *   markdown files referenced outside of these roots should be ignored for caching purposes.
     */
    private class NodeCache(private val parser: Parser, private val roots: List<File>) {
        private val existingNodes = mutableMapOf<String, Node>()

        /**
         * Returns a parsed Markdown [Node] for the target file (which is expected to be a valid markdown file).
         *
         * Once queried, the node will be cached so that subsequent calls to this method will not re-read the file. If
         * the file fails to parse, this method will throw an exception.
         */
        operator fun get(file: File): Node = file.canonicalFile.let { canonicalFile ->
            require(roots.any { canonicalFile.startsWith(it) }) {
                "File $canonicalFile is not under any of the specified Markdown roots: $roots"
            }
            existingNodes.computeIfAbsent(canonicalFile.invariantSeparatorsPath) {
                parser.parse(canonicalFile.readText())
            }
        }

        /**
         * Returns a parsed Markdown node given a relative path which will be resolved against all markdown roots.
         *
         * For example, "test/example.md" will return parsed markdown information if found in
         * `src/jsMain/resources/markdown/test/example.md`.
         *
         * This will return null if:
         * * no file is found matching the passed in path.
         * * the file at the specified location fails to parse.
         * * the relative file path escapes the current root, e.g. `../public/files/license.md`, as this could be a
         *   useful way to link to a raw markdown file that should be served as is and not converted into an html page.
         */
        fun getRelative(relPath: String): Node? = try {
            roots.asSequence()
                .map { it to it.resolve(relPath).canonicalFile }
                // Make sure we don't access anything outside our markdown roots
                .firstOrNull { (root, canonicalFile) ->
                    canonicalFile.exists() && canonicalFile.isFile && canonicalFile.startsWith(root)
                }?.second?.let(::get)
        } catch (ignored: IOException) {
            null
        }
    }
}
