package com.pgleqi

import com.pgleqi.controller.plugins.configureHTTP
import com.pgleqi.controller.plugins.configureRouting
import com.pgleqi.controller.plugins.configureSerialization
import com.pgleqi.service.AppService
import io.ktor.client.*
import io.ktor.server.testing.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureHTTP()
            configureSerialization()
            configureRouting()
        }
    }

    private fun buildContext(
        block: (
            client: HttpClient,
            applicationTestBuilder: ApplicationTestBuilder,
        ) -> Unit
    ) {
        testApplication {
            application {
                configureHTTP()
                configureSerialization()
                configureRouting()
            }
            loadAppSettingsFromEnv()

            val client = createClient {
            }

            block(client, this)
        }
    }

    private fun loadAppSettingsFromEnv() {
        val cookie = System.getenv("CLAUDE_API_COOKIE")
        AppService.appSettings.cookie = cookie
    }

    @Test
    fun `when get organization id, should success`() {
        buildContext { _, _ ->
            runBlocking {
                AppService.getOrganizationId(this.coroutineContext)
                delay(1000)
                assertTrue(AppService.appSettings.cookie.isNotEmpty())
            }
        }
    }

    @Test
    fun `when get all conversations, should success`() {
    }
}
