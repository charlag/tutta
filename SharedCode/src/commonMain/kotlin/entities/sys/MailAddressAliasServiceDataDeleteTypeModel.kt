package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val MailAddressAliasServiceDataDeleteTypeModel: TypeModel = TypeModel(
	  name = "MailAddressAliasServiceDataDelete",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 785,
    rootId = "A3N5cwADEQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"restore" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupTypeModel",
	final = false,
	external = false
))
)
