package toRemove

import toRemove.AnnotationTarget.CLASS
import toRemove.AnnotationTarget.CONSTRUCTOR_PARAMETER
import toRemove.AnnotationTarget.FUNCTION_PARAMETER
import toRemove.AnnotationTarget.GLOBAL_FUNCTION
import toRemove.AnnotationTarget.METHOD

sealed class EditorAnnotation {
    protected abstract val validTargets: Set<AnnotationTarget>

    val type: String
        get() {
            return this::class.simpleName?.removeSuffix("Annotation") ?: ""
        }

    fun isApplicableTo(target: AnnotationTarget) = target in validTargets
}

data class AttributeAnnotation(val defaultValue: DefaultValue) : EditorAnnotation() {

    @Transient
    override val validTargets = setOf(CONSTRUCTOR_PARAMETER)
}

data class BoundaryAnnotation(
    val isDiscrete: Boolean,
    val lowerIntervalLimit: Double,
    val lowerLimitType: ComparisonOperator,
    val upperIntervalLimit: Double,
    val upperLimitType: ComparisonOperator
) : EditorAnnotation() {

    @Transient
    override val validTargets = PARAMETERS
}

enum class ComparisonOperator {
    LESS_THAN_OR_EQUALS,
    LESS_THAN,
    UNRESTRICTED,
}

data class CalledAfterAnnotation(val calledAfterName: String) : EditorAnnotation() {

    @Transient
    override val validTargets = FUNCTIONS
}

data class ConstantAnnotation(val defaultValue: DefaultValue) : EditorAnnotation() {

    @Transient
    override val validTargets = PARAMETERS
}

data class EnumAnnotation(val enumName: String, val pairs: List<EnumPair>) : EditorAnnotation() {

    @Transient
    override val validTargets = PARAMETERS
}

data class EnumPair(val stringValue: String, val instanceName: String)

data class GroupAnnotation(val groupName: String, val parameters: List<String>) : EditorAnnotation() {

    @Transient
    override val validTargets = FUNCTIONS
}

data class MoveAnnotation(val destination: String) : EditorAnnotation() {

    @Transient
    override val validTargets = GLOBAL_DECLARATIONS
}

data class OptionalAnnotation(val defaultValue: DefaultValue) : EditorAnnotation() {

    @Transient
    override val validTargets = PARAMETERS
}

object PureAnnotation : EditorAnnotation() {

    @Transient
    override val validTargets = FUNCTIONS
}

data class RenameAnnotation(val newName: String) : EditorAnnotation() {

    @Transient
    override val validTargets = CLASSES.union(FUNCTIONS).union(PARAMETERS)
}

object RequiredAnnotation : EditorAnnotation() {

    @Transient
    override val validTargets = PARAMETERS
}

object UnusedAnnotation : EditorAnnotation() {

    @Transient
    override val validTargets = setOf(
        CLASS,
        GLOBAL_FUNCTION, METHOD
    )
}

sealed class DefaultValue

class DefaultBoolean(val value: Boolean) : DefaultValue() {
    override fun toString(): String {
        return "$value"
    }
}

class DefaultNumber(val value: Double) : DefaultValue() {
    override fun toString(): String {
        return "$value"
    }
}

class DefaultString(val value: String) : DefaultValue() {
    override fun toString(): String {
        return "'$value'"
    }
}

enum class AnnotationTarget(private val target: String) {
    CLASS("class"),
    GLOBAL_FUNCTION("global function"),
    METHOD("method"),
    CONSTRUCTOR_PARAMETER("constructor parameter"),
    FUNCTION_PARAMETER("function parameter");

    override fun toString(): String {
        return target
    }
}

val GLOBAL_DECLARATIONS = setOf(CLASS, GLOBAL_FUNCTION)
val CLASSES = setOf(CLASS)
val FUNCTIONS = setOf(GLOBAL_FUNCTION, METHOD)
val PARAMETERS = setOf(
    CONSTRUCTOR_PARAMETER,
    FUNCTION_PARAMETER
)
