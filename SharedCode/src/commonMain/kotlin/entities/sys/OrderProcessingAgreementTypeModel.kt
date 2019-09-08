package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val OrderProcessingAgreementTypeModel: TypeModel = TypeModel(
	  name = "OrderProcessingAgreement",
	  encrypted = true,
    type = LIST_ELEMENT_TYPE,
    id = 1326,
    rootId = "A3N5cwAFLg",
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
"customerAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"signatureDate" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
),
"version" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "CustomerTypeModel",
	final = true,
	external = false
),
"signerUserGroupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfoTypeModel",
	final = false,
	external = false
))
)
