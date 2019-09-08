package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val U2fChallengeTypeModel: TypeModel = TypeModel(
	  name = "U2fChallenge",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1183,
    rootId = "A3N5cwAEnw",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"challenge" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("keys" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "U2fKeyTypeModel",
	final = true,
	external = false
))
)
