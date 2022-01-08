plugins {
    id("java")
    id("kotlin")
    id("org.jetbrains.kotlin.jvm")
}

group = "io.evlikat.games.card-games"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
    testImplementation("org.assertj:assertj-core:3.20.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}