import sun.jvmstat.monitor.MonitoredVmUtil.mainClass

plugins {
    id("java")
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.trains"
version = "1.0"

repositories {
    mavenCentral()
}

javafx {
    version = "21" //javafx version should match the JavaVersion set below
    modules("javafx.controls", "javafx.fxml", "javafx.graphics")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21 //Should match javafx version
    targetCompatibility = JavaVersion.VERSION_21
}


dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("com.trains.App")
}


tasks.test {
    useJUnitPlatform()
}