@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.tasks

import com.varabyte.kobweb.gradle.application.templates.createApisFactoryImpl
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import com.varabyte.kobweb.gradle.core.kmp.jvmTarget
import com.varabyte.kobweb.gradle.core.project.backend.BackendData
import com.varabyte.kobweb.gradle.core.project.backend.merge
import com.varabyte.kobweb.gradle.core.tasks.KobwebModuleTask
import com.varabyte.kobweb.gradle.core.util.searchZipFor
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.internal.provider.DefaultProvider
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class KobwebGenerateApisFactoryTask @Inject constructor(
    kobwebBlock: KobwebBlock,
    @get:Optional @get:InputFiles val kspGenFile: Provider<RegularFile>?
) : KobwebModuleTask(kobwebBlock, "Generate Kobweb code for the server") {

    @InputFiles
    fun getCompileClasspath(): Provider<FileCollection> = project.jvmTarget?.let { jvmTarget ->
        @Suppress("UNCHECKED_CAST")
        project.configurations.named(jvmTarget.compileClasspath) as Provider<FileCollection>
    } ?: DefaultProvider { project.objects.fileCollection() }

    @OutputFile
    fun getGenApisFactoryFile() = kobwebBlock.getGenJvmSrcRoot(project).resolve("ApisFactoryImpl.kt")

    @TaskAction
    fun execute() {
        val backendData = buildList {
            kspGenFile?.get()?.asFile?.takeIf { it.exists() }?.let {
                add(Json.decodeFromString<BackendData>(it.readText()))
            }
            getCompileClasspath().get().files.forEach { file ->
                file.searchZipFor("backend.json") { bytes ->
                    add(Json.decodeFromString<BackendData>(bytes.decodeToString()))
                }
            }
        }.merge()

        val apisFactoryFile = getGenApisFactoryFile()
        apisFactoryFile.parentFile.mkdirs()
        apisFactoryFile.writeText(createApisFactoryImpl(backendData))
    }
}
