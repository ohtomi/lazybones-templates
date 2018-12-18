import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
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

open class VerifyTemplateConventionItem @javax.inject.Inject constructor(val objectFactory: ObjectFactory) {

    var name: String? = null

    var params: Array<String> = emptyArray()

    var steps: MutableList<VerifyTemplateConventionStep> = mutableListOf()

    fun step(configuration: Closure<VerifyTemplateConventionStep>) {
        VerifyTemplateConventionStep.newInstance(objectFactory).also {
            ConfigureUtil.configure(configuration, it)
            steps.add(it)
        }
    }

    fun step(action: Action<in VerifyTemplateConventionStep>) {
        VerifyTemplateConventionStep.newInstance(objectFactory).also {
            action.execute(it)
            steps.add(it)
        }
    }

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionItem = objectFactory.newInstance<VerifyTemplateConventionItem>(objectFactory)
    }
}

open class VerifyTemplateConventionStep {

    var name: String? = null

    var commands: Array<String> = emptyArray()

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionStep = objectFactory.newInstance<VerifyTemplateConventionStep>()
    }
}
