package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val RegistrationCaptchaServiceGetDataTypeModel: TypeModel = TypeModel(
	  name = "RegistrationCaptchaServiceGetData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1479,
    rootId = "A3N5cwAFxw",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"token" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
