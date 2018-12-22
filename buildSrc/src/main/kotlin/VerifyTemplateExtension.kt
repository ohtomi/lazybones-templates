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

open class VerifyTemplateConventionStep @javax.inject.Inject constructor(var name: String) {

    var args: Array<String> = emptyArray()

    open fun commands(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project) =
            args

    open fun directory(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project) =
            project.rootDir

    open fun timeout(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project) =
            5L

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionStep =
                objectFactory.newInstance("step")
    }
}

open class VerifyTemplateConventionGenerateStep : VerifyTemplateConventionStep("generate") {

    var params: Array<String> = emptyArray()

    override fun commands(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project): Array<String> {
        val destDir = project.workDir(templateName, index).absolutePath
        return arrayOf("lazybones", "create", templateName, templateVersion, destDir, *params)
    }

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionGenerateStep =
                objectFactory.newInstance()
    }
}

open class VerifyTemplateConventionBuildStep : VerifyTemplateConventionStep("build") {

    var tasks: Array<String> = emptyArray()

    override fun commands(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project): Array<String> {
        return arrayOf("./gradlew", "--no-daemon", *tasks)
    }

    override fun directory(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project): File =
            project.workDir(templateName, index)

    override fun timeout(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project) =
            30L

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionBuildStep =
                objectFactory.newInstance()
    }
}

abstract class VerifyTemplateConventionCheckStep(val description: String) : VerifyTemplateConventionStep("check:$description") {

    override fun directory(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project): File =
            project.workDir(templateName, index)
}

open class VerifyTemplateConventionExistsFilesStep : VerifyTemplateConventionCheckStep("exists") {

    var files: Array<String> = emptyArray()

    override fun commands(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project): Array<String> {
        return arrayOf("/bin/sh", "-c", files.map { """ test -a "$it" """ }.joinToString(" && "))
    }

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionExistsFilesStep =
                objectFactory.newInstance()
    }
}

open class VerifyTemplateConventionNotExistsFilesStep : VerifyTemplateConventionCheckStep("not exists") {

    var files: Array<String> = emptyArray()

    override fun commands(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project): Array<String> {
        return arrayOf("/bin/sh", "-c", files.map { """ ! test -a "$it" """ }.joinToString(" && "))
    }

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionNotExistsFilesStep =
                objectFactory.newInstance()
    }
}

fun Project.workDir(templateName: String, index: Int) =
        file("$buildDir/lazybones-projects/$templateName/$index")
