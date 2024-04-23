@file:Suppress("LeakingThis") // Following official Gradle guidance

package com.varabyte.kobweb.gradle.library.extensions

import com.varabyte.kobweb.gradle.core.extensions.KobwebBlock
import kotlinx.html.HEAD
import kotlinx.html.TagConsumer
import kotlinx.html.stream.createHTML
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import javax.inject.Inject

/**
 * A sub-block for defining all properties relevant to a Kobweb library.
 */
abstract class LibraryBlock : ExtensionAware {
    /**
     * A sub-block for defining properties related to the "index.html" document generated by Kobweb
     *
     * It's expected that this will be done, in general, by the app itself, but libraries are given the ability to
     * add their own tweaks, in case they provide functionality that depend on something being present in the final
     * HTML.
     */
    abstract class IndexBlock : ExtensionAware {
        @get:Inject
        internal abstract val providerFactory: ProviderFactory

        internal abstract val newHead: ListProperty<String>

        /**
         * Add a block of HTML elements to the `<head>` of the app's generated `index.html` file.
         *
         * Note that apps will have the option to opt-out of including these elements.
         */
        fun addToHead(block: HEAD.() -> Unit) {
            // Wrap computation with a provider in case it references any lazy properties
            newHead.add(providerFactory.provider { serializeHeadContents(block) })
        }

        /**
         * A list of head element builders to add to the generated index.html file.
         *
         * The reason this is exposed as a list instead of a property is so that different tasks can add their own
         * values (usually scripts or stylesheets) independently of one another.
         */
        @Deprecated("The`head` property has been reworked. Replace `head.add { ... }` with `addToHead { ... }`")
        // TODO: Rename `newHead` to `head` when removing this property
        abstract val head: ListProperty<HEAD.() -> Unit>
    }

    init {
        extensions.create<IndexBlock>("index")
    }
}

val LibraryBlock.index: LibraryBlock.IndexBlock
    get() = extensions.getByType<LibraryBlock.IndexBlock>()

val KobwebBlock.library: LibraryBlock
    get() = extensions.getByType<LibraryBlock>()

internal fun KobwebBlock.createLibraryBlock(): LibraryBlock {
    return extensions.create<LibraryBlock>("library")
}

// TODO: de-duplicate from AppBlock.kt

// Generate the html nodes without the containing <head> tag
// See: https://github.com/Kotlin/kotlinx.html/issues/228
private inline fun <T, C : TagConsumer<T>> C.headFragment(crossinline block: HEAD.() -> Unit): T {
    HEAD(emptyMap(), this).block()
    return this.finalize()
}

// Use `xhtmlCompatible = true` to include a closing slash as currently kotlinx.html needs them when adding raw text.
// See: https://github.com/Kotlin/kotlinx.html/issues/247
private fun serializeHeadContents(block: HEAD.() -> Unit): String =
    createHTML(prettyPrint = false, xhtmlCompatible = true).headFragment(block)
