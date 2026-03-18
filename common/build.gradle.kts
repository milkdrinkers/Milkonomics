import org.jooq.meta.jaxb.Logging

plugins {
    alias(libs.plugins.jooq) // Database ORM
    flyway
}

dependencies {
    // Core dependencies
    api(projects.api)
    implementation(libs.morepaperlib)

    // API
    implementation(libs.javasemver) // Required by VersionWatch
    implementation(libs.versionwatch)
    implementation(libs.wordweaver)
    implementation(libs.crate.api)
    implementation(libs.crate.yaml)
    api(libs.yaml)
    annotationProcessor(libs.configurate.`interface`.ap)
    api(libs.configurate.`interface`)
    implementation(libs.configurate.yaml)

    implementation(libs.colorparser) {
        exclude("net.kyori")
    }
    implementation(libs.threadutil.bukkit)
    implementation(libs.commandapi.shade)
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
    compileOnly(libs.bundles.flyway)
    flywayDriver(libs.h2)
    compileOnly(libs.jakarta) // Compiler bug, see: https://github.com/jOOQ/jOOQ/issues/14865#issuecomment-2077182512
    compileOnly(libs.jooq)
    jooqCodegen(libs.h2)

    // Database dependencies - JDBC drivers
    compileOnly(libs.bundles.jdbcdrivers)

    // Messaging service clients
    compileOnly(libs.bundles.messagingclients)

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

        // Shadow classes
        fun reloc(originPkg: String, targetPkg: String, exclude: String = "") = relocate(originPkg, "${project.relocationPackage}.${targetPkg}") { exclude(exclude) }

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
        reloc("org.bstats", "bstats")

        // Configurate deps
        reloc("io.leangen.geantyref", "geantyref")
        reloc("org.yaml", "yaml")
        reloc("org.spongepowered", "spongepowered")
//        reloc("it.unimi.dsi.fastutil", "it.unimi.dsi.fastutil")
//        reloc("net.kyori", "kyori", "net.kyori.adventure.text.logger.slf4j.ComponentLogger")

        mergeServiceFiles()
    }
}

flyway {
    url = provider {
        "jdbc:h2:${project.layout.buildDirectory.get()}/generated/flyway/db;AUTO_SERVER=TRUE;MODE=MySQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE;IGNORECASE=TRUE"
    }
    user = "sa"
    password = ""
    schemas = listOf("PUBLIC")
    placeholders = mapOf( // Substitute placeholders for flyway
        "tablePrefix" to "",
    )
    validateMigrationNaming = true
    baselineOnMigrate = true
    cleanDisabled = false
    enableRdbmsSpecificMigrations = true
    locations = listOf(
        "filesystem:${project.layout.projectDirectory}/src/main/resources/db/migration/",
        "classpath:${mainPackage.replace(".", "/")}/database/migration/migrations"
    )
}

jooq {
    configuration {
        logging = Logging.ERROR
        jdbc {
            driver = "org.h2.Driver"
            url = flyway.url.get()
            user = flyway.user.get()
            password = flyway.password.get()
        }
        generator {
            database {
                name = "org.jooq.meta.h2.H2Database"
                includes = ".*"
                excludes = "(flyway_schema_history)|(?i:information_schema\\..*)|(?i:system_lobs\\..*)"  // Exclude database specific files
                inputSchema = "PUBLIC"
                schemaVersionProvider = "SELECT :schema_name || '_' || MAX(\"version\") FROM \"flyway_schema_history\"" // Grab version from Flyway
            }
            target {
                packageName = "${mainPackage}.database.schema"
                withClean(true)
            }
        }
    }
}