package com.larsreimann.modeling

/**
 * An object with references to its parent and children.
 */
interface Traversable {

    /**
     * The parent of this object.
     */
    val parent: Traversable?

    /**
     * Returns `true` iff this object has no parent.
     */
    fun isRoot(): Boolean {
        return parent == null
    }

    /**
     * The children of this object.
     */
    fun children(): Sequence<Traversable>
}
