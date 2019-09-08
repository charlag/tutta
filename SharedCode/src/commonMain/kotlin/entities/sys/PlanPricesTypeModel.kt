package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PlanPricesTypeModel: TypeModel = TypeModel(
	  name = "PlanPrices",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1460,
    rootId = "A3N5cwAFtA",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"additionalUserPriceMonthly" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"contactFormPriceMonthly" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"firstYearDiscount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"includedAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"includedStorage" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"monthlyPrice" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"monthlyReferencePrice" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
