package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val SecondFactorAuthDataTypeModel: TypeModel = TypeModel(
	  name = "SecondFactorAuthData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 541,
    rootId = "A3N5cwACHQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"otpCode" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"type" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
)),
	  associations = mapOf("u2f" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "U2fResponseDataTypeModel",
	final = true,
	external = false
),
"session" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "SessionTypeModel",
	final = true,
	external = false
))
)
