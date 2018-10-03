allprojects {
    repositories {
        jcenter()
        google()
    }
}

task(name = "clean", type = Delete::class) {
    delete(rootProject.buildDir)
}
