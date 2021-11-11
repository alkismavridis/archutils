package eu.alkismavridis.archutils.analysis.model

class AnalysisConfiguration(
  val name: String = "<DEFAULT CONFIGURATION>",
  val rules: List<DependencyRules> = emptyList()
)

