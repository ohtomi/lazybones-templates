import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import org.gradle.util.ConfigureUtil
import java.io.File

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

open class VerifyTemplateConventionItem @javax.inject.Inject constructor(var name: String, val objectFactory: ObjectFactory) {

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

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionItem =
                objectFactory.newInstance("", objectFactory)
    }
}

open class VerifyTemplateConventionStep(var name: String) {

    var args: Array<String> = emptyArray()

    open fun commands(
            templateName: String,
            templateVersion: String,
            index: Int,
            item: VerifyTemplateConventionItem,
            project: Project
    ) =
            args

    open fun directory(
            templateName: String,
            templateVersion: String,
            index: Int,
            item: VerifyTemplateConventionItem,
            project: Project
    ) =
            project.rootDir

    open fun timeout(
            templateName: String,
            templateVersion: String,
            index: Int,
            item: VerifyTemplateConventionItem,
            project: Project
    ) =
            5L

    fun rootDir(
            templateName: String,
            @Suppress("UNUSED_PARAMETER") templateVersion: String,
            index: Int,
            @Suppress("UNUSED_PARAMETER") item: VerifyTemplateConventionItem,
            project: Project
    ) =
            project.file("${project.buildDir}/lazybones-projects/$templateName/$index")

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionStep =
                objectFactory.newInstance("step")
    }
}

open class VerifyTemplateConventionGenerateStep : VerifyTemplateConventionStep("generate") {

    var params: Array<String> = emptyArray()

    override fun commands(
            templateName: String,
            templateVersion: String,
            index: Int,
            item: VerifyTemplateConventionItem,
            project: Project
    ): Array<String> {
        val destDir = rootDir(templateName, templateVersion, index, item, project).absolutePath
        return arrayOf("lazybones", "create", templateName, templateVersion, destDir, *params)
    }

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionGenerateStep =
                objectFactory.newInstance()
    }
}

open class VerifyTemplateConventionBuildStep : VerifyTemplateConventionStep("build") {

    var tasks: Array<String> = emptyArray()

    override fun commands(
            templateName: String,
            templateVersion: String,
            index: Int,
            item: VerifyTemplateConventionItem,
            project: Project
    ): Array<String> {
        return arrayOf("./gradlew", "--no-daemon", *tasks)
    }

    override fun directory(
            templateName: String,
            templateVersion: String,
            index: Int,
            item: VerifyTemplateConventionItem,
            project: Project
    ): File =
            rootDir(templateName, templateVersion, index, item, project)

    override fun timeout(
            templateName: String,
            templateVersion: String,
            index: Int,
            item: VerifyTemplateConventionItem,
            project: Project
    ) = 30L

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionBuildStep =
                objectFactory.newInstance()
    }
}
