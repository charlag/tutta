package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val SecondFactorTypeModel: TypeModel = TypeModel(
	  name = "SecondFactor",
	  encrypted = false,
    type = LIST_ELEMENT_TYPE,
    id = 1169,
    rootId = "A3N5cwAEkQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"name" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
"otpSecret" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"type" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("u2f" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "U2fRegisteredDeviceTypeModel",
	final = true,
	external = false
))
)
