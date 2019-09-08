package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val WebsocketCounterDataTypeModel: TypeModel = TypeModel(
	  name = "WebsocketCounterData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1492,
    rootId = "A3N5cwAF1A",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"mailGroup" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("counterValues" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "WebsocketCounterValueTypeModel",
	final = false,
	external = false
))
)
