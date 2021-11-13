package eu.alkismavridis.archutils.analysis.model

class DependencyRules(
  val path: String = "",
  val name: String = "",
  val includedSuffixes: Set<String> = setOf("*"),
  val allowedDependencies: Map<String, List<String>> = emptyMap()
) {
  companion object {
    fun allowAll() = DependencyRules(name = "<ALLOW_ALL>", allowedDependencies = mapOf("*" to listOf("*")))
  }
}
