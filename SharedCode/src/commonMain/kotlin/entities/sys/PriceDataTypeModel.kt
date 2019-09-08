package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PriceDataTypeModel: TypeModel = TypeModel(
	  name = "PriceData",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 853,
    rootId = "A3N5cwADVQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"paymentInterval" to Value(
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
"taxIncluded" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "PriceItemDataTypeModel",
	final = false,
	external = false
))
)
