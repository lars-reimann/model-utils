package com.larsreimann.modeling.util

import com.larsreimann.modeling.ModelNode

open class TestNode : ModelNode() {
    override fun children() = emptySequence<ModelNode>()
}
