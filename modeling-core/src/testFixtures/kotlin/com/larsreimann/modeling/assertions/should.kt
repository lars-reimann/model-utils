package com.larsreimann.modeling.assertions

import com.larsreimann.modeling.ModelNode
import io.kotest.matchers.should

/**
 * Succeeds iff parent and container of this node are `null`.
 */
fun ModelNode.shouldBeReleased(): ModelNode {
    this should beReleased()
    return this
}

/**
 * Succeeds iff parent and container of this node are equal to the given values.
 */
fun ModelNode.shouldBeLocatedAt(parent: ModelNode, container: ModelNode.Container<*>): ModelNode {
    this should beLocatedAt(parent, container)
    return this
}
