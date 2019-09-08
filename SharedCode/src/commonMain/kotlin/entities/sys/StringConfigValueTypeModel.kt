package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val StringConfigValueTypeModel: TypeModel = TypeModel(
	  name = "StringConfigValue",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 515,
    rootId = "A3N5cwACAw",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"name" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"value" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
