import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.util.ConfigureUtil

open class VerifyTemplateExtension(val objectFactory: ObjectFactory) {

    val templates: MutableList<VerifyTemplateConventionItem> = mutableListOf()

    fun template(configuration: Closure<VerifyTemplateConventionItem>) {
        VerifyTemplateConventionItem.newInstance(objectFactory).also {
            ConfigureUtil.configure(configuration, it)
            templates.add(it)
        }
    }

    fun template(action: Action<in VerifyTemplateConventionItem>) {
        VerifyTemplateConventionItem.newInstance(objectFactory).also {
            action.execute(it)
            templates.add(it)
        }
    }
}

