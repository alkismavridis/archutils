package eu.alkismavridis.archutils.project

class AnalysisParameters(
  val name: String = "<DEFAULT CONFIGURATION>",
  val rules: List<DependencyRuleSet> = emptyList()
)

class DependencyRuleSet(
  val path: String = "",
  val name: String = "",
  val allowedDependencies: Map<String, List<String>> = emptyMap()
)
