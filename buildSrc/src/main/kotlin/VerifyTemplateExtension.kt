import org.gradle.api.Action
import org.gradle.api.Project

open class VerifyTemplateExtension(val project: Project) {

    val collection: MutableList<VerifyTemplateConventionItem> = mutableListOf()

    fun template(action: Action<in VerifyTemplateConventionItem>) {
        collection.add(VerifyTemplateConventionItem().apply { action.execute(this) })
    }
}

class VerifyTemplateConventionItem {

    var name: String = ""

    var params: Array<String> = emptyArray()
}
