const fs = require("fs")

const modelName = "sys"
const dir = "./src/resources/" + modelName
const dirContents = fs.readdirSync(dir)

function serializeValue(value) {
  return `Value(
  type = ${value.type}Type,
  cardinality = ${value.cardinality},
  final = ${value.final},
  encrypted = ${value.encrypted}
)`
}

function serializeValues(model) {
  const argument = Array.from(Object.values(model.values)).map(v => `                "${v.name}" to ${serializeValue(v)}`).join(",\n")
  return `mapOf(${argument})`
}

function serializeAssociation(assoc) {
  return `Association(
	type = ${assoc.type},
  cardinality = ${assoc.cardinality},
	refType = "${assoc.refType}",
	final = ${assoc.final},
	external = ${assoc.external || false}
)`
}

function serializeAssociations(model) {
  const argument = Array.from(Object.values(model.associations))
        .map(a => `"${a.name}" to ${serializeAssociation(a)}`)
        .join(",\n")
  return `mapOf(${argument})`
}

function valueToType(type, value) {
  if (value.name === "_id") {
    if (type.type === "LIST_ELEMENT_TYPE") {
      return "IdTuple"
    } else {
      return "Id"
    }
  }
  switch (value.type) {
  case "Number":
    return "Long"
  case "Bytes":
    return "ByteArray"
  default:
    return value.type
  }
}

function assocToType(assoc) {
  let singleType
  switch (assoc.type) {
  case "AGGREGATION":
    singleType = assoc.refType
    break
  case "ELEMENT_ASSOCIATION":
  case "LIST_ASSOCIATION":
    singleType = "Id"
    break
  default:
    singleType = "IdTuple"
  }
  switch (assoc.cardinality) {
  case "Any":
    return `List<${singleType}>`
  case "One":
    return singleType
  case "ZeroOrOne":
    return `${singleType}?`
  }
}

;(async () => {
  const mappingPairs = []
  for (let file of dirContents) {
    //        fs.renameSync("./src/resources/sys/" + file,
    //            "./src/resources/sys/" + file.replace(".mjs", ".json"))
    //        const {_TypeModel} = await import("./src/resources/sys/" + file)
    //        console.log(_TypeModel)
    const jsonText = fs.readFileSync(dir + "/" + file, "utf8")
    const typeName = file.slice(0, -5)
    const model = JSON.parse(jsonText)
    const mapping = `TypeModel(
	  name = "${model.name}",
	  encrypted = ${model.encrypted},
      type = ${model.type},
      id = ${model.id},
      rootId = "${model.rootId}",
      values = ${serializeValues(model)},
	  associations = ${serializeAssociations(model)},
	  version = ${model.version}
)`

    const className = typeName === "File" && modelName == "tutanota" ? "TutanotaFile" : typeName

  mappingPairs.push(`
  TypeInfo(
      ${className}::class,
      "${modelName}",
      ${mapping},
      ${className}.serializer()
  )`)
}

                                   const classMapping = `
package com.charlag.tuta.entities.${modelName}

import com.charlag.tuta.entities.${modelName}.*
import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val typeInfos = listOf(
	${mappingPairs.join(",\n\t")}
)`
  fs.writeFileSync(`./src/commonMain/kotlin/entities/${modelName}/TypeModelMapping.kt`, classMapping)
})()
