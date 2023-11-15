package com.pgleqi.constant

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder

// Gson
val gsonBuilder: GsonBuilder = GsonBuilder().apply {
    setPrettyPrinting()
    setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
}
val gson: Gson = gsonBuilder.create()