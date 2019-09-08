package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val ExternalPropertiesReturnTypeModel: TypeModel = TypeModel(
	  name = "ExternalPropertiesReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 663,
    rootId = "A3N5cwAClw",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"accountType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"message" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("bigLogo" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "FileTypeModel",
	final = false,
	external = false
),
"smallLogo" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "FileTypeModel",
	final = false,
	external = false
))
)
