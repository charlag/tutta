package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val StringWrapperTypeModel: TypeModel = TypeModel(
	  name = "StringWrapper",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 728,
    rootId = "A3N5cwAC2A",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
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
