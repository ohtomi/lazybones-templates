import org.gradle.api.tasks.Delete
import java.io.File
import javax.inject.Inject

open class IgnoreTemplateTask @Inject constructor(val templateDir: File) : Delete() {

    init {
        delete = setOf(
                // .gitignore
                "$templateDir/.gradle",
                "$templateDir/.lazybones",
                "$templateDir/.idea",
                "$templateDir/build",
                "$templateDir/*.iml",
                // buildSrc/.gitignore
                "$templateDir/buildSrc/.gradle",
                "$templateDir/buildSrc/build"
        )
    }
}
