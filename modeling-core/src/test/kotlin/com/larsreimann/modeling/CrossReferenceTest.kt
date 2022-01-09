@file:Suppress("unused")

package com.larsreimann.modeling

import com.larsreimann.modeling.util.NamedNode
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CrossReferenceTest {

    private class Root(child: ModelNode, crossReference: ModelNode) : NamedNode("root") {
        var child by ContainmentReference(child)
        var crossReference = CrossReference(crossReference)
    }

    private lateinit var innerNode: ModelNode
    private lateinit var someOtherInnerNode: ModelNode
    private lateinit var root: Root

    @BeforeEach
    fun resetTestData() {
        innerNode = NamedNode("innerNode")
        someOtherInnerNode = NamedNode("someOtherInnerNode")
        root = Root(innerNode, innerNode)
    }

    @Test
    fun `should store parent`() {
        root.crossReference.parent shouldBe root
    }

    @Test
    fun `setter should register cross-reference on new node`() {
        root.crossReference.node = someOtherInnerNode
        someOtherInnerNode.crossReferences().toList().shouldContainExactly(root.crossReference)
    }

    @Test
    fun `setter should update the value`() {
        root.crossReference.node = someOtherInnerNode
        root.crossReference.node shouldBe someOtherInnerNode
    }

    @Test
    fun `setter should deregister cross-reference on old node`() {
        root.crossReference.node = someOtherInnerNode
        innerNode.crossReferences().shouldBeEmpty()
    }

    @Test
    fun `onMove should be called when the node is moved`() {
        var wasCalled = false
        root.crossReference.handleMove = { _, _ -> wasCalled = true }
        innerNode.release()

        wasCalled.shouldBeTrue()
    }

    @Test
    fun `onMove should not called when the node was not moved`() {
        var wasCalled = false
        root.crossReference.handleMove = { _, _ -> wasCalled = true }
        someOtherInnerNode.release()

        wasCalled.shouldBeFalse()
    }
}
