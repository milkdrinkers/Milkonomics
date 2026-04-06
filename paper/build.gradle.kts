import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    alias(libs.plugins.run.paper) // Built in test server using runServer and runMojangMappedServer tasks
    alias(libs.plugins.plugin.yml.bukkit) // Automatic plugin.yml generation
    alias(libs.plugins.plugin.yml.paper) // Automatic plugin.yml generation    //alias(libs.plugins.paperweight) // Used to develop internal plugins using Mojang mappings, See https://github.com/PaperMC/paperweight
}

dependencies {
    // Core dependencies
    implementation(projects.common) {
//        isTransitive = false
    }
    implementation(libs.morepaperlib)

    // API
    implementation(libs.javasemver) // Required by VersionWatch
    implementation(libs.versionwatch)
    implementation(libs.wordweaver)
    implementation(libs.crate.api)
    implementation(libs.crate.yaml)
    implementation(libs.colorparser) {
        exclude("net.kyori")
    }
    implementation(libs.threadutil.bukkit)
    implementation(libs.commandapi.shade)
    //annotationProcessor(libs.commandapi.annotations) // Uncomment if you want to use command annotations
    implementation(libs.triumph.gui) {
        exclude("net.kyori")
    }

    // Plugin dependencies
    implementation(libs.bstats)
    compileOnly(libs.packetevents)
    compileOnly(libs.placeholderapi) {
        exclude("me.clip.placeholderapi.libs", "kyori")
    }

    // Database dependencies - Core
    implementation(libs.hikaricp)
    library(libs.bundles.flyway)
//    flywayDriver(libs.h2)
    compileOnly(libs.jakarta) // Compiler bug, see: https://github.com/jOOQ/jOOQ/issues/14865#issuecomment-2077182512
    library(libs.jooq)
//    jooqCodegen(libs.h2)

    // Database dependencies - JDBC drivers
    library(libs.bundles.jdbcdrivers)

    // Messaging service clients
    library(libs.bundles.messagingclients)

    // Testing - Core
    testImplementation(libs.annotations)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
    testRuntimeOnly(libs.slf4j)
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.bundles.testcontainers)
    testRuntimeOnly(libs.paper.api)

    // Testing - Database dependencies
    testImplementation(libs.hikaricp)
    testImplementation(libs.bundles.flyway)
    testImplementation(libs.jooq)

    // Testing - JDBC drivers
    testImplementation(libs.bundles.jdbcdrivers)

    // Testing - Messaging service clients
    testImplementation(libs.bundles.messagingclients)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveBaseName.set(rootProject.name + "-" + project.name)
        archiveClassifier.set("")

        dependsOn(":common:shadowJar")

        // Shadow classes
        fun reloc(originPkg: String, targetPkg: String) = relocate(originPkg, "${project.relocationPackage}.${targetPkg}")

        reloc("space.arim.morepaperlib", "morepaperlib")
        reloc("io.github.milkdrinkers.javasemver", "javasemver")
        reloc("io.github.milkdrinkers.versionwatch", "versionwatch")
        reloc("io.github.milkdrinkers.wordweaver", "wordweaver")
        reloc("io.github.milkdrinkers.crate", "crate")
        reloc("io.github.milkdrinkers.colorparser", "colorparser")
        reloc("io.github.milkdrinkers.threadutil", "threadutil")
        reloc("org.snakeyaml", "snakeyaml")
        reloc("org.json", "json")
        reloc("dev.jorel.commandapi", "commandapi")
        reloc("dev.triumphteam.gui", "triumphgui")
        reloc("com.zaxxer.hikari", "hikaricp")
        reloc("org.snakeyaml", "snakeyaml")

        reloc("io.leangen.geantyref", "geantyref")
        reloc("org.yaml", "yaml")
        reloc("org.spongepowered", "spongepowered")

        mergeServiceFiles()
    }

    runServer {
        // Configure the Minecraft version for our task.
        minecraftVersion(libs.versions.paper.run.get())

        // IntelliJ IDEA debugger setup: https://docs.papermc.io/paper/dev/debugging#using-a-remote-debugger
        jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-DPaper.IgnoreJavaVersion=true", "-Dcom.mojang.eula.agree=true", "-DIReallyKnowWhatIAmDoingISwear", "-Dpaper.playerconnection.keepalive=6000")
        systemProperty("terminal.jline", false)
        systemProperty("terminal.ansi", true)

        // Automatically install dependencies
        downloadPlugins {
            github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
            github("PlaceholderAPI", "PlaceholderAPI", "2.11.7", "PlaceholderAPI-2.11.7.jar")
            hangar("ViaVersion", "5.5.1")
            hangar("ViaBackwards", "5.5.1")
        }
    }
}

bukkit { // Options: https://docs.eldoria.de/pluginyml/bukkit/
    // Plugin main class (required)
    main = rootProject.entryPointClass

    // Plugin Information
    name = rootProject.name
    prefix = rootProject.name
    version = "${rootProject.version}"
    description = "${rootProject.description}"
    authors = rootProject.authors
    contributors = rootProject.contributors
    apiVersion = libs.versions.paper.api.get().substringBefore("-R").substringBefore("-pre")
    foliaSupported = true

    // Misc properties
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD // STARTUP or POSTWORLD
    depend = listOf()
    softDepend = listOf("Vault", "PlaceholderAPI")
    loadBefore = listOf()
    provides = listOf()
}

paper { // Options: https://docs.eldoria.de/pluginyml/paper/
    main = rootProject.entryPointClass
    loader = rootProject.entryPointClass + "PluginLoader"
    generateLibrariesJson = true
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    // Info
    name = rootProject.name
    prefix = rootProject.name
    version = "${rootProject.version}"
    description = "${rootProject.description}"
    authors = rootProject.authors
    contributors = rootProject.contributors
    apiVersion = libs.versions.paper.api.get().substringBefore("-R").substringBefore("-pre")
    foliaSupported = false

    // Dependencies
    hasOpenClassloader = true
    bootstrapDependencies {}
    serverDependencies {
        register("Vault") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
        register("PlaceholderAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
    }
    provides = listOf()
}