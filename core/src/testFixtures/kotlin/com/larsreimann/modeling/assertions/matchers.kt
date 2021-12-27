package com.larsreimann.modeling.assertions

import com.larsreimann.modeling.Node
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

internal fun beReleased() = object : Matcher<Node> {
    override fun test(value: Node): MatcherResult {
        val actual = Node.Location(value.parent, value.container)
        val expected = Node.Location(null, null)

        return ComparableMatcherResult(
            passed = actual == expected,
            failureMessageFn = { "Node should be released." },
            negatedFailureMessageFn = { "Node should not be released."},
            actual = actual.toString(),
            expected = expected.toString()
        )
    }
}

internal fun beLocatedAt(parent: Node, container: Node.Container<*>) = object : Matcher<Node> {
    override fun test(value: Node): MatcherResult {
        val actual = Node.Location(value.parent, value.container)
        val expected = Node.Location(parent, container)

        return ComparableMatcherResult(
            passed = actual == expected,
            failureMessageFn = { "Node should be located at $expected." },
            negatedFailureMessageFn = { "Node should not be located at $expected." },
            actual = actual.toString(),
            expected = expected.toString()
        )
    }
}
