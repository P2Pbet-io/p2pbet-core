import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.8"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"
    kotlin("plugin.jpa") version "1.6.0"
    kotlin("kapt") version "1.6.0"
    id("com.github.b3er.local.properties") version "1.1"
}

group = "com.p2pbet"
version = "1.0-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17


allprojects {
    repositories {
        mavenLocal()
        mavenCentral()

        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://gitlab.com/api/v4/projects/38931176/packages/maven")
            name = "Gitlab"
            credentials(HttpHeaderCredentials::class) {
                name = "Private-Token"
                value = project.properties["gitlabToken"] as String
            }
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
        }
        maven {
            url = uri("https://gitlab.com/api/v4/projects/38931176/packages/maven")
            name = "Gitlab"
            credentials(HttpHeaderCredentials::class.java) {
                name = "Job-Token"
                value = System.getenv("CI_JOB_TOKEN")
            }
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
        }
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2020.0.4")
    }
    dependencies {
        dependency("event-notification:event-notification-api:1.0")
    }
}

extra["log4j2.version"] = "2.17.1"


dependencies {
    val configurationProcessor = "org.springframework.boot:spring-boot-configuration-processor"
    kapt(configurationProcessor)
    kaptTest(configurationProcessor)
    annotationProcessor(configurationProcessor)

    api("event-notification:event-notification-api")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-activemq")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.security:spring-security-web")

    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.github.microutils:kotlin-logging:2.1.21")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:3.1.0")

    implementation("org.postgresql:postgresql:42.3.3")
    implementation("org.liquibase:liquibase-core:4.9.0")
    implementation("com.vladmihalcea:hibernate-types-52:2.16.2")

    implementation("org.web3j:core:4.8.9")
    implementation("org.bitcoinj:bitcoinj-core:0.15.6")

    implementation("org.apache.commons:commons-csv:1.8")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("org.keycloak:keycloak-admin-client:11.0.3")

    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql:1.16.3")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("io.mockk:mockk:1.12.1")
}

kapt {
    correctErrorTypes = true
    includeCompileClasspath = false
    annotationProcessor("org.springframework.boot.configurationprocessor.ConfigurationMetadataAnnotationProcessor")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
