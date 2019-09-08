package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val SessionTypeModel: TypeModel = TypeModel(
	  name = "Session",
	  encrypted = true,
    type = LIST_ELEMENT_TYPE,
    id = 1191,
    rootId = "A3N5cwAEpw",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
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
"accessKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"clientIdentifier" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"lastAccessTime" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
"loginIpAddress" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
"loginTime" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = true
),
"state" to Value(
  type = NumberType,
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
