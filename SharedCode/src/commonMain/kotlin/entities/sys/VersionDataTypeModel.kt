package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val VersionDataTypeModel: TypeModel = TypeModel(
	  name = "VersionData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 487,
    rootId = "A3N5cwAB5w",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"application" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
),
"listId" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"typeId" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
