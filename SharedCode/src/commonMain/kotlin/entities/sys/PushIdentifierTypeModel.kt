package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PushIdentifierTypeModel: TypeModel = TypeModel(
	  name = "PushIdentifier",
	  encrypted = true,
    type = LIST_ELEMENT_TYPE,
    id = 625,
    rootId = "A3N5cwACcQ",
    values = mapOf("_area" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
"_format" to Value(
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
"_owner" to Value(
  type = GeneratedIdType,
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
"disabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
"displayName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"identifier" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"language" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"lastNotificationDate" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"pushServiceType" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf()
)
