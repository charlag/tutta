package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val AutoLoginDataDeleteTypeModel: TypeModel = TypeModel(
	  name = "AutoLoginDataDelete",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 435,
    rootId = "A3N5cwABsw",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"deviceToken" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
