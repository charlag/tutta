package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val ResetPasswordDataTypeModel: TypeModel = TypeModel(
	  name = "ResetPasswordData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 584,
    rootId = "A3N5cwACSA",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"pwEncUserGroupKey" to Value(
  type = BytesType,
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
"verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "UserTypeModel",
	final = false,
	external = false
))
)
