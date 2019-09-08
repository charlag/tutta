package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val U2fRegisteredDeviceTypeModel: TypeModel = TypeModel(
	  name = "U2fRegisteredDevice",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1162,
    rootId = "A3N5cwAEig",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"appId" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
"compromised" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
"counter" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
"keyHandle" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
"publicKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf()
)
