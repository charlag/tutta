package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val WebsocketCounterValueTypeModel: TypeModel = TypeModel(
	  name = "WebsocketCounterValue",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1488,
    rootId = "A3N5cwAF0A",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"count" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"mailListId" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
