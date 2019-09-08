package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val ChallengeTypeModel: TypeModel = TypeModel(
	  name = "Challenge",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1187,
    rootId = "A3N5cwAEow",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"type" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("otp" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "OtpChallengeTypeModel",
	final = true,
	external = false
),
"u2f" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "U2fChallengeTypeModel",
	final = true,
	external = false
))
)
