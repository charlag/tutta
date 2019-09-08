package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val AccountingInfoTypeModel: TypeModel = TypeModel(
	  name = "AccountingInfo",
	  encrypted = true,
    type = ELEMENT_TYPE,
    id = 143,
    rootId = "A3N5cwAAjw",
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
"_modified" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
"_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
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
"business" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
"invoiceAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"invoiceCountry" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
"invoiceName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"invoiceVatIdNo" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
"lastInvoiceNbrOfSentSms" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
"lastInvoiceTimestamp" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"paymentAccountIdentifier" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
"paymentInterval" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"paymentMethod" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
"paymentMethodInfo" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
"paymentProviderCustomerId" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
"paypalBillingAgreement" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
"secondCountryInfo" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf("invoiceInfo" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "InvoiceInfoTypeModel",
	final = true,
	external = false
))
)
