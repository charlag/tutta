package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val UserDataTypeModel: TypeModel = TypeModel(
	  name = "UserData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 396,
    rootId = "A3N5cwABjA",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"date" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"mobilePhoneNumber" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"salt" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"userEncClientKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"userEncCustomerGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("userGroupData" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "CreateGroupDataTypeModel",
	final = false,
	external = false
))
)
