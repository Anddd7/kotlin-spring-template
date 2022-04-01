import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter.ofPattern

/** -------------- project's properties -------------- */

group = "com.github.anddd7"
version = "0.0.1-SNAPSHOT"

repositories {
  mavenCentral()
  maven { url = uri("https://repo.spring.io/milestone") }
  jcenter()
}

buildscript {
  repositories {
    jcenter()
  }
}

java.sourceCompatibility = JavaVersion.VERSION_11

/** -------------- import & apply plugins -------------- */

// import plugins into this project
plugins {
  val kotlinVersion = "1.6.10"

  // core plugins, which is already include in plugin dependencies spec
  idea
  java
  jacoco

  kotlin("jvm") version kotlinVersion
  kotlin("plugin.spring") version kotlinVersion
  kotlin("plugin.jpa") version kotlinVersion

  /**
   * binary(external) plugins, provide id and version to resolve it
   * base plugin for spring-boot, provide plugins and tasks
   */
  id("org.springframework.boot") version "2.6.3"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"

  id("org.flywaydb.flyway") version "7.5.2"

  id("org.jlleitschuh.gradle.ktlint") version "10.0.0"

  id("org.owasp.dependencycheck") version "6.1.6"
}

/** -------------- configure imported plugin -------------- */

val apiSourceSet = sourceSets.create("apiTest") {
  withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("src/apiTest/kotlin")
    resources.srcDir("src/apiTest/resources")
  }

  val testSourceSet = sourceSets.test.get()

  compileClasspath += testSourceSet.runtimeClasspath
  runtimeClasspath += testSourceSet.runtimeClasspath
}

idea {
  project {
    jdkName = "11"
  }
  module {
    outputDir = file("$buildDir/idea-compiler/main")
    testOutputDir = file("$buildDir/idea-compiler/test")

    apiSourceSet.withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
      testSourceDirs = testSourceDirs + kotlin.srcDirs
      testResourceDirs = testResourceDirs + resources.srcDirs
    }
  }
}

flyway {
  url = "jdbc:postgresql://localhost:5432/local?user=test&password=test"
}

jacoco {
  toolVersion = "0.8.7"
}

allOpen {
  annotation("javax.persistence.Entity")
  annotation("javax.persistence.Embeddable")
  annotation("javax.persistence.MappedSuperclass")
}

/** -------------- dependencies management -------------- */

dependencies {
  /**
   * `compile` is deprecated;
   *
   * `implementation` can only access by this project
   * `api` can access by top-level project which include this project
   */
  /* kotlin */
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  /* kotlin test */
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testImplementation("io.mockk:mockk:1.12.2")
  testImplementation("org.assertj:assertj-core:3.21.0")

  /* kotlin coroutines */
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

  /* spring mvc */
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(module = "junit")
    exclude(group = "org.mockito")
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
  }
  testImplementation("com.ninja-squad:springmockk:3.1.0")
  /* security */
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("io.jsonwebtoken:jjwt:0.9.1")
  testImplementation("org.springframework.security:spring-security-test")

  /* monitoring x logging */
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("net.logstash.logback:logstash-logback-encoder:7.0")

  /* jsr107 cache */
  implementation("org.springframework.boot:spring-boot-starter-cache")
  implementation("javax.cache:cache-api")
  implementation("org.ehcache:ehcache")

  /* openApi && swagger */
  implementation("org.springdoc:springdoc-openapi-ui:1.5.13")
  implementation("org.springdoc:springdoc-openapi-kotlin:1.5.13")

  /* db */
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("com.vladmihalcea:hibernate-types-52:2.10.2")
  runtimeOnly("org.flywaydb:flyway-core")
  runtimeOnly("org.postgresql:postgresql")

  /* mock db x server */
  testImplementation("io.zonky.test:embedded-database-spring-test:2.1.1")
  testRuntimeOnly("org.testcontainers:postgresql:1.16.2")
  testImplementation("com.github.tomakehurst:wiremock:2.27.2")

  /* architecture verification */
  testImplementation("com.tngtech.archunit:archunit-junit5-api:0.22.0")
  testRuntimeOnly("com.tngtech.archunit:archunit-junit5-engine:0.22.0")

  /* utils */
  implementation("com.google.guava:guava:31.0-jre")
}

/** -------------- configure tasks -------------- */

tasks.register<Test>("apiTest") {
  description = "Runs the api tests."
  group = "verification"
  testClassesDirs = sourceSets["apiTest"].output.classesDirs
  classpath = sourceSets["apiTest"].runtimeClasspath
  mustRunAfter(tasks["test"])
}

tasks.withType<KotlinCompile>().all {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

task("newMigration") {
  group = "flyway"
  description = """
        ./gradlew newMigration -Ptype=<ddl,dml> -Poperation=<operation>. Please ensure you already have dir `db/migration`
        """.trim()

  doLast {
    val (operation, type) = properties["operation"] to properties["type"]
    val resourcesPath = sourceSets["main"].resources.sourceDirectories.singleFile.path
    val timestamp = now().format(ofPattern("yyyyMMddHHmm"))
    val filename = "V${timestamp}__${type}_$operation.sql"
    val filepath = "$resourcesPath/db/migration/$filename"
    File(filepath).takeIf { it.createNewFile() }?.appendText("-- script")
  }
}
