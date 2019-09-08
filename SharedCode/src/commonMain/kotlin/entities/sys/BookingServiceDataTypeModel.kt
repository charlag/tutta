package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val BookingServiceDataTypeModel: TypeModel = TypeModel(
	  name = "BookingServiceData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1061,
    rootId = "A3N5cwAEJQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"amount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"date" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"featureType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
