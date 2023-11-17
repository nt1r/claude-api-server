package com.pgleqi.service

import com.google.gson.JsonParser
import com.pgleqi.constant.*
import com.pgleqi.model.AppSettings
import com.pgleqi.model.Conversation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object AppService {
    private lateinit var appSettings: AppSettings

    private var organizationId: String = ""

    internal fun init() {
        loadAppSettings()
        getOrganizationId()
    }

    private fun loadAppSettings() {
        appSettings = FileService.readTextFile(FileService.APP_SETTINGS_FILE_PATH)?.let { json ->
            gson.fromJson(json, AppSettings::class.java)
        } ?: AppSettings()
    }

    private fun getOrganizationId() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = baseHttpClient.get(organizationUrl) {
                    headers {
                        append(HttpHeaders.Cookie, appSettings.cookie)
                    }
                }
                if (response.status != HttpStatusCode.OK) {
                    println("getOrganizationId network error!")
                    return@launch
                }

                val responseText = response.bodyAsText()
                organizationId = JsonParser.parseString(responseText).asJsonArray[0].asJsonObject.get("uuid").asString
            } catch (e: Exception) {
                println(e)
                println("Check if you are in unavailable region!")
            }
        }
    }

    suspend fun getAllConversations(): List<Conversation> {
        try {
            val response = baseHttpClient.get(conversationUrl.format(organizationId)) {
                headers {
                    append(HttpHeaders.Cookie, appSettings.cookie)
                }
            }
            if (response.status != HttpStatusCode.OK) {
                println("getAllConversations network error!")
                return emptyList()
            }

            return gson.fromJson<List<Conversation>>(response.bodyAsText(), gsonListTypeToken<Conversation>()).toList()
        } catch (e: Exception) {
            println(e)
            return emptyList()
        }
    }
}