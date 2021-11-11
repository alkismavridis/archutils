package eu.alkismavridis.archutils.analysis.model

class DependencyRules(
  val path: String = "",
  val name: String = "",
  val allowedDependencies: Map<String, List<String>> = emptyMap()
) {
  companion object {
    fun allowAll() = DependencyRules("", "<ALLOW_ALL>", mapOf("*" to listOf("*")))
  }
}
