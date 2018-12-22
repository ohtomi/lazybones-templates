import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import java.io.File

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
