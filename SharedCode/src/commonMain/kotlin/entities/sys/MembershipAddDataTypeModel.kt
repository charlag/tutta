package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val MembershipAddDataTypeModel: TypeModel = TypeModel(
	  name = "MembershipAddData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 505,
    rootId = "A3N5cwAB-Q",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"symEncGKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupTypeModel",
	final = false,
	external = false
),
"user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "UserTypeModel",
	final = false,
	external = false
))
)
