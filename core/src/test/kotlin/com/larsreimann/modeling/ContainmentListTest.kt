package com.larsreimann.modeling

import com.larsreimann.modeling.assertions.shouldBeLocatedAt
import com.larsreimann.modeling.util.NamedNode
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ContainmentListTest {

    private class Root(children: List<ModelNode>) : NamedNode("root") {
        val children = ContainmentList(children)
    }

    private lateinit var innerNode: ModelNode
    private lateinit var root: Root

    @BeforeEach
    fun resetTestData() {
        innerNode = NamedNode("innerNode")
        root = Root(listOf(innerNode))
    }

    @Test
    fun `constructor should correctly link initial values`() {
        innerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode)
    }

    @Test
    fun `releaseNode should throw if a node is passed that is referenced`() {
        shouldThrow<IllegalStateException> {
            root.children.releaseNode(innerNode)
        }
    }

    @Test
    fun `releaseNode should do nothing if a node is passed that is not referenced`() {
        shouldNotThrow<IllegalStateException> {
            root.children.releaseNode(root)
        }

        innerNode.shouldBeLocatedAt(root, root.children)
        root.children.shouldContainExactly(innerNode)
    }
}
