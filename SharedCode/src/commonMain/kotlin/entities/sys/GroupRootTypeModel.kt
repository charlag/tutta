package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val GroupRootTypeModel: TypeModel = TypeModel(
	  name = "GroupRoot",
	  encrypted = false,
    type = ELEMENT_TYPE,
    id = 110,
    rootId = "A3N5cwBu",
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
)),
	  associations = mapOf("externalUserAreaGroupInfos" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserAreaGroupsTypeModel",
	final = true,
	external = false
),
"externalGroupInfos" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfoTypeModel",
	final = true,
	external = false
),
"externalUserReferences" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "ExternalUserReferenceTypeModel",
	final = true,
	external = false
))
)
