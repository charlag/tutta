package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CustomerInfoTypeModel: TypeModel = TypeModel(
	  name = "CustomerInfo",
	  encrypted = false,
    type = LIST_ELEMENT_TYPE,
    id = 148,
    rootId = "A3N5cwAAlA",
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
"activationTime" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"company" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"creationTime" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
"deletionReason" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"deletionTime" to Value(
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
"erased" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
"includedEmailAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"includedStorageCapacity" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"promotionEmailAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"promotionStorageCapacity" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"registrationMailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
"source" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"testEndTime" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"usedSharedEmailAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("bookings" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "BookingsRefTypeModel",
	final = true,
	external = false
),
"domainInfos" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "DomainInfoTypeModel",
	final = true,
	external = false
),
"accountingInfo" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "AccountingInfoTypeModel",
	final = true,
	external = false
),
"customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "CustomerTypeModel",
	final = true,
	external = false
),
"takeoverCustomer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "CustomerTypeModel",
	final = false,
	external = false
))
)
