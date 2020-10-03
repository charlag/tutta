import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.lang.Error
import java.lang.reflect.WildcardType

val metamodelTypes = listOf(
    /**
     * Instance (separate database entity) which does not belong to any particular list. Has a
     * single ID - [GeneratedId] or [CustomId].
     */
    "ELEMENT_TYPE",

    /**
     * Instance (separate database entity) which belongs to one of the lists. It means that it's
     * Id is [IdTuple], it can be loaded in ranges.
     */
    "LIST_ELEMENT_TYPE",

    /**
     * Not a separate database entity but a structure on Instance or DataTransferType. Has it's
     * own id.
     */
    "AGGREGATED_TYPE",

    /**
     * Entity which is not represented in the database but is used for sending/receiving data via
     * services.
     */
    "DATA_TRANSFER_TYPE"
)

val cardinatlityTypes = listOf(
    "One", "ZeroOrOne", "Any"
)

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

fun generateBoolean(value: Any?): String = if (value as Boolean) "true" else "false"

open class GenerateModels : DefaultTask() {
    @get:InputFile
    lateinit var modelsFile: File

    @get:OutputDirectory
    lateinit var outputDir: File

    @TaskAction
    fun generate() {
        val modelsText = modelsFile.readText()
        val modelsJson = groovy.json.JsonSlurper()
            .parseText(modelsText) as Map<String, Map<String, Map<String, Any>>>
        for ((modelName, model) in modelsJson.entries) {
            val modelDir = outputDir
            modelDir.mkdirs()
            for (type in model.values) {
                val typeName = type["name"] as String
                val fileSpec = FileSpec.builder("com.charlag.tuta.entities.$modelName", typeName)
                    .addAnnotation(
                        AnnotationSpec.builder(ClassName.bestGuess("kotlinx.serialization.UseSerializers"))
                            .addMember("com.charlag.tuta.entities.ByteArraySerializer::class")
                            .addMember("com.charlag.tuta.entities.DateSerializer::class")
                            .addMember("com.charlag.tuta.entities.IdSerilizer::class")
                            .build()
                    )
                    .addImport("com.charlag.tuta.entities", "TypeInfo", "TypeModel")

                    .addImport("com.charlag.tuta.entities.MetamodelType", metamodelTypes)
                    .addImport("com.charlag.tuta.entities.Cardinality", cardinatlityTypes)
                    .addImport(
                        "com.charlag.tuta.entities",
                        "Value",
                        "ValueType",
                        "Association",
                        "AssociationType"
                    )
                    .addProperty(
                        generateMetamodelType(typeName, modelName, type)
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

            // Generate typeInfos list
            val entityTYpe = ClassName.bestGuess("com.charlag.tuta.entities.Entity")
            // Use Z in the beginning of the file name so that the generated class is loaded
            // after all entities. This is a hack for JS/Native backends which have such
            // embarrassing issues.
            FileSpec.builder("com.charlag.tuta.entities.${modelName}", "ZTypeInfos")
                .addProperty(
                    PropertySpec.builder(
                        "typeInfo", MAP.parameterizedBy(
                            STRING,
                            ClassName.bestGuess("com.charlag.tuta.entities.TypeInfo")
                                .parameterizedBy(WildcardTypeName.producerOf(entityTYpe))
                        )
                    )
                        .initializer(
                            """
                        buildMap {
${model.keys.joinToString("\n") { "put(\"${it}\", ${it}TypeInfo)" }}                        
                        }
                    """
                        )
                        .build()
                )
                .build()
                .writeTo(modelDir)
        }
    }

    private fun generateMetamodelType(
        typeName: String,
        modelName: String,
        type: Map<String, Any>
    ): PropertySpec {
        return PropertySpec.builder(
            "${typeName}TypeInfo",
            ClassName.bestGuess("com.charlag.tuta.entities.TypeInfo")
                .parameterizedBy(ClassName.bestGuess("com.charlag.tuta.entities.${modelName}.${typeName}"))
        )
            .initializer(
                """
                                    TypeInfo(
                                       ${typeName}::class,
                                       "$modelName",
                                       TypeModel(
                                           name = "$typeName",
                                           encrypted = ${generateBoolean(type["encrypted"])},
                                           type = ${type["type"] as String},
                                           id = ${type["id"] as Int},
                                           rootId = "${type["rootId"] as String}",
                                           values = mapOf(
                                           ${
                    (type["values"] as Map<String, Map<String, Any>>).entries.joinToString { (k, v) ->
                        val valueBlock = CodeBlock.of(
                            """
                                                Value(
                                                    type = ${"ValueType.${v["type"] as String}Type"},
                                                    cardinality = ${v["cardinality"]},
                                                    encrypted = ${generateBoolean(v["encrypted"])},
                                                    final = ${generateBoolean(v["final"])}
                                                )
                                                
                                            """.trimIndent()
                        )
                        "\"$k\" to $valueBlock"
                    }
                }
                                            ),
                                           associations = mapOf(${
                    (type["associations"] as Map<String, Map<String, Any>>)
                        .entries.joinToString(separator = ",\n") { (k, v) ->
                            val assocBlock = CodeBlock.of(
                                """
                                Association(
                                    type = AssociationType.${v["type"]},
                                    cardinality = ${v["cardinality"]},
                                    refType = "${v["refType"]}",
                                    final = ${generateBoolean(v["final"])},
                                    external = ${if (v["external"] == true) "true" else false}
                                )
                            """.trimIndent()
                            )
                            "\"$k\" to $assocBlock"
                        }
                }),
                                           version = ${(type["version"] as String).toLong()}
                                       ),
                                       serializer = ${typeName}.serializer()
                                    )
                                """.trimIndent()
            )
            .build()
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
