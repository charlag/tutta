package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val BookingTypeModel: TypeModel = TypeModel(
	  name = "Booking",
	  encrypted = false,
    type = LIST_ELEMENT_TYPE,
    id = 709,
    rootId = "A3N5cwACxQ",
    values = mapOf("_area" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
"_format" to Value(
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
"_owner" to Value(
  type = GeneratedIdType,
  cardinality = One,
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
"business" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
"createDate" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
),
"endDate" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"paymentInterval" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"paymentMonths" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "BookingItemTypeModel",
	final = false,
	external = false
))
)
