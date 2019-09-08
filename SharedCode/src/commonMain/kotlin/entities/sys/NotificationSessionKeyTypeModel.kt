package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val NotificationSessionKeyTypeModel: TypeModel = TypeModel(
	  name = "NotificationSessionKey",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1553,
    rootId = "A3N5cwAGEQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"pushIdentifierSessionEncSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("pushIdentifier" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "PushIdentifierTypeModel",
	final = false,
	external = false
))
)
