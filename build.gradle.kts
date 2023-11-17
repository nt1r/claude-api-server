val kotlinVersion: String by project
val logbackVersion: String by project
val swaggerUiVersion: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin")
}

group = "com.pgleqi"
version = "0.0.1"

application {
    mainClass.set("com.pgleqi.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // ktor server
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-gson-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // ktor client
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")

    // swagger ui
    implementation("io.github.smiley4:ktor-swagger-ui:$swaggerUiVersion")


    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}
