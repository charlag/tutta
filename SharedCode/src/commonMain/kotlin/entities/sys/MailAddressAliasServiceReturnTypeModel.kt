package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val MailAddressAliasServiceReturnTypeModel: TypeModel = TypeModel(
	  name = "MailAddressAliasServiceReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 692,
    rootId = "A3N5cwACtA",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"enabledAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"nbrOfFreeAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"totalAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"usedAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
