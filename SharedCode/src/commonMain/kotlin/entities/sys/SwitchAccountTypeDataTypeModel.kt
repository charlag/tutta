package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val SwitchAccountTypeDataTypeModel: TypeModel = TypeModel(
	  name = "SwitchAccountTypeData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 772,
    rootId = "A3N5cwADBA",
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
"campaign" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"date" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"proUpgrade" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
