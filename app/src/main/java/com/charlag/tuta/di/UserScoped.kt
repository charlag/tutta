package com.charlag.tuta.di

import javax.inject.Qualifier
import javax.inject.Scope

@Scope
annotation class UserScoped

@Qualifier
annotation class UserBound

@Qualifier
annotation class NonAuthenticated