package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val DomainInfoTypeModel: TypeModel = TypeModel(
	  name = "DomainInfo",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 696,
    rootId = "A3N5cwACuA",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"certificateExpiryDate" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"domain" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
"validatedMxRecord" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("catchAllMailGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "GroupTypeModel",
	final = true,
	external = false
),
"certificate" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "SslCertificateTypeModel",
	final = true,
	external = false
),
"whitelabelConfig" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "WhitelabelConfigTypeModel",
	final = true,
	external = false
))
)
