import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

open class VerifyTemplateTask : DefaultTask() {

    @Input
    var templateName: String = ""

    @Input
    var templateVersion: String = ""

    @Input
    var destBaseDir: String = "."

    @Input
    var testCases: List<VerifyTemplateConventionItem> = emptyList()

    @TaskAction
    fun verify() {
        if (testCases.isEmpty()) {
            throw TaskExecutionException(this, IllegalStateException("No test cases for $templateName $templateVersion"))
        }

        var success = 0
        testCases.forEachIndexed { index: Int, item: VerifyTemplateConventionItem ->
            println("\n${index + 1} of ${testCases.size} $WALKING_EMOJI ...")

            // TODO clean destBaseDir before running lazybones
            val destDir = "$destBaseDir/$index"

            try {
                val createCommands = arrayOf("lazybones", "create", templateName, templateVersion, destDir, *item.params)
                val createReturnCode = runExternalProcess(createCommands, project.rootDir, 3, TimeUnit.SECONDS)
                if (createReturnCode != 0) {
                    println("\n$SCREAM_EMOJI  [create] Failed to generate a new project.")
                    return@forEachIndexed
                }
                println("$THUMBS_UP_EMOJI  [create] A new project was generated in $destDir")

                val buildStep = item.steps.find { it.name == "build" } ?: return
                val buildCommands = buildStep.commands
                val buildReturnCode = runExternalProcess(buildCommands, project.file(destDir), 30, TimeUnit.SECONDS)
                if (buildReturnCode != 0) {
                    println("$SCREAM_EMOJI  [${buildStep.name}] Failed to build the project.")
                    return@forEachIndexed
                }
                println("$THUMBS_UP_EMOJI  [${buildStep.name}] The project was completely built.")

                success++
            } catch (e: IOException) {
                println("\n$SCREAM_EMOJI  Exception: ${e.message}")
                return@forEachIndexed
            } catch (e: InterruptedException) {
                println("\n$SCREAM_EMOJI  Exception: ${e.message}")
                return@forEachIndexed
            }
        }

        if (success < testCases.size) {
            throw TaskExecutionException(this, IllegalStateException("Template $templateName:$templateVersion has some errors."))
        }
    }

    @Throws(IOException::class, InterruptedException::class)
    fun runExternalProcess(commands: Array<String>, directory: File, timeout: Long, unit: TimeUnit): Int = ProcessBuilder().run {
        command(*commands)
        directory(directory)
        start().run {
            waitFor(timeout, unit)
            if (isAlive) {
                destroyForcibly()
                waitFor(1, TimeUnit.SECONDS)
            }
            exitValue()
        }
    }
}

val WALKING_EMOJI = String(Character.toChars(0x1F6B6))

val SCREAM_EMOJI = String(Character.toChars(0x1F631))

val THUMBS_UP_EMOJI = String(Character.toChars(0x1F44D))
