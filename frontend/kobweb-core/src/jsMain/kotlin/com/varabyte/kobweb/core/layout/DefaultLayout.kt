package com.varabyte.kobweb.core.layout

/**
 * An annotation which declares a layout function as the default one for the current site.
 *
 * In other words, it lets you simplify this sort of code:
 * ```
 * @Composable
 * fun PageLayout(content: @Composable () -> Unit) { /* ... */ }
 *
 * @Layout(".components.layout.PageLayout")
 * @Page
 * @Composable
 * fun SomePage() { /* ... */ }
 *
 * @Layout(".components.layout.PageLayout")
 * @Page
 * @Composable
 * fun AnotherPage() { /* ... */ }

 * @Layout(".components.layout.PageLayout")
 * @Page
 * @Composable
 * fun YetAnotherPage() { /* ... */ }
 * ```
 * to this:
 * ```
 * @Composable
 * @DefaultLayout
 * fun PageLayout(content: @Composable () -> Unit) { /* ... */ }
 *
 * @Page
 * @Composable
 * fun SomePage() { /* ... */ }
 *
 * @Page
 * @Composable
 * fun AnotherPage() { /* ... */ }

 * @Page
 * @Composable
 * fun YetAnotherPage() { /* ... */ }
 * ```
 *
 * @see Layout
 */
@Target(AnnotationTarget.FUNCTION)
annotation class DefaultLayout()
