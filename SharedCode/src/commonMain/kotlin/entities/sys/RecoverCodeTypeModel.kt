package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val RecoverCodeTypeModel: TypeModel = TypeModel(
	  name = "RecoverCode",
	  encrypted = false,
    type = ELEMENT_TYPE,
    id = 1407,
    rootId = "A3N5cwAFfw",
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
"recoverCodeEncUserGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
"userEncRecoverCode" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
"verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf()
)
