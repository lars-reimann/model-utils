package com.larsreimann.modeling.util

import com.larsreimann.modeling.ModelNode

open class NamedNode(private val name: String) : ModelNode() {
    override fun toString(): String {
        return name
    }
}
