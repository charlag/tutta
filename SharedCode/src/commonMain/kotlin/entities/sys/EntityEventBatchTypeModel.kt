package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val EntityEventBatchTypeModel: TypeModel = TypeModel(
	  name = "EntityEventBatch",
	  encrypted = false,
    type = LIST_ELEMENT_TYPE,
    id = 1079,
    rootId = "A3N5cwAENw",
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
)),
	  associations = mapOf("events" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "EntityUpdateTypeModel",
	final = true,
	external = false
))
)
