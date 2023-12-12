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
    implementation("com.datastax.cassandra:cassandra-driver-core:3.0.8")
    implementation("com.datastax.cassandra:cassandra-driver-mapping:3.0.8")
    implementation("com.datastax.cassandra:cassandra-driver-extras:3.0.8")
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