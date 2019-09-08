package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CustomDomainCheckReturnTypeModel: TypeModel = TypeModel(
	  name = "CustomDomainCheckReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1589,
    rootId = "A3N5cwAGNQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"checkResult" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("invalidRecords" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "DnsRecordTypeModel",
	final = false,
	external = false
),
"missingRecords" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "DnsRecordTypeModel",
	final = false,
	external = false
))
)
