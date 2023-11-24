package com.pgleqi.controller.plugins

import com.pgleqi.service.AppService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/conversations") {
            call.respond(AppService.getAllConversations())
        }

        post("/conversations") {
            call.respond(AppService.createConversation() ?: "null")
        }

        get("/conversation-history/{uuid}") {
            call.parameters["uuid"]?.let { uuid ->
                call.respond(AppService.getConversationHistory(uuid))
            } ?: call.response.status(HttpStatusCode.BadRequest)
        }

        delete("/conversation/{uuid}") {
            call.parameters["uuid"]?.let { uuid ->
                val isSuccess = AppService.deleteConversation(uuid)
                call.response.status(if (isSuccess) HttpStatusCode.OK else HttpStatusCode.InternalServerError)
            } ?: call.response.status(HttpStatusCode.BadRequest)
        }

        post("/conversation/{uuid}") {
            call.parameters["uuid"]?.let { uuid ->
                val message = call.receiveText()
                call.respondTextWriter {
                    AppService.sendMessage(uuid, message, this)
                }
            }
        }

        post("/generate-conversation-title/{uuid}") {
            call.parameters["uuid"]?.let { uuid ->
                val message = call.receiveText()
                call.respondText(AppService.generateTitle(uuid, message))
            }
        }
    }
}
