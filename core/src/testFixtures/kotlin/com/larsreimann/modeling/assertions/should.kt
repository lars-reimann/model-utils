package com.larsreimann.modeling.assertions

import com.larsreimann.modeling.Node
import io.kotest.matchers.should

/**
 * Succeeds iff parent and container of this node are `null`.
 */
fun Node.shouldBeReleased(): Node {
    this should beReleased()
    return this
}

/**
 * Succeeds iff parent and container of this node are equal to the given values.
 */
fun Node.shouldBeLocatedAt(parent: Node, container: Node.Container<*>): Node {
    this should beLocatedAt(parent, container)
    return this
}
