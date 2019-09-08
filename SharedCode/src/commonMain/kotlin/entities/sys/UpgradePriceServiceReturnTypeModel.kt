package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val UpgradePriceServiceReturnTypeModel: TypeModel = TypeModel(
	  name = "UpgradePriceServiceReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1469,
    rootId = "A3N5cwAFvQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"business" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
"messageTextId" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("premiumPrices" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "PlanPricesTypeModel",
	final = false,
	external = false
),
"proPrices" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "PlanPricesTypeModel",
	final = false,
	external = false
))
)
