package com.varabyte.kobweb.core.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.varabyte.kobweb.core.PageContext

/**
 * Declare a list of layout arguments, where each entry must follow the form "key=value".
 *
 * For example:
 * ```
 * @LayoutArgs("title=Home", "desc="The home page.")
 * ```
 *
 * You can query layout arguments at runtime using `rememberPageContext().layout.params`
 *
 * @see Layout
 */
@Target(AnnotationTarget.FUNCTION)
annotation class LayoutArgs(vararg val params: String)

/**
 * Declare a single layout key/value pair.
 *
 * This annotation is repeatable, so you can declare many per function. For example:
 * ```
 * @LayoutArg("title", "Home")
 * @LayoutArg("desc", "The home page.")
 * ```
 *
 * You can query layout arguments at runtime using `rememberPageContext().layout.params`
 *
 * @see Layout
 */
@Target(AnnotationTarget.FUNCTION)
@Repeatable
annotation class LayoutArg(val key: String, val value: String)


/**
 * Tags a function (which returns a map) OR a map property as one that provides layout argument key/value pairs.
 *
 * For example:
 * ```
 * // Tag a function
 * // NOTE: You do not have to specify the `ctx` parameter if you don't need it
 * @LayoutArgsProvider()
 * fun provideLayoutArgs(ctx: LayoutArgsProviderContext): Map<String, String> {
 *   return mapOf("title" to "Home", "desc" to "The home page.")
 * }
 *
 * // Tag a property
 * @LayoutArgsProvider()
 * val layoutArgs = mapOf( "title" to "Home", "desc", to "The home page.")
 * ```
 *
 * A `@LayoutArgsProvider` value will be tied to any composable methods in the current source file tagged with
 * `@Layout`.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class LayoutArgsProvider()

/**
 * A collection of potentially useful values that can help a user customize the layout args
 */
class LayoutArgsProviderContext(
    val route: PageContext.RouteInfo
)

internal val LayoutArgsLocal = staticCompositionLocalOf<Map<String, String>> { emptyMap() }


@Composable
fun provideLayoutArgs(params: Map<String, String>, content: @Composable () -> Unit) {
    val currArgs = LayoutArgsLocal.current
    CompositionLocalProvider(LayoutArgsLocal.provides(currArgs + params), content = content)
}