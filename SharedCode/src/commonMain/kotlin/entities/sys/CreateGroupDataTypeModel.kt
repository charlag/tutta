package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CreateGroupDataTypeModel: TypeModel = TypeModel(
	  name = "CreateGroupData",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 356,
    rootId = "A3N5cwABZA",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"adminEncGKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"customerEncUserGroupInfoSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"encryptedName" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"listEncSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"mailAddress" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"pubKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"symEncGKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
"symEncPrivKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
