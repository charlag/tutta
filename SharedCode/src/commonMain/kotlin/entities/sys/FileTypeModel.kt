package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val FileTypeModel: TypeModel = TypeModel(
	  name = "File",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 917,
    rootId = "A3N5cwADlQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"data" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"mimeType" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"name" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
