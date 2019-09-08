package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val SendRegistrationCodeDataTypeModel: TypeModel = TypeModel(
	  name = "SendRegistrationCodeData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 341,
    rootId = "A3N5cwABVQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"accountType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"authToken" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"language" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"mobilePhoneNumber" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
