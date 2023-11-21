package com.pgleqi.service

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.pgleqi.constant.*
import com.pgleqi.model.AppSettings
import com.pgleqi.model.dto.ChatMessageDto
import com.pgleqi.model.dto.ConversationDto
import com.pgleqi.model.dto.TokenStreamDto
import com.pgleqi.model.payload.AppendMessagePayload
import com.pgleqi.model.payload.Completion
import com.pgleqi.model.payload.GenerateTitlePayload
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Writer
import java.util.*
import kotlin.coroutines.CoroutineContext


object AppService {
    val appSettings: AppSettings = AppSettings()

    private var organizationId: String = ""

    internal fun init() {
        loadAppSettings()
        getOrganizationId(Dispatchers.IO)
    }

    private fun loadAppSettings() {
        appSettings.cookie = FileService.readTextFile(FileService.APP_SETTINGS_FILE_PATH)?.let { json ->
            gson.fromJson(json, AppSettings::class.java).cookie
        } ?: ""
    }

    fun getOrganizationId(coroutineContext: CoroutineContext) {
        CoroutineScope(coroutineContext).launch {
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

    internal suspend fun getAllConversations(): List<ConversationDto> {
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

            return gson.fromJson(response.bodyAsText(), Array<ConversationDto>::class.java).toList()
        } catch (e: Exception) {
            println(e)
            return emptyList()
        }
    }

    internal suspend fun getConversationHistory(uuid: String): List<ChatMessageDto> {
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
            return gson.fromJson(messagesJson, Array<ChatMessageDto>::class.java).toList()
        } catch (e: Exception) {
            println(e)
            return emptyList()
        }
    }

    internal suspend fun createConversation(): ConversationDto? {
        try {
            val uuid = generateUuid()
            val response = baseHttpClient.post(allConversationsUrl.format(organizationId)) {
                headers {
                    append(HttpHeaders.Cookie, appSettings.cookie)
                }

                contentType(ContentType.Application.Json)
                setBody(ConversationDto(uuid = uuid, name = ""))
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

    internal suspend fun generateTitle(uuid: String, message: String): String {
        try {
            val payload = GenerateTitlePayload(
                organizationUuid = organizationId,
                conversationUuid = uuid,
                messageContent = message,
            )
            /*val payloadLength = gson.toJson(payload).toByteArray().size
            println(payloadLength)*/

            val response = baseHttpClient.post(generateTitleUrl) {
                headers {
                    append(HttpHeaders.Accept, "*/*")
                    /*append(HttpHeaders.AcceptEncoding, "gzip, deflate, br")
                    append(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7")*/
                    /*append(HttpHeaders.ContentType, "application/json")*/
                    /*append(HttpHeaders.ContentLength, payloadLength.toString())*/
                    append(HttpHeaders.Referrer, chatReferrerUrl.format(uuid))
                    append(HttpHeaders.Origin, "https://claude.ai")
                    append(HttpHeaders.Cookie, appSettings.cookie)
                }
                setBody(payload)
            }
            println(response.request.headers)
            if (!response.status.isSuccess()) {
                println("generateTitle network error!")
                println(response.bodyAsText())
                return ""
            }

            println(response.bodyAsText())
            return (JsonParser.parseString(response.bodyAsText()) as JsonObject).get("title").asString
        } catch (e: Exception) {
            println(e)
            return ""
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

    internal suspend fun sendMessage(uuid: String, message: String, writer: Writer) {
        try {
            baseHttpClient.preparePost(chatMessageUrl) {
                headers {
                    append(HttpHeaders.Accept, "text/event-stream, text/event-stream")
                    append(HttpHeaders.Referrer, chatReferrerUrl.format(uuid))
                    append(HttpHeaders.Origin, "https://claude.ai")
                    append(HttpHeaders.Cookie, appSettings.cookie)
                }

                setBody(
                    gson.toJson(
                        AppendMessagePayload(
                            completion = Completion(prompt = message),
                            conversationUuid = uuid,
                            organizationUuid = organizationId,
                            text = message,
                        )
                    )
                )
            }.execute { response ->
                if (!response.status.isSuccess()) {
                    println("sendMessage network error!")
                    println(response.bodyAsText())
                    return@execute
                }

                val channel: ByteReadChannel = response.bodyAsChannel()
                while (!channel.isClosedForRead) {
                    val lineStr = channel.readUTF8Line() ?: ""
                    if (lineStr.isNotEmpty()) {
                        val dataJson = lineStr.substring("data: ".length)
                        val tokenDto = gson.fromJson(dataJson, TokenStreamDto::class.java)
                        val tidyJson = gson.toJson(tokenDto)
                        println(tidyJson)
                        writer.write("$tidyJson\n")
                    }
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private fun generateUuid(): String {
        return UUID.randomUUID().toString()
    }
}