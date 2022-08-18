/**
 * plugins
 */
plugins {
    id("nebula.maven-publish") version "17.3.2"
}

/**
 * buildscript
 */
buildscript {
    dependencies {
    }
}

/**
 * dependencies
 */
dependencies {
    api("com.github.funczz:rop:1.0.0")
    //logger
    testImplementation("ch.qos.logback:logback-classic:1.1.7")
    testImplementation("org.fusesource.jansi:jansi:1.18")
    testImplementation("org.slf4j:slf4j-api:1.7.21")
    testImplementation("org.slf4j:jcl-over-slf4j:1.7.21")
}

/**
 * plugin: nebula.maven-publish
 */
publishing {
    publications {
        group = "com.github.funczz"
    }

    repositories {
        maven {
            url = uri(
                    PublishMavenRepository.url(
                            version = version.toString(),
                            baseUrl = "$buildDir/mvn-repos"
                    )
            )
        }
    }
}
