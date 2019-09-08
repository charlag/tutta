package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val U2fKeyTypeModel: TypeModel = TypeModel(
	  name = "U2fKey",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1178,
    rootId = "A3N5cwAEmg",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"appId" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
"keyHandle" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("secondFactor" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "SecondFactorTypeModel",
	final = false,
	external = false
))
)
