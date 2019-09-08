package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val ChatTypeModel: TypeModel = TypeModel(
	  name = "Chat",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 457,
    rootId = "A3N5cwAByQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"recipient" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
),
"sender" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
),
"text" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
