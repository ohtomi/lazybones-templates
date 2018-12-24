import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import org.gradle.util.ConfigureUtil
import javax.inject.Inject

open class VerifyTemplateConventionItem @Inject constructor(var name: String, val objectFactory: ObjectFactory) {

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

    fun gradleGenerate(params: Array<String>) {
        VerifyTemplateConventionGenerateStep.newInstance(objectFactory).also {
            it.params = params
            steps.add(it)
        }
    }

    fun gradleBuild(tasks: Array<String>) {
        VerifyTemplateConventionBuildStep.newInstance(objectFactory).also {
            it.tasks = tasks
            steps.add(it)
        }
    }

    fun existsFiles(files: Array<String>) {
        VerifyTemplateConventionExistsFilesStep.newInstance(objectFactory).also {
            it.files = files
            steps.add(it)
        }
    }

    fun notExistsFiles(files: Array<String>) {
        VerifyTemplateConventionNotExistsFilesStep.newInstance(objectFactory).also {
            it.files = files
            steps.add(it)
        }
    }

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionItem =
                objectFactory.newInstance("", objectFactory)
    }
}
