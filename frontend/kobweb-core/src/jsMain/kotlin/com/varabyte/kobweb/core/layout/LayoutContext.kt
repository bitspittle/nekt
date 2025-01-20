package com.varabyte.kobweb.core.layout

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.PageContextLocal
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.navigation.Route
import com.varabyte.kobweb.navigation.Router
import kotlinx.browser.window
import kotlin.error

/**
 * Various contextual information associated with a layout.
 *
 * Access it via [rememberLayoutContext] or, if you happened to call [rememberPageContext] for some other purpose, you
 * can also use the `PageContext.layout` property to check it as well
 *
 * ```
 * @Composable
 * @LayoutArg("title", "Default title")
 * fun PageLayout() {
 *    val ctx = rememberLayoutContext()
 *    val title = ctx.params.getValue("title")
 *    /* ... */
 * }
 * ```
 */
class LayoutContext internal constructor(
    val params: Map<String, String>
)

// Note: LayoutContextLocal is technically a global, but we wrap it in a `PageContextLocal` as a way to ensure it is only
// accessible when under a `@Page` composable.
internal val LayoutContextLocal = staticCompositionLocalOf<LayoutContext?> { null }

// Extend `rememberPageContext()` with layout specific values
// Users can also use `rememberLayoutContext`, but this is another, more intuitive way to grab the values if they
// already have a page context anyway.
@Suppress("UnusedReceiverParameter") // A layout context scope is tied to the page context scope
val PageContext.layout: LayoutContext
    @Composable
    @ReadOnlyComposable
    get() = LayoutContextLocal.current!! // Should always be valid as long as the parent PageContext is valid is valid

/**
 * Returns the active page's layout context (useful when writing layouts).
 *
 * This will throw an exception if not called within the scope of a `@Page` annotated composable.
 */
@Composable
// Note: Technically this isn't a real "remember", as the page context is really just a composition local, but we leave
// the API like this because user's mental model should think of it like a normal remember call. After all, they
// shouldn't wrap the return value in a remember themselves. It's possible we may revisit this approach in the future,
// as well.
fun rememberLayoutContext() = LayoutContextLocal.current ?: error("LayoutContext is only valid within a @Page composable")
