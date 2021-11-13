package eu.alkismavridis.archutils.analysis

import eu.alkismavridis.archutils.analysis.config.PathConfiguration
import eu.alkismavridis.archutils.validation.CyclicDependency
import eu.alkismavridis.archutils.modules.ModuleStats
import eu.alkismavridis.archutils.validation.IllegalModuleDependency

class AnalysisResult(
  val moduleStats: List<ModuleStats>,
  val illegalDependencies: List<IllegalModuleDependency>,
  val cyclicDependencies: Set<CyclicDependency>,
  val config: PathConfiguration
)
