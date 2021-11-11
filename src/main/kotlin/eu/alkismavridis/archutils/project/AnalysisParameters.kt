package eu.alkismavridis.archutils.project

class AnalysisParameters(
  val name: String = "<DEFAULT CONFIGURATION>",
  val rules: List<DependencyRules> = emptyList()
)

class DependencyRules(
  val path: String = "",
  val name: String = "",
  val allowedDependencies: Map<String, List<String>> = emptyMap()
) {
  companion object {
    fun allowAll() = DependencyRules("", "<ALLOW_ALL>", mapOf("*" to listOf("*")))
  }
}

class AnalysisRequest(
  val projectRelativePath: String,
  val rules: DependencyRules
)
