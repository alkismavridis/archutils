package eu.alkismavridis.archutils.analysis.model

class AnalysisResult(
  val moduleStats: List<ModuleStats>,
  val illegalDependencies: List<IllegalModuleDependency>
)
