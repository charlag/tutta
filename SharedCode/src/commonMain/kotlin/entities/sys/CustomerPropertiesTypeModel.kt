package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CustomerPropertiesTypeModel: TypeModel = TypeModel(
	  name = "CustomerProperties",
	  encrypted = false,
    type = ELEMENT_TYPE,
    id = 656,
    rootId = "A3N5cwACkA",
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
"externalUserWelcomeMessage" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"lastUpgradeReminder" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("bigLogo" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "FileTypeModel",
	final = false,
	external = false
),
"notificationMailTemplates" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "NotificationMailTemplateTypeModel",
	final = false,
	external = false
),
"smallLogo" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "FileTypeModel",
	final = false,
	external = false
))
)
