package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val BookingsRefTypeModel: TypeModel = TypeModel(
	  name = "BookingsRef",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 722,
    rootId = "A3N5cwAC0g",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "BookingTypeModel",
	final = true,
	external = false
))
)
