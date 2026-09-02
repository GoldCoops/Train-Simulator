plugins {
    id("java")
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "4.1.1"
}

group = "com.trains"
version = "1.0"

repositories {
    mavenCentral()
}

javafx {
    version = "21" //javafx version should match the JavaVersion set below
    modules("javafx.controls", "javafx.graphics")
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



jlink {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "COMP2000-Semester-1-Project"
    }
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
            "https://openjfx.io/javadoc/21/",
        )
    }
}
