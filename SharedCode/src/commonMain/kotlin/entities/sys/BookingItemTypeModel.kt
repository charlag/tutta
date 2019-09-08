package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val BookingItemTypeModel: TypeModel = TypeModel(
	  name = "BookingItem",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 700,
    rootId = "A3N5cwACvA",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"currentCount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"currentInvoicedCount" to Value(
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
"maxCount" to Value(
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
"priceType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"totalInvoicedCount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
