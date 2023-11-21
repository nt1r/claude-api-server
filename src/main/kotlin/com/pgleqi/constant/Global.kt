package com.pgleqi.constant

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.gson.*

// gson
val gsonBuilder: GsonBuilder = GsonBuilder().apply {
    setPrettyPrinting()
    setLenient()
    setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
}
val gson: Gson = gsonBuilder.create()

// http client
val baseHttpClient: HttpClient = HttpClient(CIO) {
    engine {
        requestTimeout = 0
    }

    install(ContentNegotiation) {
        gson() {
            setPrettyPrinting()
        }
    }
}.apply {
    headers {
        append(
            HttpHeaders.UserAgent,
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"
        )
        append(HttpHeaders.AcceptLanguage, "en-US,en;q=0.5")
        append(HttpHeaders.ContentType, "application/json")
        append(HttpHeaders.Referrer, referrerUrl)
        append("Sec-Fetch-Dest", "empty")
        append("Sec-Fetch-Mode", "cors")
        append("Sec-Fetch-Site", "same-origin")
        append(HttpHeaders.Connection, "keep-alive")
    }
}