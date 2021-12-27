package com.larsreimann.modeling.util

import com.larsreimann.modeling.Node

open class NamedNode(private val name: String) : Node() {
    override fun toString(): String {
        return name
    }
}
