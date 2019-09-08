package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val VariableExternalAuthInfoTypeModel: TypeModel = TypeModel(
	  name = "VariableExternalAuthInfo",
	  encrypted = false,
    type = ELEMENT_TYPE,
    id = 66,
    rootId = "A3N5cwBC",
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
"authUpdateCounter" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"lastSentTimestamp" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
),
"loggedInIpAddressHash" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"loggedInTimestamp" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"loggedInVerifier" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"sentCount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
