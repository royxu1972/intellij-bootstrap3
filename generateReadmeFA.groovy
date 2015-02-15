@Grapes(
        @Grab(group='com.google.guava', module='guava', version='18.0')
)
import com.google.common.base.CaseFormat

def file = new File("resources/liveTemplates/FontAwesome.xml")
def root = new XmlParser().parse(file)

def templates = root.template

def components = templates.collect {
    new Component(text:it.'@description', code: it.'@name', context:findMainContext(it))
}

def sorted = components.sort { it.code }
def groups = splitInGroups(sorted)

printComponents(groups)

void printComponents(groups) {
    groups.each { group ->
        println ""
        println "### ${group.name}"
        println ""
        println "| Component                      | Snippet code                   | Context |"
        println "|------------------------------- | -------------------------------| ------- |"

        group.components.each {
            println "| ${String.format("%-30s", it.text.capitalize())} | ${String.format("%-30s", it.code)} | ${String.format("%-7s", it.context)} |"
        }
    }
}

def findMainContext(template) {
    template.context.option.'@name'.contains('HTML') ? "HTML" : "CSS"
}

private List<Group> splitInGroups(components) {
    def groups = []
    Group lastGroup = null

    components.each { component ->
        def groupName = "Font Awesome"
        def group = new Group(name:groupName)

        if(lastGroup == null || group.name  != lastGroup.name) {
            lastGroup = new Group(name:groupName)
            groups << lastGroup
        }

        lastGroup.components.add(component)
    }

    return groups
}

class Group {
    String name
    List components = []
}

class Component {
    String text, code, context

    String getText() {
        text ?: 'No description'
    }
}