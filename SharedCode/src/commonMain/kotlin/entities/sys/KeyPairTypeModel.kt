package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val KeyPairTypeModel: TypeModel = TypeModel(
	  name = "KeyPair",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 0,
    rootId = "A3N5cwAA",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"pubKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
"symEncPrivKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
"version" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf()
)
