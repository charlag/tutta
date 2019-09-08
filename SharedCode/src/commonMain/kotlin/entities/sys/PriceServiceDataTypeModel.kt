package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PriceServiceDataTypeModel: TypeModel = TypeModel(
	  name = "PriceServiceData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 843,
    rootId = "A3N5cwADSw",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"campaign" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"date" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("priceRequest" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "PriceRequestDataTypeModel",
	final = false,
	external = false
))
)
