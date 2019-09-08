package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val AlarmServicePostTypeModel: TypeModel = TypeModel(
	  name = "AlarmServicePost",
	  encrypted = true,
    type = DATA_TRANSFER_TYPE,
    id = 1576,
    rootId = "A3N5cwAGKA",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("alarmNotifications" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "AlarmNotificationTypeModel",
	final = false,
	external = false
))
)
