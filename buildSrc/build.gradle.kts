plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    maven {
        url = uri("http://dl.bintray.com/pledbrook/plugins")
    }
}

dependencies {
    implementation("uk.co.cacoethes:lazybones-gradle:1.2.4")
}
