package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val UserExternalAuthInfoTypeModel: TypeModel = TypeModel(
	  name = "UserExternalAuthInfo",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 77,
    rootId = "A3N5cwBN",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"authUpdateCounter" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"autoAuthenticationId" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"autoTransmitPassword" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"latestSaltHash" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("variableAuthInfo" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "VariableExternalAuthInfoTypeModel",
	final = true,
	external = false
))
)
