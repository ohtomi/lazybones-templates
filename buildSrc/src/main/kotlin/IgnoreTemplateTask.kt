import org.gradle.api.tasks.Delete
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject

open class IgnoreTemplateTask @Inject constructor(templateDir: File) : Delete() {

    init {
        delete = templateDir.walk().fold(mutableSetOf<Any>()) { acc, file ->
            if (file.name == "gitignore") {
                val lines = Files.readAllLines(file.toPath())
                        .filter { !it.trim().isEmpty() }
                        .filter { !it.trimStart().startsWith("#") }
                        .map { Paths.get(file.parent, it).toString() }
                acc.addAll(lines)
            }
            acc
        }
    }
}
