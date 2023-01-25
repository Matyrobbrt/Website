// fileName: props.gradleKotlinDSL == true ? defaultName : null
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.minecraftforge.net/")<%
        if (mappings.channel == 'parchment') {
            out.print("\n        maven(\"https://maven.parchmentmc.org\")")
        }
        if (props.modsDotGroovy) {
            out.print("\n        maven(\"https://maven.moddinginquisition.org/releases\")")
        }
        if (props.mixinGradle) {
            out.print("""
        resolutionStrategy {
            eachPlugin {
                if (requested.id.id == "org.spongepowered.mixin") {
                    useModule("org.spongepowered:mixingradle:\${requested.version}")
                }
            }
        }""")
        }
        %>
    }
}
rootProject.name = "${modName ?: modId}"