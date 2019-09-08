package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val SecondFactorAuthenticationTypeModel: TypeModel = TypeModel(
	  name = "SecondFactorAuthentication",
	  encrypted = false,
    type = LIST_ELEMENT_TYPE,
    id = 54,
    rootId = "A3N5cwA2",
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
"code" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"finished" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
"service" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"verifyCount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
