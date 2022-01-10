package com.larsreimann.modeling

import com.larsreimann.modeling.assertions.shouldBeLocatedAt
import com.larsreimann.modeling.assertions.shouldBeReleased
import com.larsreimann.modeling.util.NamedNode
import io.kotest.assertions.throwables.shouldNotThrowUnit
import io.kotest.matchers.concurrent.shouldCompleteWithin
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.sequences.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class ContainmentReferenceTest {

    private class Root(child: ModelNode, someOtherChild: ModelNode) : NamedNode("root") {
        val child = ContainmentReference(child)
        val someOtherChild = ContainmentReference(someOtherChild)
    }

    private lateinit var innerNode: ModelNode
    private lateinit var someOtherInnerNode: ModelNode
    private lateinit var root: Root

    @BeforeEach
    fun resetTestData() {
        innerNode = NamedNode("innerNode")
        someOtherInnerNode = NamedNode("someOtherInnerNode")
        root = Root(innerNode, someOtherInnerNode)
    }

    @Test
    fun `constructor should correctly link initial value`() {
        innerNode.shouldBeLocatedAt(root, root.child)
        root.child.node shouldBe innerNode
    }

    @Test
    fun `should store parent`() {
        root.child.parent shouldBe root
    }

    @Test
    fun `children should list child if it is not null`() {
        root.child.children().shouldContainExactly(innerNode)
    }

    @Test
    fun `children should not list child if it is null`() {
        root.child.node = null
        root.child.children().shouldBeEmpty()
    }

    @Test
    fun `setter should work if a new node is passed`() {
        root.child.node = root.someOtherChild.node

        innerNode.shouldBeReleased()
        root.child.node shouldBe someOtherInnerNode

        someOtherInnerNode.shouldBeLocatedAt(root, root.child)
        root.someOtherChild.node.shouldBeNull()
    }

    @Test
    fun `setter should work if null is passed`() {
        root.child.node = null

        innerNode.shouldBeReleased()
        root.child.node.shouldBeNull()
    }

    @Test
    fun `setter should not recurse infinitely if the same value is passed`() {
        shouldCompleteWithin(1, TimeUnit.SECONDS) {
            shouldNotThrowUnit<StackOverflowError> {
                root.child.node = root.child.node
            }
        }
    }

    @Test
    fun `releaseNode should remove links if a node is passed that is referenced`() {
        root.child.releaseNode(innerNode)

        innerNode.shouldBeReleased()
        root.child.node.shouldBeNull()
    }

    @Test
    fun `releaseNode should do nothing if a node is passed that is not referenced`() {
        root.child.releaseNode(someOtherInnerNode)

        innerNode.shouldBeLocatedAt(root, root.child)
        root.child.node shouldBe innerNode

        someOtherInnerNode.shouldBeLocatedAt(root, root.someOtherChild)
        root.someOtherChild.node shouldBe someOtherInnerNode
    }
}
