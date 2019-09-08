package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val VersionReturnTypeModel: TypeModel = TypeModel(
	  name = "VersionReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 493,
    rootId = "A3N5cwAB7Q",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("versions" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "VersionTypeModel",
	final = false,
	external = false
))
)
