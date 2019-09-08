package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val WhitelabelConfigTypeModel: TypeModel = TypeModel(
	  name = "WhitelabelConfig",
	  encrypted = false,
    type = ELEMENT_TYPE,
    id = 1127,
    rootId = "A3N5cwAEZw",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"germanLanguageCode" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"imprintUrl" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"jsonTheme" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"metaTags" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"privacyStatementUrl" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("bootstrapCustomizations" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "BootstrapFeatureTypeModel",
	final = false,
	external = false
),
"certificateInfo" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "CertificateInfoTypeModel",
	final = false,
	external = false
))
)
