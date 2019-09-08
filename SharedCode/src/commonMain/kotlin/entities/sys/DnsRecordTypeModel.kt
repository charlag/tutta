package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val DnsRecordTypeModel: TypeModel = TypeModel(
	  name = "DnsRecord",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1581,
    rootId = "A3N5cwAGLQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"subdomain" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"type" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"value" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
