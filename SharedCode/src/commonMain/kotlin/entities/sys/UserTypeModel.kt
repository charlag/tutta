package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val UserTypeModel: TypeModel = TypeModel(
	  name = "User",
	  encrypted = false,
    type = ELEMENT_TYPE,
    id = 84,
    rootId = "A3N5cwBU",
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
"accountType" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
"enabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
"requirePasswordUpdate" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
"salt" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"userEncClientKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
"verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("alarmInfoList" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserAlarmInfoListTypeTypeModel",
	final = false,
	external = false
),
"auth" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserAuthenticationTypeModel",
	final = true,
	external = false
),
"authenticatedDevices" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "AuthenticatedDeviceTypeModel",
	final = true,
	external = false
),
"externalAuthInfo" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserExternalAuthInfoTypeModel",
	final = true,
	external = false
),
"memberships" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "GroupMembershipTypeModel",
	final = true,
	external = false
),
"phoneNumbers" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "PhoneNumberTypeModel",
	final = true,
	external = false
),
"pushIdentifierList" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "PushIdentifierListTypeModel",
	final = false,
	external = false
),
"userGroup" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "GroupMembershipTypeModel",
	final = true,
	external = false
),
"customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "CustomerTypeModel",
	final = true,
	external = false
),
"failedLogins" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "LoginTypeModel",
	final = true,
	external = false
),
"secondFactorAuthentications" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "SecondFactorAuthenticationTypeModel",
	final = true,
	external = false
),
"successfulLogins" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "LoginTypeModel",
	final = true,
	external = false
))
)
