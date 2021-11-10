package eu.alkismavridis.archutils.project

class ProjectAnalysisService(private val configuration: AnalysisParameters) {
  fun analyse(modulesStats: List<ModuleStats>, projectRelativePath: String) : AnalysisResult {
    val rules = this.getDependencyRulesFor(projectRelativePath)
    return AnalysisResult(projectRelativePath, rules, modulesStats) // TODO alkis detect circular dependencies and so on
  }

  private fun getDependencyRulesFor(projectRelativePath: String): DependencyRuleSet {
    return this.configuration.rules
      .find { it.path == projectRelativePath }
      ?: EMPTY_RULES
  }

  companion object {
    private val EMPTY_RULES = DependencyRuleSet(
      "",
      "<EMPTY_RULE_SET>",
      mapOf("*" to listOf("*"))
    )
  }
}
