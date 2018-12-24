import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import javax.inject.Inject

open class VerifyTemplateConventionStep @Inject constructor(var name: String) {

    var args: Array<String> = emptyArray()

    open fun execute(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project): StepResult {
        return runExternalProcess(args, project.rootDir, 5, TimeUnit.SECONDS)
    }

    fun runExternalProcess(commands: Array<String>, directory: File, timeout: Long, unit: TimeUnit): StepResult {
        try {
            return ProcessBuilder().run {
                command(*commands)
                directory(directory)
                start().run {
                    waitFor(timeout, unit)
                    if (isAlive) {
                        destroyForcibly()
                        waitFor(1, TimeUnit.SECONDS)
                    }
                    val rc = exitValue()
                    if (rc == 0) {
                        StepResult(true, "Done.")
                    } else {
                        StepResult(false, "Failed. Something wrong. rc: $rc")
                    }
                }
            }
        } catch (e: IOException) {
            return StepResult(false, "Exception: ${e.message}")
        } catch (e: InterruptedException) {
            return StepResult(false, "Exception: ${e.message}")
        }
    }

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionStep =
                objectFactory.newInstance("step")
    }

    data class StepResult(val isSuucess: Boolean, val message: String?)
}

open class VerifyTemplateConventionGenerateStep : VerifyTemplateConventionStep("generate") {

    var params: Array<String> = emptyArray()

    override fun execute(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project): StepResult {
        val destDir = project.workDir(templateName, index).absolutePath
        val commands = arrayOf("lazybones", "create", templateName, templateVersion, destDir, *params)
        val directory = project.rootDir
        return runExternalProcess(commands, directory, 5, TimeUnit.SECONDS)
    }

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionGenerateStep =
                objectFactory.newInstance()
    }
}

open class VerifyTemplateConventionBuildStep : VerifyTemplateConventionStep("build") {

    var tasks: Array<String> = emptyArray()

    override fun execute(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project): StepResult {
        val commands = arrayOf("./gradlew", "--no-daemon", *tasks)
        val directory = project.workDir(templateName, index)
        return runExternalProcess(commands, directory, 30, TimeUnit.SECONDS)
    }

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionBuildStep =
                objectFactory.newInstance()
    }
}

open class VerifyTemplateConventionExistsFilesStep : VerifyTemplateConventionStep("check:exists") {

    var files: Array<String> = emptyArray()

    override fun execute(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project): StepResult {
        val errors = files.map { Paths.get(project.workDir(templateName, index).absolutePath, it) }.filter { !Files.exists(it) }
        if (errors.isEmpty()) {
            return StepResult(true, "All files exists.")
        } else {
            return StepResult(false, "Not found. files: ${errors.joinToString { "${it.fileName}" }}")
        }
    }

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionExistsFilesStep =
                objectFactory.newInstance()
    }
}

open class VerifyTemplateConventionNotExistsFilesStep : VerifyTemplateConventionStep("check:not exists") {

    var files: Array<String> = emptyArray()

    override fun execute(templateName: String, templateVersion: String, index: Int, item: VerifyTemplateConventionItem, project: Project): StepResult {
        val errors = files.map { Paths.get(project.workDir(templateName, index).absolutePath, it) }.filter { Files.exists(it) }
        if (errors.isEmpty()) {
            return StepResult(true, "All files not exists.")
        } else {
            return StepResult(false, "Found. files: ${errors.joinToString { "${it.fileName}" }}")
        }
    }

    companion object {
        fun newInstance(objectFactory: ObjectFactory): VerifyTemplateConventionNotExistsFilesStep =
                objectFactory.newInstance()
    }
}

fun Project.workDir(templateName: String, index: Int) =
        file("$buildDir/lazybones-projects/$templateName/$index")
