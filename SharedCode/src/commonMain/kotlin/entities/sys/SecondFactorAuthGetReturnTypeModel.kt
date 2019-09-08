package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val SecondFactorAuthGetReturnTypeModel: TypeModel = TypeModel(
	  name = "SecondFactorAuthGetReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1236,
    rootId = "A3N5cwAE1A",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"secondFactorPending" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf()
)
