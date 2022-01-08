@file:Suppress("UNUSED_VARIABLE", "unused")

package com.larsreimann.modeling

import com.larsreimann.modeling.assertions.shouldBeReleased
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.sequences.shouldBeEmpty
import org.junit.jupiter.api.Test

class ModelNodeTest {

    @Test
    fun `isRoot() should be true by default`() {
        ModelNode().isRoot().shouldBeTrue()
    }

    @Test
    fun `isRoot() should indicate whether the node has a parent`() {
        val innerNode = ModelNode()
        val root = object : ModelNode() {
            val child = ContainmentReference(innerNode)
        }

        innerNode.isRoot().shouldBeFalse()
        root.isRoot().shouldBeTrue()
    }

    @Test
    fun `children() should be empty by default`() {
        ModelNode().children().shouldBeEmpty()
    }

    @Test
    fun `release() should set parent and container to null`() {
        val innerNode = ModelNode()
        val root = object : ModelNode() {
            val child = ContainmentReference(innerNode)
        }

        innerNode.release()

        innerNode.shouldBeReleased()
    }
}
