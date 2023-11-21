package com.pgleqi

import com.pgleqi.controller.plugins.configureHTTP
import com.pgleqi.controller.plugins.configureRouting
import com.pgleqi.controller.plugins.configureSerialization
import com.pgleqi.service.AppService
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import io.ktor.server.testing.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.BeforeClass
import kotlin.test.Test
import kotlin.test.assertTrue

class ApplicationTest {
    companion object {
        val client: HttpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson() {
                    setPrettyPrinting()
                }
            }
        }

        @BeforeClass
        @JvmStatic
        fun setup() {
            loadAppSettingsFromEnv()
            runBlocking {
                AppService.getOrganizationId(this.coroutineContext)
                delay(1000)
            }
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
        }

        private fun loadAppSettingsFromEnv() {
            val cookie = System.getenv("CLAUDE_API_COOKIE")
            AppService.appSettings.cookie = cookie
        }
    }

    private fun buildContext(block: () -> Unit) {
        testApplication {
            application {
                configureHTTP()
                configureSerialization()
                configureRouting()
            }
            block()
        }
    }

    /*@Test
    fun `when get organization id, should success`() {
        runBlocking {
            assertTrue(AppService.appSettings.cookie.isNotEmpty())
        }
    }*/

    /*@Test
    fun `when get all conversations, should success`() {
        buildContext {
            runBlocking {
                val response = client.get("/conversations")
                assertTrue(response.status.isSuccess())
                val conversationList = response.body() as List<Conversation>
                assertTrue(conversationList.isNotEmpty())
            }
        }
    }*/
}
