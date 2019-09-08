package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val GroupMembershipTypeModel: TypeModel = TypeModel(
	  name = "GroupMembership",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 25,
    rootId = "A3N5cwAZ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"admin" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
"groupType" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"symEncGKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupTypeModel",
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
"groupMember" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupMemberTypeModel",
	final = true,
	external = false
))
)
