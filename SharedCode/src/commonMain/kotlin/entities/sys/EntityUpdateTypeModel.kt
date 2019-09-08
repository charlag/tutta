package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val EntityUpdateTypeModel: TypeModel = TypeModel(
	  name = "EntityUpdate",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 462,
    rootId = "A3N5cwABzg",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"application" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"instanceId" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"instanceListId" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"operation" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"type" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
