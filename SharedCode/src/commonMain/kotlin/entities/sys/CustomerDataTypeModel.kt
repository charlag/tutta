package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CustomerDataTypeModel: TypeModel = TypeModel(
	  name = "CustomerData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 374,
    rootId = "A3N5cwABdg",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"accountingInfoBucketEncAccountingInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"adminEncAccountingInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"authToken" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"company" to Value(
  type = StringType,
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
"domain" to Value(
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
"symEncAccountGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"systemCustomerPubEncAccountingInfoBucketKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"systemCustomerPubKeyVersion" to Value(
  type = NumberType,
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
"verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("adminGroupList" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "CreateGroupListDataTypeModel",
	final = false,
	external = false
),
"customerGroupList" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "CreateGroupListDataTypeModel",
	final = false,
	external = false
),
"teamGroupList" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "CreateGroupListDataTypeModel",
	final = false,
	external = false
),
"userGroupList" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "CreateGroupListDataTypeModel",
	final = false,
	external = false
))
)
