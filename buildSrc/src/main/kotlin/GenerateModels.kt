import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.lang.Error

/**
 * Type of the entity.
 * Entities and services are grouped into "apps" (like "sys" or "tutanota") which are namespaces
 * with their own versioning and migrations.
 */
enum class MetamodelType {
    /**
     * Instance (separate database entity) which does not belong to any particular list. Has a
     * single ID - [GeneratedId] or [CustomId].
     */
    ELEMENT_TYPE,

    /**
     * Instance (separate database entity) which belongs to one of the lists. It means that it's
     * Id is [IdTuple], it can be loaded in ranges.
     */
    LIST_ELEMENT_TYPE,

    /**
     * Not a separate database entity but a structure on Instance or DataTransferType. Has it's
     * own id.
     */
    AGGREGATED_TYPE,

    /**
     * Entity which is not represented in the database but is used for sending/receiving data via
     * services.
     */
    DATA_TRANSFER_TYPE;
}

enum class ValueType {
    BooleanType,
    NumberType,
    StringType,
    DateType,
    GeneratedIdType,
    CustomIdType,
    BytesType,
    CompressedStringType;
}

enum class AssociationType {
    /**
     * Id referencing ElementEntity
     */
    ELEMENT_ASSOCIATION,

    /**
     * IdTuple referencing ListElementEntity
     */
    LIST_ELEMENT_ASSOCIATION,

    /**
     * Id referencing the whole list (listId from [IdTuple])
     */
    LIST_ASSOCIATION,

    /**
     * Aggregation.
     * See [MetamodelType.AGGREGATED_TYPE].
     */
    AGGREGATION;
}

enum class Cardinality {
    /**
     * Single value. Must be present.
     */
    One,

    /**
     * Single value. May be absent.
     */
    ZeroOrOne,

    /**
     * List of values. May be empty.
     */
    Any;
}

/**
 * Value descriptor in TypeModel.
 */
data class Value(
    val type: ValueType,
    val cardinality: Cardinality,
    val encrypted: Boolean,
    val final: Boolean
)

/**
 * Association description in TypeModel.
 */
data class Association(
    val type: AssociationType,
    val cardinality: Cardinality,
    val refType: String,
    val final: Boolean,
    /**
     * If it belongs to another app
     */
    val external: Boolean
)

/**
 * Description of the entity structure and type.
 */
data class TypeModel(
    val name: String,
    val encrypted: Boolean,
    val type: MetamodelType,
    val id: Long,
    val rootId: String,
    val values: Map<String, Value> = mapOf(),
    val associations: Map<String, Association> = mapOf(),
    val version: Int
)


val idClassName = ClassName.bestGuess("com.charlag.tuta.entities.Id")
val idTupleClassName = ClassName.bestGuess("com.charlag.tuta.entities.IdTuple")

fun valueToType(type: Map<String, Any>, value: Map<String, Any>): TypeName {
    val valueName = value["name"] as String
    val valueType = value["type"]
    val baseType = if (valueName == "_id") {
        if (type["type"] == "LIST_ELEMENT_TYPE") {
            idTupleClassName
        } else {
            idClassName
        }
    } else {
        when (valueType) {
            "Number" -> LONG
            "Bytes" -> BYTE_ARRAY
            "CustomId" -> idClassName
            "String" -> STRING
            "Date" -> ClassName.bestGuess("com.charlag.tuta.entities.Date")
            "GeneratedId" -> idClassName
            "Boolean" -> BOOLEAN
            "CompressedString" -> STRING
            else -> throw Error("idk what is $valueType")
        }
    }
    return if (valueName[0] == '_' || value["cardinality"] == "ZeroOrOne") {
        baseType.copy(nullable = true)
    } else {
        baseType
    }
}

open class GenerateModels : DefaultTask() {
    @get:InputFile
    lateinit var modelsFile: File

    @get:OutputDirectory
    lateinit var outputDir: File

    @TaskAction
    fun generate() {
        println("I am alive! $modelsFile $outputDir")
        val modelsText = modelsFile.readText()
        val modelsJson = groovy.json.JsonSlurper()
            .parseText(modelsText) as Map<String, Map<String, Map<String, Any>>>
        for ((modelName, model) in modelsJson.entries) {
            val modelDir = outputDir
            println("Writing to $modelDir")
            modelDir.mkdirs()
            for (type in model.values) {
                val typeName = type["name"] as String
                val fileSpec = FileSpec.builder("com.charlag.tuta.entities.$modelName", typeName)
                    .addAnnotation(
                        AnnotationSpec.builder(ClassName.bestGuess("kotlinx.serialization.UseSerializers"))
                            .addMember("com.charlag.tuta.entities.ByteArraySerializer::class")
                            .addMember("com.charlag.tuta.entities.DateSerializer::class")
                            .build()
                    )
                    .addType(
                        TypeSpec.classBuilder(typeName)
                            .apply {
                                val constructorBuilder = FunSpec.constructorBuilder()
                                val values = type["values"] as Map<String, Any>
                                val isElement = when (type["type"]) {
                                    "LIST_ELEMENT_TYPE" -> {
                                        superclass(ClassName.bestGuess("com.charlag.tuta.entities.ListElementEntity"))
                                        true
                                    }
                                    "ELEMENT_TYPE" -> {
                                        superclass(ClassName.bestGuess("com.charlag.tuta.entities.ElementEntity"))
                                        true
                                    }
                                    else -> {
                                        superclass(ClassName.bestGuess("com.charlag.tuta.entities.Entity"))
                                        false
                                    }
                                }
                                for ((valueName, value) in values) {
                                    val valueType = valueToType(type, value as Map<String, Any>)
                                    addProperty(
                                        PropertySpec.builder(valueName, valueType)
                                            .initializer(valueName)
                                            .apply {
                                                if (valueName == "_id" && isElement) {
                                                    addModifiers(KModifier.OVERRIDE)
                                                }
                                            }
                                            .build()
                                    )
                                    constructorBuilder.addParameter(
                                        ParameterSpec.builder(valueName, valueType)
                                            .apply {
                                                if (valueName.startsWith("_")) defaultValue("null")
                                            }
                                            .build()
                                    )
                                }

                                val assocs = type["associations"] as Map<String, Map<String, Any>>
                                for ((assocName, assoc) in assocs) {
                                    val assocType = assocToType(model, modelName, assoc)
                                    addProperty(
                                        PropertySpec.builder(assocName, assocType)
                                            .initializer(assocName)
                                            .build()
                                    )
                                    constructorBuilder.addParameter(assocName, assocType)
                                }
                                primaryConstructor(constructorBuilder.build())

                                addAnnotation(ClassName.bestGuess("kotlinx.serialization.Serializable"))
                            }
                            .build()
                    )
                    .build()
                fileSpec.writeTo(modelDir)
            }
        }
    }
}

fun assocToType(model: Map<String, Any>, modelName: String, assoc: Map<String, Any>): TypeName {
    val baseType = when (assoc["type"] as String) {
        "ELEMENT_ASSOCIATION" -> idClassName
        "LIST_ELEMENT_ASSOCIATION" -> idTupleClassName
        "LIST_ASSOCIATION" -> idClassName
        "AGGREGATION" -> {
            val refTypeName = assoc["refType"] as String
            val refPackage = if (refTypeName in model) modelName else "sys"
            val refType = "com.charlag.tuta.entities.$refPackage.$refTypeName"
            ClassName.bestGuess(refType)
        }
        else -> error("Unknown association type $assoc")
    }
    return when (assoc["cardinality"] as String) {
        "One" -> baseType
        "ZeroOrOne" -> baseType.copy(nullable = true)
        "Any" -> LIST.parameterizedBy(baseType)
        else -> error("Unknown cardinality $assoc")
    }
}
