package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val WhitelabelParentTypeModel: TypeModel = TypeModel(
	  name = "WhitelabelParent",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1272,
    rootId = "A3N5cwAE-A",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "CustomerTypeModel",
	final = true,
	external = false
),
"whitelabelChildInParent" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "WhitelabelChildTypeModel",
	final = true,
	external = false
))
)
