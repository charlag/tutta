package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val SseConnectDataTypeModel: TypeModel = TypeModel(
	  name = "SseConnectData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1352,
    rootId = "A3N5cwAFSA",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"identifier" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("userIds" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "GeneratedIdWrapperTypeModel",
	final = false,
	external = false
))
)
