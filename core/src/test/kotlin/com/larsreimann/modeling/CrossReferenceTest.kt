@file:Suppress("unused")

package com.larsreimann.modeling

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.sequences.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CrossReferenceTest {

    private class Root(child: ModelNode) : ModelNode() {
        var child by ContainmentReference(child)
    }

    private lateinit var innerNode: ModelNode
    private lateinit var someOtherInnerNode: ModelNode
    private lateinit var root: Root
    private lateinit var crossReference: ModelNode.CrossReference<ModelNode>

    @BeforeEach
    fun resetTestData() {
        innerNode = ModelNode()
        someOtherInnerNode = ModelNode()
        root = Root(innerNode)
        crossReference = ModelNode.CrossReference(innerNode)
    }

    @Test
    fun `setter should register cross-reference on new node`() {
        crossReference.node = someOtherInnerNode
        someOtherInnerNode.crossReferences().shouldContainExactly(crossReference)
    }

    @Test
    fun `setter should update the value`() {
        crossReference.node = someOtherInnerNode
        crossReference.node shouldBe someOtherInnerNode
    }

    @Test
    fun `setter should deregister cross-reference on old node`() {
        crossReference.node = someOtherInnerNode
        innerNode.crossReferences().shouldBeEmpty()
    }

    @Test
    fun `onMove should be called when the node is moved`() {
        var wasCalled = false
        crossReference = ModelNode.CrossReference(innerNode) { _, _ -> wasCalled = true }
        innerNode.release()

        wasCalled.shouldBeTrue()
    }

    @Test
    fun `onMove should not called when the node was not moved`() {
        var wasCalled = false
        crossReference = ModelNode.CrossReference(someOtherInnerNode) { _, _ -> wasCalled = true }
        someOtherInnerNode.release()

        wasCalled.shouldBeFalse()
    }
}
