package com.pgleqi

import com.pgleqi.controller.plugins.*
import com.pgleqi.service.AppService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8001, host = "127.0.0.1", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureRouting()
    configureSwaggerUi()

    AppService.loadAppSettings()
}
