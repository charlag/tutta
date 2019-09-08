package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val GeneratedIdWrapperTypeModel: TypeModel = TypeModel(
	  name = "GeneratedIdWrapper",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1349,
    rootId = "A3N5cwAFRQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"value" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
