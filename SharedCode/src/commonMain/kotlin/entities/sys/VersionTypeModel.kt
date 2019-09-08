package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val VersionTypeModel: TypeModel = TypeModel(
	  name = "Version",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 480,
    rootId = "A3N5cwAB4A",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"operation" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"timestamp" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
),
"version" to Value(
  type = GeneratedIdType,
  cardinality = One,
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
	final = false,
	external = false
))
)
