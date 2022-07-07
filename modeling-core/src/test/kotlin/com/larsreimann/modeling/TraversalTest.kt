package com.larsreimann.modeling

import com.larsreimann.modeling.util.NamedNode
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TraversalTest {

    private class Root(children: List<ModelNode>) : NamedNode("root") {
        val children = MutableContainmentList(children)

        override fun children() = sequence {
            yieldAll(children)
        }
    }

    private class InnerNode(child: ModelNode) : NamedNode("innerNode") {
        val child by ContainmentReference(child)

        override fun children() = sequence {
            child?.let { yield(it) }
        }
    }

    private lateinit var leaf1: ModelNode
    private lateinit var leaf2: ModelNode
    private lateinit var leaf3: ModelNode
    private lateinit var innerNode: InnerNode
    private lateinit var root: Root

    @BeforeEach
    fun resetTestData() {
        leaf1 = NamedNode("leaf1")
        leaf2 = NamedNode("leaf2")
        leaf3 = NamedNode("leaf3")
        innerNode = InnerNode(leaf1)
        root = Root(listOf(innerNode, leaf2, leaf3))
    }

    @Test
    fun `root() should return the node itself for the root`() {
        root.root() shouldBe root
    }

    @Test
    fun `root() should return the root for an inner node`() {
        leaf1.root() shouldBe root
    }

    @Test
    fun `ancestor() should return all nodes along the path to the root`() {
        leaf1.ancestors().toList().shouldContainExactly(innerNode, root)
    }

    @Test
    fun `ancestorOrSelf() should return the node and all nodes along the path to the root`() {
        leaf1.ancestorsOrSelf().toList().shouldContainExactly(leaf1, innerNode, root)
    }

    @Test
    fun `siblings() should return the siblings of the node`() {
        innerNode.siblings().toList().shouldContainExactly(leaf2, leaf3)
    }

    @Test
    fun `siblingsOrSelf() should return all children of the parent`() {
        innerNode.siblingsOrSelf().toList().shouldContainExactly(innerNode, leaf2, leaf3)
    }

    @Test
    fun `descendant(PREORDER) should return all nodes below of the node in preorder`() {
        root.descendants(Traversal.PREORDER).toList().shouldContainExactly(innerNode, leaf1, leaf2, leaf3)
    }

    @Test
    fun `descendant(POSTORDER) should return all nodes below of the node in postorder`() {
        root.descendants(Traversal.POSTORDER).toList().shouldContainExactly(leaf1, innerNode, leaf2, leaf3)
    }

    @Test
    fun `descendant() should prune nodes if a filter is passed`() {
        root.descendants { it is InnerNode }.toList().shouldContainExactly(leaf2, leaf3)
    }

    @Test
    fun `descendantOrSelf(PREORDER) should return the node and all nodes below it in preorder`() {
        root.descendantsOrSelf(Traversal.PREORDER).toList().shouldContainExactly(root, innerNode, leaf1, leaf2, leaf3)
    }

    @Test
    fun `descendantOrSelf(POSTORDER) should return the node and all nodes below it in postorder`() {
        root.descendantsOrSelf(Traversal.POSTORDER).toList().shouldContainExactly(leaf1, innerNode, leaf2, leaf3, root)
    }

    @Test
    fun `descendantOrSelf() should prune nodes if a filter is passed`() {
        root.descendantsOrSelf { it is InnerNode }.toList().shouldContainExactly(root, leaf2, leaf3)
    }

    @Test
    fun `closest() should return the node itself if has the correct type`() {
        innerNode.closest<InnerNode>() shouldBe innerNode
    }

    @Test
    fun `closest() should return the first node with the correct type along the path to the root`() {
        leaf1.closest<InnerNode>() shouldBe innerNode
    }
}
