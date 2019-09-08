package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val VersionInfoTypeModel: TypeModel = TypeModel(
	  name = "VersionInfo",
	  encrypted = false,
    type = LIST_ELEMENT_TYPE,
    id = 237,
    rootId = "A3N5cwAA7Q",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"app" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"operation" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"referenceList" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"timestamp" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
),
"type" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"versionData" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("author" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupTypeModel",
	final = false,
	external = false
),
"authorGroupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfoTypeModel",
	final = true,
	external = false
))
)
