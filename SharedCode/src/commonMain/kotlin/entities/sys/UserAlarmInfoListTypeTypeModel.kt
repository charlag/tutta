package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val UserAlarmInfoListTypeTypeModel: TypeModel = TypeModel(
	  name = "UserAlarmInfoListType",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1549,
    rootId = "A3N5cwAGDQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("alarms" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "UserAlarmInfoTypeModel",
	final = true,
	external = false
))
)
