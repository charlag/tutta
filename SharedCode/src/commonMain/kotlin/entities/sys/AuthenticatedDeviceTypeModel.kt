package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val AuthenticatedDeviceTypeModel: TypeModel = TypeModel(
	  name = "AuthenticatedDevice",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 43,
    rootId = "A3N5cwAr",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"authType" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
"deviceKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
"deviceToken" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf()
)
