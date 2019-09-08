package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val AlarmInfoTypeModel: TypeModel = TypeModel(
	  name = "AlarmInfo",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1536,
    rootId = "A3N5cwAGAA",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"alarmIdentifier" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
"trigger" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf("calendarRef" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "CalendarEventRefTypeModel",
	final = false,
	external = false
))
)
