plugins {
    kotlin("jvm") version "1.5.0-M1"
    id("com.adarshr.test-logger") version "2.1.1"
    id("org.graalvm.plugin.compiler") version "0.1.0-alpha2"
}

group = "it.skrape"
version = "1.0.0"

val graalVmVersion = "21.0.0.2"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

graal {
    version = graalVmVersion
}

testlogger {
    setTheme("mocha-parallel")
    isShowFullStackTraces = false
    slowThreshold = 1_000
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.graalvm.sdk:graal-sdk:$graalVmVersion")
    implementation("org.graalvm.js:js:$graalVmVersion")
    implementation("org.graalvm.js:js-scriptengine:$graalVmVersion")
    implementation("org.graalvm.compiler:compiler:$graalVmVersion")
    implementation("org.graalvm.truffle:truffle-api:$graalVmVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testImplementation("io.strikt:strikt-core:0.29.0") {
        exclude("com.christophsturm")
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
