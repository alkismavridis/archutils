package eu.alkismavridis.archutils.project

class ProjectAnalysisService {
  fun analyse(modulesStats: List<ModuleStats>) : AnalysisResult {
    return AnalysisResult(modulesStats) // TODO alkis detect circular dependencies and so on
  }
}
