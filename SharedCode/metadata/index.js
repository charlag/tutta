/**
 * Script to extract model definitions form Tutanota client source code.
 * Usage: node index.js /path/to/tutanota/source.
 * Output: file models.json in current directory.
 */
import {promises as fs} from "fs"
import {resolve} from "path"
import babelParser from "@babel/parser"


(async function() {
  const sourceRoot = process.argv[2]
  if (sourceRoot == null) {
    console.error("Please provide a path to the Tutanota client source root");
    process.exit(1);
  }
  console.log("Source root is in ", sourceRoot);

  const mappings = {};
  const modelsDir = resolve(sourceRoot, "src/api/entities");
  const modelNames = await fs.readdir(modelsDir);
  console.log("detected models are: ", modelNames);


  for (let model of modelNames) {
    mappings[model] = {};
  }

  for (const modelName of modelNames) {
    const appDir = resolve(modelsDir, modelName);
    const files = await fs.readdir(appDir);
    for (const file of files) {
      if (file === "Services.js") {
        continue;
      }
      const filePath = resolve(appDir, file);
      const contents = await fs.readFile(filePath, 'utf8');
      console.log(filePath)
      const parsed = babelParser.parse(contents, {
        sourceType: "module",
        plugins: ["flow"],
      });
      const modelNode = parsed.program.body
            .find((node) =>
                  node.type === "ExportNamedDeclaration"
                  && node.declaration.type === "VariableDeclaration"
                  && node.declaration.declarations[0].id.name === "_TypeModel"
                 );
      const dataNode = modelNode.declaration.declarations[0].init;
      const model = JSON.parse(contents.slice(dataNode.start, dataNode.end));
      const typeName = model.name

      mappings[modelName][typeName] = model


//       const mapping = `TypeModel(
// 	  name = "${model.name}",
// 	  encrypted = ${model.encrypted},
//       type = ${model.type},
//       id = ${model.id},
//       rootId = "${model.rootId}",
//       values = ${serializeValues(model)},
// 	  associations = ${serializeAssociations(model)},
// 	  version = ${model.version}
// )`

//       const className = typeName === "File" && modelName == "tutanota" ? "TutanotaFile" : typeName

//       mappingPairs.push(`
//   TypeInfo(
//       ${className}::class,
//       "${modelName}",
//       ${mapping},
//       ${className}.serializer()
//   )`)
    }

//     const classMapping = `
// package com.charlag.tuta.entities.${modelName}

// import com.charlag.tuta.entities.${modelName}.*
// import com.charlag.tuta.entities.*
// import com.charlag.tuta.entities.MetamodelType.*
// import com.charlag.tuta.entities.Cardinality.*
// import com.charlag.tuta.entities.ValueType.*
// import com.charlag.tuta.entities.AssociationType.*

// val typeInfos = listOf(
// 	${mappingPairs.join(",\n\t")}
// )`

    // await fs.writeFile(`../src/commonMain/kotlin/entities/${modelName}/TypeModelMapping.kt`, classMapping)
    }
  await fs.writeFile('models.json', JSON.stringify(mappings, null, 2));
})();

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
