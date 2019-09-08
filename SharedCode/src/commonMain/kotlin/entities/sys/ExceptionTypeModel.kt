package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val ExceptionTypeModel: TypeModel = TypeModel(
	  name = "Exception",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 468,
    rootId = "A3N5cwAB1A",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"msg" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"type" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
