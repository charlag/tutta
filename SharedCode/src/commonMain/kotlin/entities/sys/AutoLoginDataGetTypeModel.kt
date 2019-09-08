package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val AutoLoginDataGetTypeModel: TypeModel = TypeModel(
	  name = "AutoLoginDataGet",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 431,
    rootId = "A3N5cwABrw",
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
	  associations = mapOf("userId" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "UserTypeModel",
	final = false,
	external = false
))
)
