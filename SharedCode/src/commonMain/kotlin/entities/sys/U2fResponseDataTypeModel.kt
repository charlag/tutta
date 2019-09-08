package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val U2fResponseDataTypeModel: TypeModel = TypeModel(
	  name = "U2fResponseData",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1225,
    rootId = "A3N5cwAEyQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"clientData" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
"keyHandle" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
"signatureData" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf()
)
