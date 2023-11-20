package com.pgleqi.controller.plugins

import com.pgleqi.service.AppService
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/conversations", {
            description = "Get all conversations Endpoint."
        }) {
            call.respond(AppService.getAllConversations())
        }

        post("/conversations", {
            description = "Create new conversation Endpoint."
        }) {
            call.respond(AppService.createConversation() ?: "null")
        }

        get("/history/{uuid}", {
            description = "Get conversation history Endpoint."
        }) {
            call.parameters["uuid"]?.let { uuid ->
                call.respond(AppService.getConversationHistory(uuid))
            } ?: call.response.status(HttpStatusCode.BadRequest)
        }

        delete("/conversation/{uuid}", {
            description = "Delete conversation Endpoint."
        }) {
            call.parameters["uuid"]?.let { uuid ->
                val isSuccess = AppService.deleteConversation(uuid)
                call.response.status(if (isSuccess) HttpStatusCode.OK else HttpStatusCode.InternalServerError)
            } ?: call.response.status(HttpStatusCode.BadRequest)
        }
    }
}
