package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CertificateInfoTypeModel: TypeModel = TypeModel(
	  name = "CertificateInfo",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1500,
    rootId = "A3N5cwAF3A",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"expiryDate" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"state" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
"type" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("certificate" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "SslCertificateTypeModel",
	final = true,
	external = false
))
)
