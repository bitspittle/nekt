package com.varabyte.kobweb.core.layout

/**
 * An annotation which opts a page or layout out of using a [DefaultLayout], if specified.
 *
 * @see DefaultLayout
 */
@Target(AnnotationTarget.FUNCTION)
annotation class NoLayout(val fqn: String)
