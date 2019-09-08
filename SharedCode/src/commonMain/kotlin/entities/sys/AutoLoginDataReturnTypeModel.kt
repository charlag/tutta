package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val AutoLoginDataReturnTypeModel: TypeModel = TypeModel(
	  name = "AutoLoginDataReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 438,
    rootId = "A3N5cwABtg",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"deviceKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
