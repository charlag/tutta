package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val SecondFactorAuthAllowedReturnTypeModel: TypeModel = TypeModel(
	  name = "SecondFactorAuthAllowedReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 546,
    rootId = "A3N5cwACIg",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"allowed" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
