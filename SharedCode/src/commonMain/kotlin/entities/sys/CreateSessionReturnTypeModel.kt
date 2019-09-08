package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CreateSessionReturnTypeModel: TypeModel = TypeModel(
	  name = "CreateSessionReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1219,
    rootId = "A3N5cwAEww",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"accessToken" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("challenges" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "ChallengeTypeModel",
	final = true,
	external = false
),
"user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "UserTypeModel",
	final = true,
	external = false
))
)
