package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val RegistrationServiceDataTypeModel: TypeModel = TypeModel(
	  name = "RegistrationServiceData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 316,
    rootId = "A3N5cwABPA",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"starterDomain" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"source" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"state" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
