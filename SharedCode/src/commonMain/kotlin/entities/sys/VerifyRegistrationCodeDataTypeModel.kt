package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val VerifyRegistrationCodeDataTypeModel: TypeModel = TypeModel(
	  name = "VerifyRegistrationCodeData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 351,
    rootId = "A3N5cwABXw",
    values = mapOf("_format" to Value(
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
"code" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
