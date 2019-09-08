package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val InvoiceInfoTypeModel: TypeModel = TypeModel(
	  name = "InvoiceInfo",
	  encrypted = false,
    type = ELEMENT_TYPE,
    id = 752,
    rootId = "A3N5cwAC8A",
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
"publishInvoices" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
"specialPriceBrandingPerUser" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"specialPriceContactFormSingle" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"specialPriceSharedGroupSingle" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"specialPriceUserSingle" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"specialPriceUserTotal" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("invoices" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "InvoiceTypeModel",
	final = true,
	external = false
))
)
