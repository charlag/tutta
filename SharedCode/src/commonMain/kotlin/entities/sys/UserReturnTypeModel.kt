package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val UserReturnTypeModel: TypeModel = TypeModel(
	  name = "UserReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 392,
    rootId = "A3N5cwABiA",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "UserTypeModel",
	final = false,
	external = false
),
"userGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupTypeModel",
	final = false,
	external = false
))
)
