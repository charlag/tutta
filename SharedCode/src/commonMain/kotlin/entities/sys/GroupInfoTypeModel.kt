package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val GroupInfoTypeModel: TypeModel = TypeModel(
	  name = "GroupInfo",
	  encrypted = true,
    type = LIST_ELEMENT_TYPE,
    id = 14,
    rootId = "A3N5cwAO",
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
"_listEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
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
"created" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
"deleted" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"groupType" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"mailAddress" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"name" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf("mailAddressAliases" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "MailAddressAliasTypeModel",
	final = true,
	external = false
),
"group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupTypeModel",
	final = true,
	external = false
),
"localAdmin" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "GroupTypeModel",
	final = true,
	external = false
))
)
