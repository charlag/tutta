package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val UserAreaGroupsTypeModel: TypeModel = TypeModel(
	  name = "UserAreaGroups",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 988,
    rootId = "A3N5cwAD3A",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("list" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfoTypeModel",
	final = true,
	external = false
))
)
