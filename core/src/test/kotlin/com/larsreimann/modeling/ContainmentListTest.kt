package com.larsreimann.modeling

import com.larsreimann.modeling.assertions.shouldBeLocatedAt
import com.larsreimann.modeling.assertions.shouldBeReleased
import com.larsreimann.modeling.util.NamedNode
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ContainmentListTest {

    private class Root(children: List<Node>, someMoreChildren: List<Node>) : NamedNode("root") {
        val children = ContainmentList(children)
        val someMoreChildren = ContainmentList(someMoreChildren)
    }

    private lateinit var innerNode: Node
    private lateinit var someOtherInnerNode: Node
    private lateinit var root: Root

    @BeforeEach
    fun resetTestData() {
        innerNode = NamedNode("innerNode")
        someOtherInnerNode = NamedNode("someOtherInnerNode")
        root = Root(listOf(innerNode), listOf(someOtherInnerNode))
    }

    @Test
    fun `constructor should correctly link initial values`() {
        innerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode)
    }

    @Test
    fun `releaseNode should update links if a node is passed that is referenced`() {
        root.children.releaseNode(innerNode)

        innerNode.shouldBeReleased()
        root.children.shouldBeEmpty()
    }

    @Test
    fun `releaseNode should do nothing if a node is passed that is not referenced`() {
        root.children.releaseNode(someOtherInnerNode)

        innerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode)

        someOtherInnerNode.shouldBeLocatedAt(root, root.someMoreChildren)
        root.someMoreChildren.shouldContainExactly(someOtherInnerNode)
    }

    @Test
    fun `add(T) should update links if a new node is passed`() {
        root.children.add(someOtherInnerNode)

        someOtherInnerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode, someOtherInnerNode)
        root.someMoreChildren.shouldBeEmpty()
    }

    @Test
    fun `add(T) should update links if an existing node is passed`() {
        root.children.add(innerNode)

        innerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode)
    }

    @Test
    fun `add(Int, T) should update links if a new node is passed`() {
        root.children.add(0, someOtherInnerNode)

        someOtherInnerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(someOtherInnerNode, innerNode)
        root.someMoreChildren.shouldBeEmpty()
    }

    @Test
    fun `add(Int, T) should update links if an existing node is passed`() {
        root.children.add(0, innerNode)

        innerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode)
    }

    @Test
    fun `addAll(Collection) should update links if new nodes are passed`() {
        root.children.addAll(listOf(someOtherInnerNode))

        someOtherInnerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode, someOtherInnerNode)
        root.someMoreChildren.shouldBeEmpty()
    }

    @Test
    fun `addAll(Collection) should update links if existing nodes are passed`() {
        root.children.addAll(listOf(innerNode))

        innerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode)
    }

    @Test
    fun `addAll(Int, Collection) should update links if new nodes are passed`() {
        root.children.addAll(0, listOf(someOtherInnerNode))

        someOtherInnerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(someOtherInnerNode, innerNode)
        root.someMoreChildren.shouldBeEmpty()
    }

    @Test
    fun `addAll(Int, Collection) should update links if existing nodes are passed`() {
        root.children.addAll(0, listOf(innerNode))

        innerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode)
    }

    @Test
    fun `remove(T) should update links if a node is removed that is referenced`() {
        root.children.remove(innerNode)

        innerNode.shouldBeReleased()
        root.children.shouldBeEmpty()
    }

    @Test
    fun `remove(T) should do nothing if a node is removed that is not referenced`() {
        root.children.remove(someOtherInnerNode)

        innerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode)

        someOtherInnerNode.shouldBeLocatedAt(root, root.someMoreChildren)
        root.someMoreChildren.shouldContainExactly(someOtherInnerNode)
    }

    @Test
    fun `removeAt(Int) should update links if a node exists at the index`() {
        root.children.removeAt(0)

        innerNode.shouldBeReleased()
        root.children.shouldBeEmpty()
    }

    @Test
    fun `removeAll(Collection) should update links if nodes are removed that are referenced`() {
        root.children.removeAll(listOf(innerNode))

        innerNode.shouldBeReleased()
        root.children.shouldBeEmpty()
    }

    @Test
    fun `removeAll(Collection) should do nothing if nodes are removed that are not referenced`() {
        root.children.removeAll(listOf(someOtherInnerNode))

        innerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode)

        someOtherInnerNode.shouldBeLocatedAt(root, root.someMoreChildren)
        root.someMoreChildren.shouldContainExactly(someOtherInnerNode)
    }

    @Test
    fun `retainAll(Collection) should update links for nodes that are not retained`() {
        root.children.retainAll(listOf())

        innerNode.shouldBeReleased()
        root.children.shouldBeEmpty()
    }

    @Test
    fun `retainAll(Collection) should not change the passed nodes`() {
        root.children.retainAll(listOf(someOtherInnerNode))

        someOtherInnerNode.shouldBeLocatedAt(root, root.someMoreChildren)
        root.someMoreChildren.shouldContainExactly(someOtherInnerNode)
    }

    @Test
    fun `retainAll(Collection) should do nothing for nodes that are retained`() {
        root.children.retainAll(listOf(innerNode))

        innerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode)
    }

    @Test
    fun `clear() should update links for all nodes`() {
        root.children.clear()

        innerNode.shouldBeReleased()
        root.children.shouldBeEmpty()
    }

    @Test
    fun `set(Int, T) should update links if a new node is passed`() {
        root.children[0] = someOtherInnerNode

        innerNode.shouldBeReleased()

        someOtherInnerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(someOtherInnerNode)
    }

    @Test
    fun `set(Int, T) should update links if an existing node is passed`() {
        root.children[0] = innerNode

        innerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode)
    }
}
