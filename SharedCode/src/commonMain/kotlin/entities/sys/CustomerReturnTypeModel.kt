package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CustomerReturnTypeModel: TypeModel = TypeModel(
	  name = "CustomerReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 370,
    rootId = "A3N5cwABcg",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("adminUser" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "UserTypeModel",
	final = false,
	external = false
),
"adminUserGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupTypeModel",
	final = false,
	external = false
))
)
