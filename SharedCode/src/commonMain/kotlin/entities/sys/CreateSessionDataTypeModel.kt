package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CreateSessionDataTypeModel: TypeModel = TypeModel(
	  name = "CreateSessionData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1211,
    rootId = "A3N5cwAEuw",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"accessKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"authToken" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"authVerifier" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"clientIdentifier" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
"mailAddress" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"recoverCodeVerifier" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
)),
	  associations = mapOf("user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "UserTypeModel",
	final = true,
	external = false
))
)
