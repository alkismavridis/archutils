package eu.alkismavridis.archutils.analysis

import eu.alkismavridis.archutils.analysis.model.*

class DependencyAnalysisService {
  fun findIllegalDependencies(request: AnalysisRequest, modulesStats: List<ModuleStats>) : List<IllegalModuleDependency> {
    if (request.rules.allowedDependencies.isEmpty()) {
      return emptyList()
    }

    return modulesStats
      .asSequence()
      .flatMap { getIllegalDependenciesFrom(it, request.rules) }
      .toList()
  }

  private fun getIllegalDependenciesFrom(module: ModuleStats, rules: DependencyRules): List<IllegalModuleDependency> {
    val permittedDependencies = rules.allowedDependencies[module.name]
      ?: rules.allowedDependencies["*"]
      ?: emptyList()

    if (permittedDependencies.contains("*")) return emptyList()

    return module.usesModules
      .asSequence()
      .filter { it !in permittedDependencies }
      .map { IllegalModuleDependency(moduleFrom = module.name, moduleTo = it) }
      .toList()
  }
}
