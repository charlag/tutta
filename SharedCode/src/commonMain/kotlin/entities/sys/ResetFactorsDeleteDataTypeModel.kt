package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val ResetFactorsDeleteDataTypeModel: TypeModel = TypeModel(
	  name = "ResetFactorsDeleteData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1419,
    rootId = "A3N5cwAFiw",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"authVerifier" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
"mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
"recoverCodeVerifier" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf()
)
