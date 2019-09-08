package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val MailAddressAliasTypeModel: TypeModel = TypeModel(
	  name = "MailAddressAlias",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 684,
    rootId = "A3N5cwACrA",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"enabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
"mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf()
)
