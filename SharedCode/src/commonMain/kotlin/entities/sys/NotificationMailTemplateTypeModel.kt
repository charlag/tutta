package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val NotificationMailTemplateTypeModel: TypeModel = TypeModel(
	  name = "NotificationMailTemplate",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1517,
    rootId = "A3N5cwAF7Q",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"body" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"language" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"subject" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
