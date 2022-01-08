package com.larsreimann.modeling.assertions

import com.larsreimann.modeling.ModelNode
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

internal fun beReleased() = object : Matcher<ModelNode> {
    override fun test(value: ModelNode): MatcherResult {
        val actual = ModelNode.Location(value.parent, value.container)
        val expected = ModelNode.Location(null, null)

        return ComparableMatcherResult(
            passed = actual == expected,
            failureMessageFn = { "Node should be released." },
            negatedFailureMessageFn = { "Node should not be released."},
            actual = actual.toString(),
            expected = expected.toString()
        )
    }
}

internal fun beLocatedAt(parent: ModelNode, container: ModelNode.Container<*>) = object : Matcher<ModelNode> {
    override fun test(value: ModelNode): MatcherResult {
        val actual = ModelNode.Location(value.parent, value.container)
        val expected = ModelNode.Location(parent, container)

        return ComparableMatcherResult(
            passed = actual == expected,
            failureMessageFn = { "Node should be located at $expected." },
            negatedFailureMessageFn = { "Node should not be located at $expected." },
            actual = actual.toString(),
            expected = expected.toString()
        )
    }
}
