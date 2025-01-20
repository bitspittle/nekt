package com.varabyte.kobweb.core.layout

/**
 * @see LayoutArgs
 */
@Target(AnnotationTarget.FUNCTION)
annotation class DefaultLayoutArgs(vararg val params: String)
