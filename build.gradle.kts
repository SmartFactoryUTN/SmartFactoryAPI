plugins {
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.1.5"
	id("org.flywaydb.flyway") version "10.18.2"
	id("jacoco")
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
	kotlin("plugin.jpa") version "1.9.24"
	kotlin("plugin.serialization") version "1.9.24"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.3")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
	implementation("software.amazon.awssdk:lambda:2.20.50")
	implementation("software.amazon.awssdk:core:2.20.50")
	implementation("io.github.oshai:kotlin-logging-jvm:5.1.4")
	implementation("org.flywaydb:flyway-core:10.18.2")
	implementation("io.projectreactor:reactor-core:3.6.10")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.1")
	implementation("org.jsoup:jsoup:1.15.3") // SVG Sanitization

	// Auth0
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.security:spring-security-oauth2-resource-server")
	implementation("org.springframework.security:spring-security-oauth2-jose")
	implementation("org.springframework.security:spring-security-config")
	implementation("org.springframework.security:spring-security-core")
	implementation("org.springframework.security:spring-security-web")


	// Use the latest stable version of the AWS SDK for Kotlin
	implementation("aws.sdk.kotlin:s3:0.20.1-beta"){
		exclude("com.squareup.okhttp3:okhttp")
	}
	implementation("aws.sdk.kotlin:aws-core-jvm:0.20.1-beta"){
		exclude("com.squareup.okhttp3:okhttp")
	}
	// OkHttp dependency that matches the AWS SDK for Kotlin requirements
	implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
	runtimeOnly("org.flywaydb:flyway-mysql:10.18.2")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.mockito", module = "mockito-core")
	}
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	// Spring Boot WebMvcTest
	testImplementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("io.mockk:mockk:1.13.12")
	// JUnit 5
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
	// H2 for testing
	testImplementation("com.h2database:h2:2.3.232")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	runtimeOnly("mysql:mysql-connector-java:8.0.32") //compatible with AWS Aurora RDS Mysql
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
	jvmToolchain(21)
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.jar {
	manifest {
		attributes["Main-Class"] = "com.example.SmartFactoryAPI"
	}
	configurations["compileClasspath"].forEach { file: File ->
		from(zipTree(file.absoluteFile))
		duplicatesStrategy = DuplicatesStrategy.INCLUDE
	}
}
