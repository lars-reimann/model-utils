package com.larsreimann.modeling

/**
 * Returns the root of the tree that contains this [TreeNode]. This may be this [TreeNode] itself.
 */
fun TreeNode.root(): TreeNode {
    return parent?.root() ?: this
}

/**
 * Returns the ancestors of this [TreeNode] starting with its parent (if we are not at the root already) and then
 * walking up the tree to the root.
 */
fun TreeNode.ancestors(): Sequence<TreeNode> {
    return sequence {
        var current = parent
        while (current != null) {
            yield(current)
            current = current.parent
        }
    }
}

/**
 * Returns this [TreeNode] and its ancestors starting at this [TreeNode] and then walking up the tree to the root.
 */
fun TreeNode.ancestorsOrSelf(): Sequence<TreeNode> {
    return sequence {
        yield(this@ancestorsOrSelf)
        yieldAll(ancestors())
    }
}

/**
 * Returns the siblings of this [TreeNode], i.e. the children of its parent excluding this [TreeNode]. The
 * siblings are ordered like the children of the parent.
 */
fun TreeNode.siblings(): Sequence<TreeNode> {
    return parent?.children()?.filterNot { it == this } ?: emptySequence()
}

/**
 * Returns the children of the parent of this [TreeNode], including this [TreeNode]. The elements are ordered like
 * the children of the parent.
 */
fun TreeNode.siblingsOrSelf(): Sequence<TreeNode> {
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
 * Returns the descendants of this [TreeNode]. We can switch between preorder and postorder traversal.
 *
 * @param order
 * The traversal order. Preorder means a parent is listed before any of its children and postorder means a parent is
 * listed after all its children.
 *
 * @param shouldTraverse
 * Whether the subtree should be traversed. If this function returns false for a concept none of its descendants will be
 * returned.
 */
fun TreeNode.descendants(
    order: Traversal = Traversal.PREORDER,
    shouldTraverse: (TreeNode) -> Boolean = { true }
): Sequence<TreeNode> {

    // Prevent children from being traversed if this concept should be pruned
    if (!shouldTraverse(this)) {
        return emptySequence()
    }

    return sequence {
        for (child in children()) {

            // We must prune again here; otherwise the child would be yielded unchecked
            if (!shouldTraverse(child)) {
                continue
            }

            if (order == Traversal.PREORDER) {
                yield(child)
            }
            yieldAll(child.descendants(order, shouldTraverse))
            if (order == Traversal.POSTORDER) {
                yield(child)
            }
        }
    }
}

/**
 * Returns this [TreeNode] and its descendants. We can switch between preorder and postorder traversal.
 *
 * @param order
 * The traversal order. Preorder means a parent is listed before any of its children and postorder means a parent is
 * listed after all its children.
 *
 * @param shouldTraverse
 * Whether the subtree should be traversed. If this function returns false for a concept neither the concept itself nor
 * any of its descendants will be traversed.
 */
fun TreeNode.descendantsOrSelf(
    order: Traversal = Traversal.PREORDER,
    shouldTraverse: (TreeNode) -> Boolean = { true }
): Sequence<TreeNode> {
    if (!shouldTraverse(this)) {
        return emptySequence()
    }

    return sequence {
        if (order == Traversal.PREORDER) {
            yield(this@descendantsOrSelf)
        }
        yieldAll(descendants(order, shouldTraverse))
        if (order == Traversal.POSTORDER) {
            yield(this@descendantsOrSelf)
        }
    }
}

/**
 * Returns this [TreeNode] or its nearest ancestor with the specified type.
 */
inline fun <reified T> TreeNode.closest(): T? {
    return ancestorsOrSelf().firstOrNull { it is T } as T?
}
