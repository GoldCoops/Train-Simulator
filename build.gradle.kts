plugins {
    id("java")
    application
}

group = "com.trains"
version = providers.gradleProperty("releaseVersion").getOrElse("1.1")

repositories {
    mavenCentral()
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("com.trains.App")
    mainModule.set("com.trains")
}

tasks.test {
    useJUnitPlatform()
}

// The application plugin doesn't touch the jar manifest, so without this the jar
// builds fine but `java -jar` fails with "no main manifest attribute".
tasks.jar {
    manifest {
        attributes("Main-Class" to application.mainClass.get())
    }
}