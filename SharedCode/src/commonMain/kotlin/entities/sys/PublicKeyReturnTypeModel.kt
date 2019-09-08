package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PublicKeyReturnTypeModel: TypeModel = TypeModel(
	  name = "PublicKeyReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 412,
    rootId = "A3N5cwABnA",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"pubKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"pubKeyVersion" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
