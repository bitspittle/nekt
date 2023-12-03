@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.extensions

import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.common.text.prefixIfNot
import com.varabyte.kobweb.gradle.application.Browser
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.project.KobwebFolder
import com.varabyte.kobweb.project.conf.KobwebConf
import kotlinx.html.HEAD
import kotlinx.html.link
import kotlinx.html.meta
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import java.nio.file.Path
import javax.inject.Inject
import kotlin.time.Duration

// The "export" block moved into the "app" sub-block. We need to provide an old version for backwards compatibility for
// a short time.
// TODO(#168): Remove before Kobweb v1.0
// NOTE: Gradle doesn't show this top-level deprecation message to users unfortunately, but we leave it here so we can
// find it easily when searching for `@Deprecated` tags and remember to delete it later.
@Deprecated("`kobweb { export { ... } }` has moved to `kobweb { app { export { ... } } }`.")
abstract class LegacyExportBlock {
    @Deprecated("The `export` block has moved under the `app` block. Use `kobweb { app { export { browser.set(...) } } }` instead.")
    abstract val browser: Property<Browser>

    @Deprecated("The `export` block has moved under the `app` block. Use `kobweb { app { export { includeSourceMap.set(...) } } }` instead.")
    abstract val includeSourceMap: Property<Boolean>
}

/**
 * A sub-block for defining all properties relevant to a Kobweb application.
 */
abstract class AppBlock @Inject constructor(
    kobwebFolder: KobwebFolder,
    conf: KobwebConf,
    baseGenDir: Property<String>,
    @Suppress("DEPRECATION") legacyExportBlock: LegacyExportBlock,
) :
    KobwebBlock.FileGeneratingBlock {
    /**
     * A sub-block for defining properties related to the "index.html" document generated by Kobweb
     */
    abstract class IndexBlock @Inject constructor(val routePrefix: RoutePrefix) : ExtensionAware {
        class ExcludeTagsContext(val name: String)

        /**
         * A list of head element builders to add to the generated index.html file.
         *
         * The reason this is exposed as a list instead of a property is so that different tasks can add their own
         * values (usually scripts or stylesheets) independently of one another.
         */
        abstract val head: ListProperty<HEAD.() -> Unit>

        /**
         * The default description to set in the meta tag.
         *
         * Note that if you completely replace the head block (e.g. `head.set(...)` in your build script), this value
         * will not be used.
         */
        abstract val description: Property<String>

        /**
         * The path to use for the favicon in the link tag.
         *
         * For example, "/favicon.ico" (which is the default value) will refer to the icon file located at
         * "jsMain/resources/public/favicon.ico".
         *
         * You are expected to begin your path with a '/' to explicitly indicate that the path will always be rooted
         * regardless of which URL on your site you visit. If you do not, a leading slash will be added for you.
         *
         * Note that if you completely replace the head block (e.g. `head.set(...)` in your build script), this value
         * will not be used.
         */
        abstract val faviconPath: Property<String>

        /**
         * A list of attribute key / value pairs to add to the script tag that imports your site.
         *
         * By default, Kobweb will just generate a very minimal script tag:
         *
         * ```
         * <script src="/yourapp.js"></script>
         * ```
         *
         * However, if you need to add attributes to it, you can do so here. For example, if you need to add a `type`
         * attribute, you can do so like this:
         *
         * ```
         * scriptAttributes.put("type", "module")
         * ```
         *
         * which would generate:
         *
         * ```
         * <script src="/yourapp.js" type="module"></script>
         * ```
         */
        abstract val scriptAttributes: MapProperty<String, String>

        /**
         * Logic to decide if any dependencies should be excluded from generating head elements in the index.html file.
         *
         * If you include a Kobweb library that declares a collection of head elements, this will be reported with a
         * message that shows what they are and includes a value to set here to opt-out of them.
         *
         * An example message looks like this:
         *
         * ```
         * Dependency "kotlin-bootstrap-js-1.0.klib" will add the following <head> elements to your site's index.html:
         *   <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"></script>
         *   <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css">
         *   <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
         * Add `kobweb { app { index { excludeTagsForDependency("kotlin-bootstrap") } } }` to your build.gradle.kts file to opt-out.
         * ```
         *
         * You can use [excludeTagsForDependency], or you can handle it yourself by using [excludeTags] directly:
         *
         * ```
         * excludeTags.set { name.startsWith("kotlin-bootstrap") }
         * ```
         *
         * The receiver for the callback is the [ExcludeTagsContext] class.
         *
         * You can do a full opt-out by calling [excludeAllTags].
         */
        abstract val excludeTags: Property<ExcludeTagsContext.() -> Boolean>

        init {
            description.convention("Powered by Kobweb")
            faviconPath.convention("/favicon.ico")

            head.set(listOf {
                meta {
                    name = "description"
                    content = description.get()
                }
                link {
                    rel = "icon"
                    href = routePrefix.prependTo(faviconPath.get().prefixIfNot("/"))
                }

                // Viewport content chosen for a good mobile experience.
                // See also: https://developer.mozilla.org/en-US/docs/Web/HTML/Viewport_meta_tag#viewport_basics
                meta("viewport", "width=device-width, initial-scale=1")
            })
        }
    }

    /**
     * Configuration values for the backend of this Kobweb application.
     */
    abstract class ServerBlock : ExtensionAware {
        /**
         * Configuration for remote debugging.
         */
        abstract class RemoteDebuggingBlock : ExtensionAware {
            /**
             * When `true`, enables remote debugging on the Kobweb server.
             *
             * Remote debugging will only work if the server is running in development mode.
             */
            abstract val enabled: Property<Boolean>

            /**
             * The port to use for remote debugging.
             *
             * Defaults to `5005`, a common default for remote debugging.
             *
             * @see <a href="https://www.jetbrains.com/help/idea/attaching-to-local-process.html#attach-to-remote">Remote debugging documentation</a>
             */
            abstract val port: Property<Int>

            init {
                enabled.convention(false)
                port.convention(5005)
            }
        }

        init {
            extensions.create<RemoteDebuggingBlock>("remoteDebugging")
        }
    }

    /**
     * A sub-block for defining properties related to configuring the `kobweb export` step.
     */
    @Suppress("DEPRECATION")
    abstract class ExportBlock @Inject constructor(
        private val kobwebFolder: KobwebFolder,
        legacyExportBlock: LegacyExportBlock
    ) {
        /**
         * @property route The route for the current page being exported, including a leading slash
         *   (e.g. "/admin/login"). Note that if your project specifies a global route prefix, it will not be included
         *   here.
         */
        class ExportFilterContext(
            val route: String,
        )

        /**
         * Configuration values for taking a trace at the export step.
         *
         * You can read more about traces [in the official documentation](https://playwright.dev/docs/trace-viewer).
         *
         * @property root The root directory where traces should be saved.
         * @property filter A filter which, if set, will be invoked for every route being exported to test if it should
         *   be traced. For example, to export all traces under the /admin/ route, you could set this to
         *   `filter = { route -> route.startsWith("/admin/") }`.
         * @property includeScreenshots Whether to include screenshots in the final trace. This will increase the size
         *  of each trace, but it's highly encouraged to set this to true as it can be very helpful in debugging.
         *
         * @see enableTraces
         */
        class TraceConfig(
            val root: Path,
            val filter: (String) -> Boolean,
            val includeScreenshots: Boolean,
        )

        /**
         * Which browser to use for the export step.
         *
         * Besides potentially affecting the snapshotted output and export times, this can also affect the download size.
         *
         * Chromium is chosen as a default due to its ubiquity, but Firefox may also be a good choice as its download size
         * is significantly smaller than Chromium's.
         */
        abstract val browser: Property<Browser>

        /**
         * Whether to include a source map when exporting your site.
         *
         * In release mode, source gets minified and obfuscated, but if you include your source map with your site, browsers
         * can still show you the original source code when you inspect an element.
         *
         * By default, this value is set to true, making it easier for developers to debug a problem gone awry.
         */
        abstract val includeSourceMap: Property<Boolean>

        /**
         * A filter which, if set, will be invoked for every page to test if it should be exported.
         *
         * The callback should return true to allow the export and false otherwise.
         *
         * If this isn't set, then all discovered pages will be exported.
         *
         * @see ExportFilterContext
         */
        abstract val filter: Property<ExportFilterContext.() -> Boolean>

        /**
         * The max timeout to allow for each export.
         *
         * By default, this is chosen by Playwright, which at the time of writing this documentation uses 30 seconds as
         * a timeout.
         */
        abstract val timeout: Property<Duration>

        /**
         * @see enableTraces
         */
        internal abstract val traceConfig: Property<TraceConfig>

        /**
         * Enable traces for your export.
         *
         * Traces are a feature provided by Playwright (the engine we use to download a browser and take export
         * snapshots). Traces should rarely be required, but they can help you understand what's going on under the hood
         * when an export takes significantly longer than you'd expect.
         *
         * When enabled, a bunch of zip files will be saved under your specified trace path, which can be dragged /
         * dropped into the [Playwright Trace Viewer](https://trace.playwright.dev/) to get a breakdown of what's going
         * on. This can be useful to do in combination with setting the server logs level to "TRACE" (and then checking
         * `.kobweb/server/logs/kobweb-server.log` to get a deeper look into what's gone wrong.)
         *
         * You can read more about traces [in the official documentation](https://playwright.dev/docs/trace-viewer).
         *
         * See the docs for [TraceConfig] for more details about the parameters for this method. The `tracesRoot`
         * location defaults to `.kobweb/export-traces`.
         *
         * @see com.varabyte.kobweb.project.conf.Server.Logging.Level
         */
        fun enableTraces(
            tracesRoot: Path = kobwebFolder.resolve("export-traces"),
            filter: (String) -> Boolean = { true },
            showScreenshots: Boolean = true,
        ) {
            traceConfig.set(TraceConfig(tracesRoot, filter, showScreenshots))
        }

        init {
            // TODO(#168): When legacy export block is deleted, move defaults straight into the actual properties
            legacyExportBlock.browser.convention(Browser.Chromium)
            browser.convention(legacyExportBlock.browser)
            legacyExportBlock.includeSourceMap.convention(true)
            includeSourceMap.convention(legacyExportBlock.includeSourceMap)
        }
    }

    /**
     * A collection of key / value pairs which will be made available within your Kobweb app via `AppGlobals`.
     *
     * This is a useful place to save constant values that describe your app, like a version value or build timestamp.
     *
     * See also: `com.varabyte.kobweb.core.AppGlobals`.
     */
    abstract val globals: MapProperty<String, String>

    /**
     * When `true`, all URLs will have their `.htm` and `.html` suffix automatically removed when the user types it in.
     *
     * Defaults to `true`.
     */
    abstract val cleanUrls: Property<Boolean>

    init {
        globals.set(mapOf("title" to conf.site.title))
        cleanUrls.convention(true)
        genDir.convention(baseGenDir.map { "$it/app" })

        extensions.create<IndexBlock>("index", RoutePrefix(conf.site.routePrefix))
        extensions.create<ServerBlock>("server")
        extensions.create<ExportBlock>("export", kobwebFolder, legacyExportBlock)
    }
}

val AppBlock.index: AppBlock.IndexBlock
    get() = extensions.getByType<AppBlock.IndexBlock>()

val AppBlock.export: AppBlock.ExportBlock
    get() = extensions.getByType<AppBlock.ExportBlock>()

val AppBlock.server: AppBlock.ServerBlock
    get() = extensions.getByType<AppBlock.ServerBlock>()

val AppBlock.ServerBlock.remoteDebugging: AppBlock.ServerBlock.RemoteDebuggingBlock
    get() = extensions.getByType<AppBlock.ServerBlock.RemoteDebuggingBlock>()

val KobwebBlock.app: AppBlock
    get() = extensions.getByType<AppBlock>()

internal fun KobwebBlock.createAppBlock(kobwebFolder: KobwebFolder, conf: KobwebConf) {
    // Deprecation is meant to communicate this class is going away eventually, but we still need to support it for a
    // little while
    @Suppress("DEPRECATION")
    val legacyExportBlock = extensions.create<LegacyExportBlock>("export")

    extensions.create<AppBlock>("app", kobwebFolder, conf, baseGenDir, legacyExportBlock)
}

/**
 * A convenience method for the common case of specifying a direct dependency with the [excludeTags][AppBlock.IndexBlock.excludeTags] method.
 *
 * The value passed into this method is a prefix, so for example, the names "kotlin-bootstrap", "kotlin-bootstrap-js",
 * "kotlin-bootstrap-js-1.0", and "kotlin-bootstrap-js-1.0.klib" would all work to opt-out of accepting head elements
 * from the "kotlin-bootstrap-js-1.0.klib" artifact.
 *
 * When Kobweb detects a dependency that adds head elements, it will print a warning message that includes the value
 * of the artifact name which you can use here.
 *
 * If you call this method multiple times with different dependencies, the effect is additive.
 */
fun AppBlock.IndexBlock.excludeTagsForDependency(dependencyNamePrefix: String) {
    excludeTags.set {
        val ctx = this
        name.startsWith(dependencyNamePrefix) || excludeTags.get().invoke(ctx)
    }
}

fun AppBlock.IndexBlock.excludeAllTags() {
    excludeTags.set { true }
}
