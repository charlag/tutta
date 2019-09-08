package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val UpdateAdminshipDataTypeModel: TypeModel = TypeModel(
	  name = "UpdateAdminshipData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1288,
    rootId = "A3N5cwAFCA",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"newAdminGroupEncGKey" to Value(
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
"newAdminGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupTypeModel",
	final = true,
	external = false
))
)
