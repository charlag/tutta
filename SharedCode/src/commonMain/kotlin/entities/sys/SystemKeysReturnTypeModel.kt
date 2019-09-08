package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val SystemKeysReturnTypeModel: TypeModel = TypeModel(
	  name = "SystemKeysReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 301,
    rootId = "A3N5cwABLQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"freeGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"premiumGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"starterGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"systemAdminPubKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"systemAdminPubKeyVersion" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("freeGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "GroupTypeModel",
	final = false,
	external = false
),
"premiumGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "GroupTypeModel",
	final = false,
	external = false
))
)
