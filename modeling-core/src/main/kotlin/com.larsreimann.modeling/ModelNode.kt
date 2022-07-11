package com.larsreimann.modeling

import kotlin.reflect.KProperty

/**
 * A node in a tree. It has references to its parent and children and supports cross-references. The node can be moved
 * around in the tree, while those references are updated in the background.
 */
abstract class ModelNode : Traversable {

    /**
     * Parent and container of this node in the tree.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val location: Location
        get() = Location(parent, container)

    /**
     * The parent of this node in the tree.
     */
    override val parent: ModelNode?
        get() = container?.parent

    /**
     * The container of this node in the tree.
     */
    var container: Container<*>? = null
        private set

    /**
     * Cross-references to this node. They get notified whenever this node is moved.
     */
    private val crossReferencesToThis = mutableListOf<CrossReference<*>>()

    /**
     * Cross-references to this node. They get notified whenever this node is moved.
     */
    fun crossReferencesToThis() = crossReferencesToThis.toList().asSequence()

    /**
     * Releases the subtree that has this node as root.
     */
    fun release() {
        this.container?.releaseNode(this)
    }

    /**
     * Sets parent and container of this node in the tree.
     */
    private fun move(newParent: ModelNode?, newContainer: Container<*>?) {
        val oldLocation = this.location
        val newLocation = Location(newParent, newContainer)
        this.container = newContainer

        crossReferencesToThis.forEach { it.onMove(oldLocation, newLocation) }
    }

    /**
     * A container for [ModelNode]s of the given type.
     */
    sealed class Container<T : ModelNode> {

        /**
         * The [ModelNode] that includes this container.
         */
        abstract val parent: ModelNode

        /**
         * The [ModelNode]s within this container.
         */
        abstract fun children(): Sequence<T>

        /**
         * Releases the subtree that has this node as root. If this container does not contain the node nothing should
         * happen. Otherwise, the following links need to be removed:
         *   - From the container to the node
         *   - From the node to its parent
         *   - From the node to its container
         */
        internal abstract fun releaseNode(node: ModelNode)

        /**
         * Sets parent and container properties of the node to `null`. This method can be called without causing cyclic
         * updates.
         */
        protected fun nullifyUplinks(node: ModelNode?) {
            node?.move(newParent = null, newContainer = null)
        }

        /**
         * Sets parent and container properties of the node to this container. This method can be called without causing
         * cyclic updates.
         */
        protected fun ModelNode?.pointUplinksToThisContainer(node: ModelNode?) {
            node?.move(newParent = this@pointUplinksToThisContainer, newContainer = this@Container)
        }
    }

    /**
     * Stores a reference to a [ModelNode] and keeps uplinks (parent/container) and downlinks (container to node) updated
     * on mutation.
     *
     * **Samples:**
     *
     * _Normal assignment:_
     * ```kt
     * object Root: Node() {
     *     private val child = ContainmentReference(Node())
     *
     *     fun get(): Node? {
     *         return child.node
     *     }
     *
     *     fun set(newNode: Node?) {
     *         child.node = newNode
     *     }
     * }
     * ```
     *
     * _Mutable delegate:_
     * ```kt
     * object Root: Node() {
     *     private var child by ContainmentReference(Node())
     *
     *     fun get(): Node? {
     *         return child
     *     }
     *
     *     fun set(newNode: Node?) {
     *         child = newNode
     *     }
     * }
     * ```
     *
     * _Immutable delegate:_
     * ```kt
     * object Root: Node() {
     *     private val child by ContainmentReference(Node())
     *
     *     fun get(): Node? {
     *         return child
     *     }
     *
     *     fun set(newNode: Node?) {
     *         // Not possible
     *     }
     * }
     * ```
     *
     * @param node The initial value.
     */
    inner class ContainmentReference<T : ModelNode>(node: T?) : Container<T>() {

        /**
         * The node that is currently contained or `null`.
         */
        var node: T? = null
            set(value) {

                // Prevents infinite recursion when releasing the new value
                if (field == value) {
                    return
                }

                // Release old value
                nullifyUplinks(field)
                field = null

                // Release new value
                value?.release()

                // Store new value in this container
                pointUplinksToThisContainer(value)
                field = value
            }

        init {
            this.node = node
        }

        override val parent = this@ModelNode

        override fun children() = sequence {
            node?.let { yield(it) }
        }

        override fun releaseNode(node: ModelNode) {
            if (this.node == node) {
                this.node = null
            }
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return this.node
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, newNode: T?) {
            this.node = newNode
        }
    }

    /**
     * Stores a list of references to [ModelNode]s.
     *
     * @param nodes The initial nodes.
     */
    inner class ContainmentList<T : ModelNode> private constructor(
        nodes: Collection<T>,
        private val delegate: MutableList<T>
    ) : Container<T>(), List<T> by delegate {

        constructor(nodes: Collection<T> = emptyList()) : this(nodes, mutableListOf())

        init {
            nodes.forEach {
                it.release()
                pointUplinksToThisContainer(it)
            }
            delegate.addAll(nodes)
        }

        override val parent = this@ModelNode

        override fun children() = sequence {
            yieldAll(delegate)
        }

        override fun releaseNode(node: ModelNode) {
            if (node in delegate) {
                throw IllegalStateException("Node is contained in an immutable list and cannot be released.")
            }
        }
    }

    /**
     * Stores a list of references to [ModelNode]s and keeps uplinks (parent/container) and downlinks (container to node)
     * updated on mutation.
     *
     * @param nodes The initial nodes.
     */
    inner class MutableContainmentList<T : ModelNode> private constructor(
        nodes: Collection<T>,
        private val delegate: MutableList<T>
    ) : Container<T>(), MutableList<T> by delegate {

        constructor(nodes: Collection<T> = emptyList()) : this(nodes, mutableListOf())

        init {
            addAll(nodes)
        }

        override val parent = this@ModelNode

        override fun children() = sequence {
            yieldAll(delegate)
        }

        override fun releaseNode(node: ModelNode) {
            this.remove(node)
        }

        override fun add(element: T): Boolean {
            element.release()
            pointUplinksToThisContainer(element)
            return delegate.add(element)
        }

        override fun add(index: Int, element: T) {
            element.release()
            pointUplinksToThisContainer(element)
            delegate.add(index, element)
        }

        override fun addAll(elements: Collection<T>): Boolean {
            elements.forEach {
                it.release()
                pointUplinksToThisContainer(it)
            }
            return delegate.addAll(elements)
        }

        override fun addAll(index: Int, elements: Collection<T>): Boolean {
            elements.forEach {
                it.release()
                pointUplinksToThisContainer(it)
            }
            return delegate.addAll(index, elements)
        }

        override fun remove(element: T): Boolean {
            val wasRemoved = delegate.remove(element)
            if (wasRemoved) {
                nullifyUplinks(element)
            }
            return wasRemoved
        }

        override fun removeAt(index: Int): T {
            val removedElement = delegate.removeAt(index)
            nullifyUplinks(removedElement)
            return removedElement
        }

        override fun removeAll(elements: Collection<T>): Boolean {
            return elements.fold(false) { accumulator, element ->
                accumulator || remove(element)
            }
        }

        override fun retainAll(elements: Collection<T>): Boolean {
            val toRemove = subtract(elements.toSet())
            return removeAll(toRemove)
        }

        override fun clear() {
            forEach { nullifyUplinks(it) }
            delegate.clear()
        }

        override fun set(index: Int, element: T): T {
            val replacedElement = delegate.set(index, element)
            nullifyUplinks(replacedElement)

            element.release()
            pointUplinksToThisContainer(element)

            return replacedElement
        }
    }

    /**
     * References a [ModelNode] without containing it. Gets notified whenever the [ModelNode] is moved.
     *
     * **Samples:**
     *
     * _Normal assignment:_
     * ```kt
     * object Root: Node() {
     *     private val reference = CrossReference(Node())
     *
     *     fun get(): Node? {
     *         return reference.node
     *     }
     *
     *     fun set(newNode: Node?) {
     *         reference.node = newNode
     *     }
     * }
     * ```
     *
     * _Mutable delegate:_
     * ```kt
     * object Root: Node() {
     *     private var reference by CrossReference(Node())
     *
     *     fun get(): Node? {
     *         return reference
     *     }
     *
     *     fun set(newNode: Node?) {
     *         reference = newNode
     *     }
     * }
     * ```
     *
     * _Immutable delegate:_
     * ```kt
     * object Root: Node() {
     *     private val reference by CrossReference(Node())
     *
     *     fun get(): Node? {
     *         return reference
     *     }
     *
     *     fun set(newNode: Node?) {
     *         // Not possible
     *     }
     * }
     * ```
     *
     * @param node The initial value.
     */
    inner class CrossReference<T : ModelNode>(
        node: T?,
        var handleMove: CrossReference<T>.(from: Location, to: Location) -> Unit = { _, _ -> }
    ) {

        /**
         * The node that is currently referenced or `null`.
         */
        var node: T? = null
            set(value) {
                if (field == value) {
                    return
                }

                field?.crossReferencesToThis?.remove(this)
                field = value
                value?.crossReferencesToThis?.add(this)
            }

        init {
            this.node = node
        }

        /**
         * The [ModelNode] that includes this cross-reference.
         */
        val parent = this@ModelNode

        internal fun onMove(from: Location, to: Location) {
            handleMove(from, to)
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return this.node
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, newNode: T?) {
            this.node = newNode
        }
    }

    data class Location(val parent: ModelNode?, val container: Container<*>?)
}
