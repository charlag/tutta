package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CalendarEventRefTypeModel: TypeModel = TypeModel(
	  name = "CalendarEventRef",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1532,
    rootId = "A3N5cwAF_A",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"elementId" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"listId" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf()
)
