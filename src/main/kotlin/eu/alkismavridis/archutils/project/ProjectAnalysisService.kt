package eu.alkismavridis.archutils.project

class ProjectAnalysisService(private val configuration: AnalysisParameters) {
  fun analyse(modulesStats: List<ModuleStats>) : AnalysisResult {
    return AnalysisResult(this.configuration, modulesStats) // TODO alkis detect circular dependencies and so on
  }
}
