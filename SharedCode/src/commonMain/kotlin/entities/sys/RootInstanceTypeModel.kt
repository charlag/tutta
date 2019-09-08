package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val RootInstanceTypeModel: TypeModel = TypeModel(
	  name = "RootInstance",
	  encrypted = false,
    type = LIST_ELEMENT_TYPE,
    id = 231,
    rootId = "A3N5cwAA5w",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"reference" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
