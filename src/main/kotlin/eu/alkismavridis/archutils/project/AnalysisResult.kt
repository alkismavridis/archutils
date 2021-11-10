package eu.alkismavridis.archutils.project

class AnalysisResult(
  val projectRelativePath: String,
  val rules: DependencyRuleSet,
  val moduleStats: List<ModuleStats>
)
