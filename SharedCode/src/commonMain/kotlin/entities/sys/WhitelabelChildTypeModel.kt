package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val WhitelabelChildTypeModel: TypeModel = TypeModel(
	  name = "WhitelabelChild",
	  encrypted = true,
    type = LIST_ELEMENT_TYPE,
    id = 1257,
    rootId = "A3N5cwAE6Q",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
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
"comment" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"createdDate" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
"deletedDate" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "CustomerTypeModel",
	final = true,
	external = false
))
)
