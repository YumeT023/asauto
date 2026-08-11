plugins {
    id("java")
}

group = "io.asall"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.microsoft.playwright:playwright:1.62.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.1")

    implementation("org.slf4j:slf4j-api:2.0.9")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.9")

    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "io.asall.auto.AsallAuto"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.map { cp ->
        cp.files.map { if (it.isDirectory) it else zipTree(it) }
    })
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
}