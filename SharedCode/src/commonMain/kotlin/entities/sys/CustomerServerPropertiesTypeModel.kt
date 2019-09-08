package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CustomerServerPropertiesTypeModel: TypeModel = TypeModel(
	  name = "CustomerServerProperties",
	  encrypted = true,
    type = ELEMENT_TYPE,
    id = 954,
    rootId = "A3N5cwADug",
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
"requirePasswordUpdateAfterReset" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
"saveEncryptedIpAddressInSession" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
"whitelabelCode" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("emailSenderList" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "EmailSenderListElementTypeModel",
	final = false,
	external = false
),
"whitelabelRegistrationDomains" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "StringWrapperTypeModel",
	final = false,
	external = false
),
"whitelistedDomains" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "DomainsRefTypeModel",
	final = true,
	external = false
))
)
