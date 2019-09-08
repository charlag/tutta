package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CustomDomainReturnTypeModel: TypeModel = TypeModel(
	  name = "CustomDomainReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 731,
    rootId = "A3N5cwAC2w",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"validationResult" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("invalidDnsRecords" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "StringWrapperTypeModel",
	final = true,
	external = false
))
)
