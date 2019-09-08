package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PriceItemDataTypeModel: TypeModel = TypeModel(
	  name = "PriceItemData",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 847,
    rootId = "A3N5cwADTw",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"count" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"featureType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"price" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"singleType" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
