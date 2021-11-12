package eu.alkismavridis.archutils.analysis.model

import eu.alkismavridis.archutils.cycles.CyclicDependency

class AnalysisResult(
  val moduleStats: List<ModuleStats>,
  val illegalDependencies: List<IllegalModuleDependency>,
  val cyclicDependencies: List<CyclicDependency>
)
