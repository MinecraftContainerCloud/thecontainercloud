plugins {
    id("java")
    application
}

group = "de.theccloud"
version = "1.0-SNAPSHOT"

val mainClassPath = "${group}.thecontainercloud.Main"

repositories {
    mavenCentral()
}

dependencies {
    // Cassandra
    implementation("com.datastax.cassandra:cassandra-driver-core:3.0.8")
    implementation("com.datastax.cassandra:cassandra-driver-mapping:3.0.8")
    implementation("com.datastax.cassandra:cassandra-driver-extras:3.0.8")

    implementation(project(":api"))

    // Javalin
    implementation("io.javalin:javalin:5.6.3")

    implementation("org.slf4j:slf4j-simple:2.0.7")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")

    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.withType(Jar::class) {
    manifest {
        attributes["Manifest-Version"] = version
        attributes["Main-Class"] = mainClassPath
    }
}

tasks {
    application {
        mainClass = mainClassPath
    }
}