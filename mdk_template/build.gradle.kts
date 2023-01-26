// fileName: props.gradleKotlinDSL == true ? defaultName : null
plugins {
    eclipse
    `maven-publish`
    id("net.minecraftforge.gradle") version "5.1.+"<%
    if (props.mixinGradle) {
        out.print("\n    id(\"org.spongepowered.mixin\") version \"0.7.+\"")
    }
    if (props.modsDotGroovy) {
        out.print("\n    id(\"io.github.groovymc.modsdotgroovy\") version \"1.1.2\"")
    }
    if (mappings.channel == 'parchment') {
        out.print("\n    id(\"org.parchmentmc.librarian.forgegradle\") version \"1.+\"")
    } %>
}
<% if (props.usesMixins)
out.println("""\nmixin {
    add(sourceSets.main.get(), \"${modId}.refmap.json\")
    config(\"${modId}.mixins.json\")
}""") %>
group = "${packageName}"
setProperty("archivesBaseName", "${modId}")

<% if (props.apiSourceSet) {
    out.println('val api by sourceSets.registering')
} %><% if (props.datagenSourceSet) {
    out.println('val datagen by sourceSets.registering {')
    out.println('    compileClasspath += api.get().output')
    out.println('    compileClasspath += sourceSets.main.get().output')
    out.println('}')
} %><% if (props.apiSourceSet || props.datagenSourceSet) {
    final configurations = []
    if (props.apiSourceSet) configurations.add('apiImplementation')
    if (props.datagenSourceSet) configurations.add('datagenImplementation')
    configurations.each {
        out.println("val ${it}: Configuration by configurations.getting {")
        out.println('    extendsFrom(configurations.minecraft.get())')
        out.println('}')
    }

    out.println('\nsourceSets {')
    if (props.apiSourceSet) {
        out.println('    main {')
        out.println('        compileClasspath += api.get().output')
        out.println('    }')
    }
    out.println('}')
} %>
<% if (props.modsDotGroovy) {
    out.println('modsDotGroovy {')
    out.println('    dslVersion.set("1.2.2")')
    out.println('    platform("forge")')
    out.println('}')
} %>
java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

println("Java: \${System.getProperty("java.version")}, JVM: \${System.getProperty("java.vm.version")} (\${System.getProperty("java.vendor")}), Arch: \${System.getProperty("os.arch")}")
minecraft {
    mappings("${mappings.channel}", "${mappings.version}")

    <% if (props.usesAccessTransformers == false) out.print '// ' %>accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        all {<% if (props.sharedRunDirs == true) out.println('\n            workingDirectory(project.file(\"run\"))') %>
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", "${modId}")
            <% if (props.usesMixins) out.println('property("mixin.debug.export", "true")') %>
            mods {
                create("${modId}") {
                    source(sourceSets.main.get())
<% if (props.apiSourceSet) {
    out.println("                    source(api.get())")
} %>
                }
            }
        }
        <% if (props.sharedRunDirs == true) {
            out.print('\n        create("client") {}\n        create("server") {}')
        } else {
            out.println('''
        client {
            workingDirectory(project.file("run/client"))
        }
        server {
            workingDirectory(project.file("run/server"))
        }''')} %>
        <% if ((versions.minecraft.split('\\.')[1] as int) >= 17) {
            if (props.sharedRunDirs == false) {
                out.println('''
        create("gameTestServer") {
            workingDirectory(project.file("run/server"))
        }''')
            } else {
                out.println('create("gameTestServer") {}')
            }
        }%>
        create("data") {
            workingDirectory(project.file("run"))
            args("--mod", "${modId}", "--all",
                    "--output", file("src/generated/resources/"),
                    "--existing", file("src/main/resources/")) <% if (props.datagenSourceSet) out.println("""
            mods {
                named("${modId}") {
                    source(datagen.get())
                }
            }""")%>
        }
    }
}

// Include resources generated by data generators.
sourceSets.main { resources { srcDir("src/generated/resources") } }

dependencies {
    <% if (props.defineVersionsInPropertiesFile == true) out.print("minecraft(\"net.minecraftforge:forge:\${minecraft_version}-\${forge_version}\")") else out.print("minecraft(\"net.minecraftforge:forge:${versions.minecraft}-${versions.forge}\")") %><% if (props.usesMixins) out.print("\nannotationProcessor(\"org.spongepowered:mixin:0.8.5:processor\"") %>
}

val manifestAttributes = mutableMapOf(
        "Specification-Title"    to "${modId}",
        "Specification-Vendor"   to "${author ?: modId + 'sareus'}",
        "Specification-Version"  to "1",
        "Implementation-Title"   to project.name,
        "Implementation-Version" to project.version,
        "Implementation-Vendor"  to "${author ?: modId + 'sareus'}"<% if (props.usesMixins) out.print(",\n        \"MixinConfigs\"           to \"${modId}.mixins.json\"") %>
)

tasks {
    jar {
        manifest.attributes(manifestAttributes)
        finalizedBy("reobfJar")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
