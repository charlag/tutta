package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CustomDomainDataTypeModel: TypeModel = TypeModel(
	  name = "CustomDomainData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 735,
    rootId = "A3N5cwAC3w",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"domain" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("catchAllMailGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "GroupTypeModel",
	final = false,
	external = false
))
)
