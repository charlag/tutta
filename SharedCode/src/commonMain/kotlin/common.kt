package com.charlag.tuta

import com.charlag.tuta.entities.Entity
import com.charlag.tuta.entities.TypeInfo
import com.charlag.tuta.entities.sys.typeInfo
import kotlin.collections.set
import kotlin.reflect.KClass

expect fun platformName(): String

expect val KClass<*>.noReflectionName: String

val typemodelMap: Map<String, TypeInfo<out Entity>> by lazy {
    val typeModelMap = mutableMapOf<String, TypeInfo<*>>()
    // TODO: improve lookup so that names cannot collide
    for ((name, typeInfo) in typeInfo) {
        typeModelMap[typeInfo.typemodel.name] = typeInfo
    }
    for ((name, typeInfo) in com.charlag.tuta.entities.tutanota.typeInfo) {
        typeModelMap[typeInfo.typemodel.name] = typeInfo
    }
    @Suppress("UNCHECKED_CAST") // we can remove it when we generate everything as Entity
    typeModelMap as Map<String, TypeInfo<out Entity>>
}