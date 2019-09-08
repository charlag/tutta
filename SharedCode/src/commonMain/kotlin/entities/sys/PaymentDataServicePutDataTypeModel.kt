package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PaymentDataServicePutDataTypeModel: TypeModel = TypeModel(
	  name = "PaymentDataServicePutData",
	  encrypted = true,
    type = DATA_TRANSFER_TYPE,
    id = 793,
    rootId = "A3N5cwADGQ",
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
"confirmedCountry" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
"invoiceAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"invoiceCountry" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"invoiceName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"invoiceVatIdNo" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"paymentInterval" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"paymentMethod" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
"paymentMethodInfo" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
"paymentToken" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
)),
	  associations = mapOf("creditCard" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "CreditCardTypeModel",
	final = false,
	external = false
))
)
