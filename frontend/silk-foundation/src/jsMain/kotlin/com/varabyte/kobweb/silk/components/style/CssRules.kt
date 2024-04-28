@file:Suppress("DeprecatedCallableAddReplaceWith") // ReplaceWith doesn't work great for extension methods

package com.varabyte.kobweb.silk.components.style

import com.varabyte.kobweb.silk.style.CssRule
import com.varabyte.kobweb.silk.style.StyleModifiers
import com.varabyte.kobweb.silk.style.selector.active
import com.varabyte.kobweb.silk.style.selector.after
import com.varabyte.kobweb.silk.style.selector.anyLink
import com.varabyte.kobweb.silk.style.selector.ariaDisabled
import com.varabyte.kobweb.silk.style.selector.ariaInvalid
import com.varabyte.kobweb.silk.style.selector.ariaRequired
import com.varabyte.kobweb.silk.style.selector.autofill
import com.varabyte.kobweb.silk.style.selector.before
import com.varabyte.kobweb.silk.style.selector.checked
import com.varabyte.kobweb.silk.style.selector.default
import com.varabyte.kobweb.silk.style.selector.disabled
import com.varabyte.kobweb.silk.style.selector.empty
import com.varabyte.kobweb.silk.style.selector.enabled
import com.varabyte.kobweb.silk.style.selector.firstChild
import com.varabyte.kobweb.silk.style.selector.firstLetter
import com.varabyte.kobweb.silk.style.selector.firstLine
import com.varabyte.kobweb.silk.style.selector.firstOfType
import com.varabyte.kobweb.silk.style.selector.focus
import com.varabyte.kobweb.silk.style.selector.focusVisible
import com.varabyte.kobweb.silk.style.selector.focusWithin
import com.varabyte.kobweb.silk.style.selector.hover
import com.varabyte.kobweb.silk.style.selector.inRange
import com.varabyte.kobweb.silk.style.selector.indeterminate
import com.varabyte.kobweb.silk.style.selector.invalid
import com.varabyte.kobweb.silk.style.selector.lastChild
import com.varabyte.kobweb.silk.style.selector.lastOfType
import com.varabyte.kobweb.silk.style.selector.link
import com.varabyte.kobweb.silk.style.selector.mediaPrint
import com.varabyte.kobweb.silk.style.selector.not
import com.varabyte.kobweb.silk.style.selector.onlyChild
import com.varabyte.kobweb.silk.style.selector.onlyOfType
import com.varabyte.kobweb.silk.style.selector.optional
import com.varabyte.kobweb.silk.style.selector.outOfRange
import com.varabyte.kobweb.silk.style.selector.placeholder
import com.varabyte.kobweb.silk.style.selector.placeholderShown
import com.varabyte.kobweb.silk.style.selector.readOnly
import com.varabyte.kobweb.silk.style.selector.readWrite
import com.varabyte.kobweb.silk.style.selector.required
import com.varabyte.kobweb.silk.style.selector.root
import com.varabyte.kobweb.silk.style.selector.selection
import com.varabyte.kobweb.silk.style.selector.target
import com.varabyte.kobweb.silk.style.selector.userInvalid
import com.varabyte.kobweb.silk.style.selector.userValid
import com.varabyte.kobweb.silk.style.selector.valid
import com.varabyte.kobweb.silk.style.selector.visited

//region Pseudo classes

//region Location

/**
 * Styles to apply to components that would be matched by both "link" or "visited".
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:any-link">:any-link</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.anyLink`")
val StyleModifiers.anyLink get() = anyLink

/**
 * Styles to apply to components that represent navigation links which have not yet been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:link">:link</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.link`")
val StyleModifiers.link get() = link

/**
 * Styles to apply to elements that are targets of links in the same document.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:target">:target</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.target`")
val StyleModifiers.target get() = target

/**
 * Styles to apply to components that represent navigation links which have previously been visited.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:visited">:visited</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.visited`")
val StyleModifiers.visited get() = visited

//endregion

//region User action

/**
 * Styles to apply to components when a cursor is pointing at them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:hover">:hover</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.hover`")
val StyleModifiers.hover get() = hover

/**
 * Styles to apply to components when a cursor is interacting with them.
 *
 * Be aware that you should use the LVHA order if using link, visited, hover, and/or active pseudo classes.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:active">:active</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.active`")
val StyleModifiers.active get() = active

/**
 * Styles to apply to components when they have focus.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:focus">:focus</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.focus`")
val StyleModifiers.focus get() = focus

/**
 * Styles to apply to components when they have keyboard / a11y-assisted focus.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:focus-visible">:focus-visible</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.focusVisible`")
val StyleModifiers.focusVisible get() = focusVisible

/**
 * Styles to apply to components when they or any descendants have focus.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:focus-within">:focus-within</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.focusWithin`")
val StyleModifiers.focusWithin get() = focusWithin

//endregion

//region Input

/**
 * Matches when an input element has been autofilled by the browser.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:autofill">:autofill</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.autofill`")
val StyleModifiers.autofill get() = autofill

/**
 * Represents a user interface element that is in an enabled state.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:enabled">:enabled</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.enabled`")
val StyleModifiers.enabled get() = enabled

/**
 * Represents a user interface element that is in a disabled state.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:disabled">:disabled</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.disabled`")
val StyleModifiers.disabled get() = disabled

/**
 * Represents any element that cannot be changed by the user.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:read-only">:read-only</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.readOnly`")
val StyleModifiers.readOnly get() = readOnly

/**
 * Represents any element that is user-editable.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:read-write">:read-write</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.readWrite`")
val StyleModifiers.readWrite get() = readWrite

/**
 * Matches an input element that is displaying placeholder text.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:placeholder-shown">:placeholder-shown</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.placeholderShown`")
val StyleModifiers.placeholderShown get() = placeholderShown

/**
 * Matches one or more UI elements that are the default among a set of elements.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:default">:default</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.default`")
val StyleModifiers.default get() = default

/**
 * Matches an element, such as checkboxes and radio buttons, that are checked or toggled to an `on` state.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:checked">:checked</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.checked`")
val StyleModifiers.checked get() = checked

/**
 * Matches when elements, such as checkboxes and radio buttons, are in an indeterminate state.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:indeterminate">:indeterminate</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.indeterminate`")
val StyleModifiers.indeterminate get() = indeterminate

/**
 * Matches an element with valid contents. For example, an input element with type 'email' which contains a validly formed email address.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:valid">:valid</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.valid`")
val StyleModifiers.valid get() = valid

/**
 * Matches an element with invalid contents. For example, an input element with type 'email' with a name entered.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:invalid">:invalid</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.invalid`")
val StyleModifiers.invalid get() = invalid

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is in the allowed range.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:in-range">:in-range</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.inRange`")
val StyleModifiers.inRange get() = inRange

/**
 * Applies to elements with range limitations, for example a slider control, when the selected value is outside the
 * allowed range.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:out-of-range">:out-of-range</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.outOfRange`")
val StyleModifiers.outOfRange get() = outOfRange

/**
 * Matches when a form element is required.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:required">:required</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.required`")
val StyleModifiers.required get() = required

/**
 * Matches when a form element is optional.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:optional">:optional</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.optional`")
val StyleModifiers.optional get() = optional

/**
 * Represents an element with correct input, but only when the user has interacted with it.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:user-valid">:user-valid</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.userValid`")
val StyleModifiers.userValid get() = userValid

/**
 * Represents an element with incorrect input, but only when the user has interacted with it.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:user-invalid">:user-invalid</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.userInvalid`")
val StyleModifiers.userInvalid get() = userInvalid

//endregion

//region Tree

/**
 * Represents an element that is the root of the document. In HTML this is usually the `<html>` element.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:root">:root</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.root`")
val StyleModifiers.root get() = root

/**
 * Represents an element with no children other than white-space characters.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:empty">:empty</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.empty`")
val StyleModifiers.empty get() = empty

/**
 * Matches an element that is the first of its siblings.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:first-child">:first-child</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.firstChild`")
val StyleModifiers.firstChild get() = firstChild

/**
 * Matches an element that is the last of its siblings.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:last-child">:last-child</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.lastChild`")
val StyleModifiers.lastChild get() = lastChild

/**
 * Matches an element that has no siblings. For example, a list item with no other list items in that list.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:only-child">:only-child</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.onlyChild`")
val StyleModifiers.onlyChild get() = onlyChild

/**
 * Matches an element that is the first of its siblings, and also matches a certain type selector.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:first-of-type">:first-of-type</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.firstOfType`")
val StyleModifiers.firstOfType get() = firstOfType

/**
 * Matches an element that is the last of its siblings, and also matches a certain type selector.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:last-of-type">:last-of-type</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.lastOfType`")
val StyleModifiers.lastOfType get() = lastOfType

/**
 * Matches an element that has no siblings of the chosen type selector.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/:only-of-type">:only-of-type</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.onlyOfType`")
val StyleModifiers.onlyOfType get() = onlyOfType

//endregion

//endregion

//region Pseudo elements

/**
 * Styles to apply to a virtual element that is created before the first element in some container.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::before">::before</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.before`")
val StyleModifiers.before get() = before

/**
 * Styles to apply to a virtual element that is created after the last element in some container.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::after">::after</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.after`")
val StyleModifiers.after get() = after

/**
 * Styles to apply to the selected part of a document.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::selection">::selection</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.selection`")
val StyleModifiers.selection get() = selection

/**
 * Styles to apply to the first letter in a block of text.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::first-letter">::first-letter</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.firstLetter`")
val StyleModifiers.firstLetter get() = firstLetter

/**
 * Styles to apply to the first line in a block of text.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::first-line">::first-line</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.firstLine`")
val StyleModifiers.firstLine get() = firstLine

/**
 * Matches the placeholder text within an input element that is displaying placeholder text.
 *
 * Note that if you override the color of the placeholder, you should consider setting its `opacity` to `1`.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/::placeholder">::placeholder</a>
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.placeholder`")
val StyleModifiers.placeholder get() = placeholder

//endregion

// region Media queries

/**
 * Used to indicate styles which should only be applied when the page is being printed.
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.mediaPrint`")
val StyleModifiers.mediaPrint get() = mediaPrint

//endregion

// region Functional pseudo classes

@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.not`")
fun StyleModifiers.not(vararg params: CssRule.NonMediaCssRule) = not(*params)

//endregion

//region Aria attributes

/**
 * A way to select elements that have been tagged with an `aria-disabled` attribute.
 *
 * This is different from the `:disabled` pseudo-class selector! There are various reasons to use the ARIA version over
 * the HTML version; for example, some elements don't support `disabled` and also `disabled` elements don't fire
 * mouse events, which can be useful e.g. when implementing tooltips.
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.ariaDisabled`")
val StyleModifiers.ariaDisabled get() = ariaDisabled

/**
 * A way to select elements that have been tagged with an `aria-invalid` attribute.
 *
 * This is different from the `:invalid` pseudo-class selector! The `invalid` pseudo-class is useful when the standard
 * widget supports a general invalidation algorithm (like an email type input with an invalid email address), but the
 * `ariaInvalid` version can be used to support custom invalidation strategies.
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.ariaInvalid`")
val StyleModifiers.ariaInvalid get() = ariaInvalid

/**
 * A way to select elements that have been tagged with an `aria-required` attribute.
 *
 * This is different from the `:required` pseudo-class selector! It can be useful to use the `ariaRequired` tag when
 * using elements that don't support the `required` attribute, like elements created from divs, as a way to communicate
 * their required state to accessibility readers.
 */
@Deprecated("Please change your import to `com.varabyte.kobweb.silk.style.selector.ariaRequired`")
val StyleModifiers.ariaRequired get() = ariaRequired

//endregion
