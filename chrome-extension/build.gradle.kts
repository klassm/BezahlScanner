task(name = "createBuildDir") {
    file("build").mkdir()
}

task(name = "createZip", type = Zip::class) {
    baseName = "build/extension"
    from(".")
    exclude("build", "*.iml")
}

task(name = "clean") {
    file("build").delete();
}

task(name = "release") {
    dependsOn("createZip")
}