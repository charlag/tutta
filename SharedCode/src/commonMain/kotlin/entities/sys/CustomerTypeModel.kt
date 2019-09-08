package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CustomerTypeModel: TypeModel = TypeModel(
	  name = "Customer",
	  encrypted = false,
    type = ELEMENT_TYPE,
    id = 31,
    rootId = "A3N5cwAf",
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
"approvalStatus" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"canceledPremiumAccount" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
"orderProcessingAgreementNeeded" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
"type" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("auditLog" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "AuditLogRefTypeModel",
	final = true,
	external = false
),
"contactFormUserAreaGroups" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserAreaGroupsTypeModel",
	final = true,
	external = false
),
"contactFormUserGroups" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserAreaGroupsTypeModel",
	final = true,
	external = false
),
"customizations" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "FeatureTypeModel",
	final = false,
	external = false
),
"userAreaGroups" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserAreaGroupsTypeModel",
	final = true,
	external = false
),
"whitelabelChildren" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "WhitelabelChildrenRefTypeModel",
	final = true,
	external = false
),
"whitelabelParent" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "WhitelabelParentTypeModel",
	final = true,
	external = false
),
"adminGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupTypeModel",
	final = true,
	external = false
),
"adminGroups" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfoTypeModel",
	final = true,
	external = false
),
"customerGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupTypeModel",
	final = true,
	external = false
),
"customerGroups" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfoTypeModel",
	final = true,
	external = false
),
"customerInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "CustomerInfoTypeModel",
	final = true,
	external = false
),
"orderProcessingAgreement" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "OrderProcessingAgreementTypeModel",
	final = true,
	external = false
),
"properties" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "CustomerPropertiesTypeModel",
	final = true,
	external = false
),
"serverProperties" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "CustomerServerPropertiesTypeModel",
	final = true,
	external = false
),
"teamGroups" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfoTypeModel",
	final = true,
	external = false
),
"userGroups" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfoTypeModel",
	final = true,
	external = false
))
)
