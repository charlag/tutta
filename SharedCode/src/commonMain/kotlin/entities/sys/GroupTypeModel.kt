package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val GroupTypeModel: TypeModel = TypeModel(
	  name = "Group",
	  encrypted = false,
    type = ELEMENT_TYPE,
    id = 5,
    rootId = "A3N5cwAF",
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
"adminGroupEncGKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"enabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
"external" to Value(
  type = BooleanType,
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
	  associations = mapOf("administratedGroups" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "AdministratedGroupsRefTypeModel",
	final = true,
	external = false
),
"keys" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "KeyPairTypeModel",
	final = true,
	external = false
),
"admin" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "GroupTypeModel",
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
"groupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfoTypeModel",
	final = true,
	external = false
),
"invitations" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupInvitationTypeModel",
	final = true,
	external = false
),
"members" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupMemberTypeModel",
	final = true,
	external = false
),
"user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "UserTypeModel",
	final = true,
	external = false
))
)
