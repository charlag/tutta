package com.charlag.tuta

import kotlinx.cinterop.CVariable
import kotlinx.cinterop.convert
import kotlinx.cinterop.ptr
import kotlinx.cinterop.sizeOf
import platform.posix.memset

inline fun <reified T : CVariable>T.zeroOut() = memset(this.ptr, 0, sizeOf<T>().convert())