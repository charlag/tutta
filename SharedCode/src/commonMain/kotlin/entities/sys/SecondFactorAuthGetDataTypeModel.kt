package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val SecondFactorAuthGetDataTypeModel: TypeModel = TypeModel(
	  name = "SecondFactorAuthGetData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1233,
    rootId = "A3N5cwAE0Q",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"accessToken" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf()
)
