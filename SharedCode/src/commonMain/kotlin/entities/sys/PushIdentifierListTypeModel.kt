package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PushIdentifierListTypeModel: TypeModel = TypeModel(
	  name = "PushIdentifierList",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 635,
    rootId = "A3N5cwACew",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("list" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "PushIdentifierTypeModel",
	final = true,
	external = false
))
)
