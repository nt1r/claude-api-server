package com.pgleqi.controller.plugins

import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/hello-world", {
            description = "Hello World Endpoint."
            response {
                HttpStatusCode.OK to {
                    description = "Successful request."
                    body<String> { description = "Response body" }
                }
                HttpStatusCode.InternalServerError to {
                    description = "Internal server error."
                }
            }
        }) {
            call.respondText("Hello World!")
        }
    }
}
