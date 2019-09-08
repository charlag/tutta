package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PriceServiceReturnTypeModel: TypeModel = TypeModel(
	  name = "PriceServiceReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 859,
    rootId = "A3N5cwADWw",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"currentPeriodAddedPrice" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"periodEndDate" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("currentPriceNextPeriod" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "PriceDataTypeModel",
	final = false,
	external = false
),
"currentPriceThisPeriod" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "PriceDataTypeModel",
	final = false,
	external = false
),
"futurePriceNextPeriod" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "PriceDataTypeModel",
	final = false,
	external = false
))
)
