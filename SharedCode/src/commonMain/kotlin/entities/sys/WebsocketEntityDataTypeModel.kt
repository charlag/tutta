package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val WebsocketEntityDataTypeModel: TypeModel = TypeModel(
	  name = "WebsocketEntityData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1483,
    rootId = "A3N5cwAFyw",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"eventBatchId" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
),
"eventBatchOwner" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("eventBatch" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "EntityUpdateTypeModel",
	final = false,
	external = false
))
)
