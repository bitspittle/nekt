@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.application.extensions

import com.varabyte.kobweb.common.navigation.RoutePrefix
import com.varabyte.kobweb.common.text.prefixIfNot
import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
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
import javax.inject.Inject

/**
 * A sub-block for defining properties related to the "index.html" document generated by Kobweb
 */
abstract class AppBlock @Inject constructor(conf: KobwebConf, baseGenDir: Property<String>) :
    KobwebBlock.FileGeneratingBlock {
    /**
     * A sub-block for defining properties related to the "index.html" document generated by Kobweb
     */
    abstract class IndexBlock @Inject constructor(val routePrefix: RoutePrefix) : ExtensionAware {
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
    }
}

val AppBlock.index: AppBlock.IndexBlock
    get() = extensions.getByType<AppBlock.IndexBlock>()

val KobwebBlock.app: AppBlock
    get() = extensions.getByType<AppBlock>()

internal fun KobwebBlock.createAppBlock(conf: KobwebConf) {
    extensions.create<AppBlock>("app", conf, baseGenDir)
}
