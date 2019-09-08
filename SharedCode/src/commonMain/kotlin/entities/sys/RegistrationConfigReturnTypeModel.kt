package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val RegistrationConfigReturnTypeModel: TypeModel = TypeModel(
	  name = "RegistrationConfigReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 606,
    rootId = "A3N5cwACXg",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"freeEnabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
"starterEnabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
