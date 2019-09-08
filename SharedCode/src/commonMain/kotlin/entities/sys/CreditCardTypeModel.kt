package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CreditCardTypeModel: TypeModel = TypeModel(
	  name = "CreditCard",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1313,
    rootId = "A3N5cwAFIQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"cardHolderName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"cvv" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"expirationMonth" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"expirationYear" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"number" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf()
)
