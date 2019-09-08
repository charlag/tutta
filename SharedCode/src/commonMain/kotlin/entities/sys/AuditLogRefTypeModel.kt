package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val AuditLogRefTypeModel: TypeModel = TypeModel(
	  name = "AuditLogRef",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1114,
    rootId = "A3N5cwAEWg",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "AuditLogEntryTypeModel",
	final = true,
	external = false
))
)
