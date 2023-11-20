package com.pgleqi.service

import com.google.gson.JsonParser
import com.pgleqi.constant.*
import com.pgleqi.model.AppSettings
import com.pgleqi.model.ChatMessage
import com.pgleqi.model.Conversation
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext


object AppService {
    lateinit var appSettings: AppSettings

    private var organizationId: String = ""

    internal fun init() {
        loadAppSettings()
        getOrganizationId()
    }

    fun loadAppSettings() {
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
                if (!response.status.isSuccess()) {
                    println("getOrganizationId network error!")
                    return@launch
                }

                val responseText = response.bodyAsText()
                organizationId = JsonParser.parseString(responseText).asJsonArray[0].asJsonObject.get("uuid").asString
                println("Organization ID: $organizationId")
            } catch (e: Exception) {
                println(e)
                println("Check if you are in unavailable region!")
            }
        }
    }

    internal suspend fun getAllConversations(): List<Conversation> {
        try {
            val response = baseHttpClient.get(allConversationsUrl.format(organizationId)) {
                headers {
                    append(HttpHeaders.Cookie, appSettings.cookie)
                }
            }
            if (!response.status.isSuccess()) {
                println("getAllConversations network error!")
                return emptyList()
            }

            return gson.fromJson(response.bodyAsText(), Array<Conversation>::class.java).toList()
        } catch (e: Exception) {
            println(e)
            return emptyList()
        }
    }

    internal suspend fun getConversationHistory(uuid: String): List<ChatMessage> {
        try {
            val response = baseHttpClient.get(conversationUrl.format(organizationId, uuid)) {
                headers {
                    append(HttpHeaders.Cookie, appSettings.cookie)
                }
            }
            if (!response.status.isSuccess()) {
                println("getConversationHistory network error!")
                return emptyList()
            }

            val messagesJson = JsonParser.parseString(response.bodyAsText()).asJsonObject.get("chat_messages").asJsonArray.toString()
            return gson.fromJson(messagesJson, Array<ChatMessage>::class.java).toList()
        } catch (e: Exception) {
            println(e)
            return emptyList()
        }
    }

    internal suspend fun createConversation(): Conversation? {
        try {
            val uuid = generateUUID()
            val response = baseHttpClient.post(allConversationsUrl.format(organizationId)) {
                headers {
                    append(HttpHeaders.Cookie, appSettings.cookie)
                }

                contentType(ContentType.Application.Json)
                setBody(Conversation(uuid = uuid, name = ""))
            }

            if (!response.status.isSuccess()) {
                println("createConversation network error!")
                println(response.bodyAsText())
                return null
            }

            return response.body()
        } catch (e: Exception) {
            println(e)
            return null
        }
    }

    internal suspend fun deleteConversation(uuid: String): Boolean {
        try {
            val response = baseHttpClient.delete(conversationUrl.format(organizationId, uuid)) {
                headers {
                    append(HttpHeaders.Cookie, appSettings.cookie)
                }

                setBody(uuid)
            }
            if (!response.status.isSuccess()) {
                println("deleteConversation network error!")
                return false
            }
            return true
        } catch (e: Exception) {
            println(e.message)
            return false
        }
    }

    internal suspend fun sendMessage() {

    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}