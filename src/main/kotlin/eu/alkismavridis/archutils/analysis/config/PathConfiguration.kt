package eu.alkismavridis.archutils.analysis.config

class PathConfiguration(
  val path: String = "",
  val name: String = "",
  val includedSuffixes: Set<String> = setOf("*"),
  val allowedDependencies: Map<String, List<String>> = emptyMap()
) {
  companion object {
    private val ALLOW_EVERYTHING_MAP = mapOf("*" to listOf("*"))
    fun allowAll() = PathConfiguration(name = "Allow everything", allowedDependencies = ALLOW_EVERYTHING_MAP)
  }
}
