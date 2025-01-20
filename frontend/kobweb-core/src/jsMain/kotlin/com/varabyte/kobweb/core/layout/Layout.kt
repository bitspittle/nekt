package com.varabyte.kobweb.core.layout

import androidx.compose.runtime.*

/**
 * An annotation which declares that a [Composable] function is one that should be wrapped by a parent layout.
 *
 * A `Layout` annotation takes a path to a composable method which is designed to serve as the parent layout for this
 * method. For example, a declaration might look like `@Layout(".components.layout.ExampleLayout")`.
 *
 * This target layout method must only accept a single argument: `content: @Composable () -> Unit`. If you want to pass
 * any additional arguments to it, you must use [LayoutArgs].
 *
 * The motivation behind this feature is it allows us to convert pages from this:
 * ```
 * @Page
 * @Composable
 * fun BlogPage() {
 *   BlogLayout {
 *     /* ... */
 *   }
 * }
 * ```
 * to this:
 * ```
 * @Page
 * @Layout(".components.layouts.BlogLayout")
 * @Composable
 * fun BlogPage() {
 *   /* ... */
 * }
 * ```
 * which, in addition to reducing one level of nesting, plays nicer with Compose by correcting the composable hierarchy
 * to what it should be.
 *
 * For example, above, users will mentally model the page as "App > BlogLayout > BlogPage" intuitively (even if, in
 * practice, the actual hierarchy is "App > BlogPage > BlogLayout").
 *
 * Without using `@Layout`, a page will still render just fine. However, if users navigate between different pages,
 * Compose will lose any state normally stored inside the layout, because from its point of view, it sees the "layout"
 * composable as a child to each page, so each one is considered separate.
 *
 * In other words, the remembered `state` value in a layout like this:
 * ```
 * @Composable
 * fun ExampleLayout(content: @Composable () -> Unit) {
 *   val state = remember { /* ... */ }
 * }
 * ```
 * will be remembered across pages that use `@Layout` to reference the layout method, but it will be discarded on pages
 * that manually call `ExampleLayout` directly themselves:
 * ```
 * // Navigating between the following two pages will result in `ExampleLayout` from resetting any remembered state.
 *
 * @Page
 * @Composable
 * fun FirstPage() {
 *   ExampleLayout { /* ... */ }
 * }
 *
 * @Page
 * @Composable
 * fun SecondPage() {
 *   ExampleLayout { /* ... */ }
 * }
 * ```
 *
 * Finally, note that a layout can delegate to another layout, like so:
 * ```
 * @Composable()
 * fun PageLayout(content: @Composable () -> Unit) { /* ... */ }
 *
 * @Composable
 * @Layout(".components.layout.PageLayout")
 * fun BlogLayout(content: @Composable () -> Unit) { /* ... */ }
 *
 * @Page
 * @Composable
 * @Layout(".components.layout.BlogLayout")
 * fun BlogPage() { /* ... */ }
 * ```
 * which, before layouts, used to look like this:
 * ```
 * @Composable()
 * fun PageLayout(content: @Composable () -> Unit) { /* ... */ }
 *
 * @Composable
 * @Layout(".components.layout.PageLayout")
 * fun BlogLayout(content: @Composable () -> Unit) { /* ... */ }
 *
 * @Page
 * @Composable
 * @Layout(".components.layout.BlogLayout")
 * fun BlogPage() { /* ... */ }
 * ```
 *
 * Ultimately, users are heavily encouraged to use `@Layout` for their pages, as in general there is very little
 * disadvantage to doing so, and remembering layout state across pages is very useful. For example, your layout can
 * contain a sidebar with collapsable sections. When you reference this layout via the `@Layout` annotation, you can
 * remember which sections are collapsed which will then persist across pages.
 *
 * @see DefaultLayout
 * @see LayoutArgs
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Layout(val fqn: String)
