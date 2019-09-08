package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val DeleteCustomerDataTypeModel: TypeModel = TypeModel(
	  name = "DeleteCustomerData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 641,
    rootId = "A3N5cwACgQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"authVerifier" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"reason" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"takeoverMailAddress" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"undelete" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "CustomerTypeModel",
	final = false,
	external = false
))
)
