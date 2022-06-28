package com.larsreimann.modeling.util

open class NamedNode(private val name: String) : TestNode() {
    override fun toString(): String {
        return name
    }
}
