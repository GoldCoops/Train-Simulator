plugins {
    id("java")
    application
}

group = "com.trains"
version = "1.0"

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

tasks.javadoc {
    (options as StandardJavadocDocletOptions).apply {
        encoding = "UTF-8"
        charSet = "UTF-8"
        windowTitle = "${rootProject.name} API"
        docTitle = "${rootProject.name} API"
        addStringOption("Xdoclint:none", "-quiet")

        // module-info only exports com.trains, and javadoc's module-mode default is
        // --show-packages exported — without these the other packages are omitted.
        addStringOption("-show-packages", "all")
        addStringOption("-show-module-contents", "all")

        links(
            "https://docs.oracle.com/en/java/javase/21/docs/api/",
        )
    }
}
