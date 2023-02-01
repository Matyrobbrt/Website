// fileName: props.modsDotGroovy == true ? defaultName : null
ModsDotGroovy.make {
    modLoader = 'javafml'
    loaderVersion = '[${versions.forge.split('\\.')[0]},)'

    license = '${license}'
    // A URL to refer people to when problems occur with this mod
    issueTrackerUrl = 'https://change.me.to.your.issue.tracker.example.invalid/'

    mod {
        modId = '${modId}'
        displayName = '${modName ?: modId.capitalize()}'
        displayTest = '${displayTest}'

        version = this.version

        description = '''${modDescription ?: 'Lorem Ipsum'}'''
        authors = ['${author}']

        // logoFile = '${modId}.png'

        dependencies {
            forge = "[\${this.forgeVersion},)"
            minecraft = this.minecraftVersionRange
        }
    }
}