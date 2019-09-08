package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CustomerInfoReturnTypeModel: TypeModel = TypeModel(
	  name = "CustomerInfoReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 550,
    rootId = "A3N5cwACJg",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"sendMailDisabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
