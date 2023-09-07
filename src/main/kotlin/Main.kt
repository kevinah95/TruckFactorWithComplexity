import io.github.kevinah95.kdriller.Repository
import io.github.kevinah95.kdriller.domain.Developer
import models.RawChange

fun main() {

    val pathToRepo = listOf(
        "https://github.com/centic9/jgit-cookbook.git"
    )
    val contributors = mutableSetOf<Developer>()
    val projectName = Repository(pathToRepo).traverseCommits().first().projectName
    val rawChanges = mutableListOf<RawChange>()
    val regex = Regex("package\\s+([\\w\\.]+);")

    for(commit in Repository(pathToRepo).traverseCommits()){
        contributors.add(commit.committer)
        try {
            for (modifiedFile in commit.modifiedFiles) {
                val rawChange = commit.committer.name?.let { RawChange(it) }
                rawChange?.devEmail = commit.committer.email.toString()
                rawChange?.date = commit.committerDate.toString()
                rawChange?.type = modifiedFile.changeType.name
                if (modifiedFile.filename.endsWith(".java")){
                    val matchPackageResult = modifiedFile.sourceCode?.let { regex.find(it) }
                    for (method in modifiedFile.methods){
                        if (matchPackageResult != null){
                            rawChange?._package = matchPackageResult.groupValues[1]
                            rawChange?._class = method.name.split("::")[0]
                            rawChange?.method = method.name.split("::")[1]
                            rawChange?.complexity = method.complexity
                            rawChange?.newPath = modifiedFile.newPath.toString()
                            if (rawChange != null) {
                                rawChanges.add(rawChange)
                            }
                        }
                    }

//                    for(methodBefore in modifiedFile.methodsBefore){
//                        val matchResultBefore = modifiedFile.sourceCodeBefore?.let { regex.find(it) }
//                        if (matchResultBefore != null){
//                            rawChange?._packageBefore = matchResultBefore.groupValues[1]
//                            rawChange?._classBefore = methodBefore.name.split("::")[0]
//                            rawChange?.method = methodBefore.name.split("::")[1]
//                            rawChange?.complexity = methodBefore.complexity
//                            rawChange?.oldPath = modifiedFile.oldPath.toString()
//                            if (rawChange != null) {
//                                rawChanges.add(rawChange)
//                            }
//                        }
//                    }
                }
            }
        } catch (e: Exception) {
            println(e)
        }

    }
    println(projectName)
    println(contributors.size)
    contributors.sortedBy { c -> c.name }.forEach { println(it) }
}