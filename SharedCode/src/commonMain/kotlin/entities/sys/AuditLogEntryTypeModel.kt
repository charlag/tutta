package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val AuditLogEntryTypeModel: TypeModel = TypeModel(
	  name = "AuditLogEntry",
	  encrypted = true,
    type = LIST_ELEMENT_TYPE,
    id = 1101,
    rootId = "A3N5cwAETQ",
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
"action" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
),
"actorIpAddress" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
"actorMailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
),
"date" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = true
),
"modifiedEntity" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf("groupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "GroupInfoTypeModel",
	final = true,
	external = false
),
"modifiedGroupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "GroupInfoTypeModel",
	final = true,
	external = false
))
)
