package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PriceRequestDataTypeModel: TypeModel = TypeModel(
	  name = "PriceRequestData",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 836,
    rootId = "A3N5cwADRA",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"accountType" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"business" to Value(
  type = BooleanType,
  cardinality = ZeroOrOne,
  final = false,
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
"paymentInterval" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"reactivate" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
