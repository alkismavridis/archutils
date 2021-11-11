package eu.alkismavridis.archutils.project

class ProjectAnalysisService {
  fun analyse(request: AnalysisRequest, modulesStats: List<ModuleStats>) : AnalysisResult {
    return AnalysisResult(modulesStats) // TODO alkis detect circular dependencies and so on
  }
}
