package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CreateGroupListDataTypeModel: TypeModel = TypeModel(
	  name = "CreateGroupListData",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 365,
    rootId = "A3N5cwABbQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"adminEncGroupInfoListKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"customerEncGroupInfoListKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("createGroupData" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "CreateGroupDataTypeModel",
	final = false,
	external = false
))
)
