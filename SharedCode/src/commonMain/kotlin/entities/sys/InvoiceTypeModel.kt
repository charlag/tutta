package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val InvoiceTypeModel: TypeModel = TypeModel(
	  name = "Invoice",
	  encrypted = true,
    type = LIST_ELEMENT_TYPE,
    id = 739,
    rootId = "A3N5cwAC4w",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"_listEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"country" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"date" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = true
),
"grandTotal" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
"number" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
"paymentMethod" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
"source" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"status" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"vat" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
"vatRate" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf("bookings" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "BookingTypeModel",
	final = true,
	external = false
),
"changes" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "InvoiceChangeTypeModel",
	final = true,
	external = false
))
)
