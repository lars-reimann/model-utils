package com.larsreimann.modeling

/**
 * Returns the root of the tree that contains this [ModelNode]. This may be this [ModelNode] itself.
 */
fun ModelNode.root(): ModelNode {
    return parent?.root() ?: this
}

/**
 * Returns the ancestors of this [ModelNode] starting with its parent (if we are not at the root already) and then
 * walking up the tree to the root.
 */
fun ModelNode.ancestors(): Sequence<ModelNode> {
    return sequence {
        var current = parent
        while (current != null) {
            yield(current)
            current = current.parent
        }
    }
}

/**
 * Returns this [ModelNode] and its ancestors starting at this [ModelNode] and then walking up the tree to the root.
 */
fun ModelNode.ancestorsOrSelf(): Sequence<ModelNode> {
    return sequence {
        yield(this@ancestorsOrSelf)
        yieldAll(ancestors())
    }
}

/**
 * Returns the siblings of this [ModelNode], i.e. the children of its parent excluding this [ModelNode]. The
 * siblings are ordered like the children of the parent.
 */
fun ModelNode.siblings(): Sequence<ModelNode> {
    return parent?.children()?.filterNot { it == this } ?: emptySequence()
}

/**
 * Returns the children of the parent of this [ModelNode], including this [ModelNode]. The elements are ordered like
 * the children of the parent.
 */
fun ModelNode.siblingsOrSelf(): Sequence<ModelNode> {
    return parent?.children() ?: emptySequence()
}

/**
 * Defines the order in which descendants are traversed by [descendants] or [descendantsOrSelf].
 */
enum class Traversal {

    /**
     * A parent is listed before any of its children.
     */
    PREORDER,

    /**
     * A parent is listed after all its children.
     */
    POSTORDER
}

/**
 * Returns the descendants of this [ModelNode] using either preorder and postorder traversal.
 *
 * @param order
 * The traversal order. Preorder means a parent is listed before any of its children and postorder means a parent is
 * listed after all its children.
 *
 * @param shouldPrune
 * Whether the subtree should be pruned. If this function returns true for a concept neither the concept itself nor
 * any of its descendants will be traversed.
 */
fun ModelNode.descendants(
    order: Traversal = Traversal.PREORDER,
    shouldPrune: (ModelNode) -> Boolean = { false }
): Sequence<ModelNode> {

    // Prevent children from being traversed if this concept should be pruned
    if (shouldPrune(this)) {
        return emptySequence()
    }

    return sequence {
        for (child in children()) {

            // We must prune again here; otherwise the child would be yielded unchecked
            if (shouldPrune(child)) {
                continue
            }

            if (order == Traversal.PREORDER) {
                yield(child)
            }
            yieldAll(child.descendants(order, shouldPrune))
            if (order == Traversal.POSTORDER) {
                yield(child)
            }
        }
    }
}

/**
 * Returns this [ModelNode] and its descendants using either preorder and postorder traversal.
 *
 * @param order
 * The traversal order. Preorder means a parent is listed before any of its children and postorder means a parent is
 * listed after all its children.
 *
 * @param shouldPrune
 * Whether the subtree should be pruned. If this function returns true for a concept neither the concept itself nor
 * any of its descendants will be traversed.
 */
fun ModelNode.descendantsOrSelf(
    order: Traversal = Traversal.PREORDER,
    shouldPrune: (ModelNode) -> Boolean = { false }
): Sequence<ModelNode> {
    if (shouldPrune(this)) {
        return emptySequence()
    }

    return sequence {
        if (order == Traversal.PREORDER) {
            yield(this@descendantsOrSelf)
        }
        yieldAll(descendants(order, shouldPrune))
        if (order == Traversal.POSTORDER) {
            yield(this@descendantsOrSelf)
        }
    }
}

/**
 * Returns this [ModelNode] or its nearest ancestor with the specified type.
 */
inline fun <reified T> ModelNode.closest(): T? {
    return ancestorsOrSelf().firstOrNull { it is T } as T?
}
