import com.google.gson.Gson
import io.github.kevinah95.kdriller.Repository
import io.github.kevinah95.kdriller.domain.Developer
import models.RawChange
import java.io.File

fun main() {

    val parsedReposJava = listOf(
        "https://github.com/centic9/jgit-cookbook.git",
        "https://github.com/skylot/jadx.git"
    )

    val parsedReposCsharp = listOf(
        "https://github.com/elsa-workflows/elsa-core.git"
    )
    val pathToRepo = listOf(
        "https://github.com/centic9/jgit-cookbook.git", //TODO: add repository
    )
    val contributors = mutableSetOf<Developer>()
    val projectName = Repository(pathToRepo).traverseCommits().first().projectName
    val regex = Regex("package\\s+([\\w\\.]+)") // TODO: change to namespace
    val gson = Gson()
    val outputFile = File("changes_${projectName.toSnakeCase()}.json")
    outputFile.writeText("[")

    for(commit in Repository(pathToRepo).traverseCommits()){
        contributors.add(commit.committer)
        try {
            for (modifiedFile in commit.modifiedFiles) {
                val rawChange = commit.committer.name?.let { RawChange(it) }
                rawChange?.devEmail = commit.committer.email.toString()
                rawChange?.date = commit.committerDate
                rawChange?.type = modifiedFile.changeType.name
                if (modifiedFile.filename.endsWith(".java")){ // TODO: change to language extension
                    val matchPackageResult = modifiedFile.sourceCode?.let { regex.find(it) }
                    for (method in modifiedFile.methods){
                        if (matchPackageResult != null){
                            rawChange?._package = method.name.split("::")[0]
                            rawChange?._class = method.name.split("::")[1]
                            rawChange?.method = method.name.split("::")[2]
                            rawChange?.complexity = method.complexity
                            if (rawChange != null) {
                                //rawChanges.add(rawChange)
                                outputFile.appendText(gson.toJson(rawChange))
                                outputFile.appendText(",")
                            }
                        }
                    }

                }
            }
        } catch (e: Exception) {
            println(e)
        }

    }
    outputFile.appendText("]")
    println(projectName)
    println(contributors.size)
    contributors.sortedBy { c -> c.name }.forEach { println(it) }
}

fun String.toSnakeCase() = replace(humps, "_").lowercase()
private val humps = "(?<=.)(?=\\p{Upper})".toRegex()