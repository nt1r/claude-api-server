package com.pgleqi.controller.plugins

import com.pgleqi.service.AppService
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/conversations", {
            description = "Get all conversations Endpoint."
        }) {
            val conversations = AppService.getAllConversations()
            call.respond(conversations)
        }

        get("/history/{uuid}", {
            description = "Get conversation history Endpoint."
        }) {
            call.parameters["uuid"]?.let { uuid ->
                call.respond(AppService.getConversationHistory(uuid))
            } ?: call.response.status(HttpStatusCode.BadRequest)
        }
    }
}
