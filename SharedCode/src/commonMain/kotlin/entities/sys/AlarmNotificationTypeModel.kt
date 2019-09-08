package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val AlarmNotificationTypeModel: TypeModel = TypeModel(
	  name = "AlarmNotification",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1564,
    rootId = "A3N5cwAGHA",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"eventEnd" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = true
),
"eventStart" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = true
),
"operation" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
"summary" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf("alarmInfo" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "AlarmInfoTypeModel",
	final = true,
	external = false
),
"notificationSessionKeys" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "NotificationSessionKeyTypeModel",
	final = true,
	external = false
),
"repeatRule" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "RepeatRuleTypeModel",
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
