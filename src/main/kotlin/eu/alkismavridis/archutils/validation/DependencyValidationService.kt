package eu.alkismavridis.archutils.validation

import eu.alkismavridis.archutils.modules.ModuleStats

class DependencyValidationService {
  fun findIllegalDependencies(modulesStats: List<ModuleStats>, allowedDependencies: AllowedDependencies) : List<IllegalModuleDependency> {
    return modulesStats
      .asSequence()
      .flatMap { getIllegalDependenciesFrom(it, allowedDependencies) }
      .toList()
  }

  private fun getIllegalDependenciesFrom(module: ModuleStats, allowedDependencies: AllowedDependencies): List<IllegalModuleDependency> {
    val permittedDependencies = allowedDependencies[module.name]
      ?: allowedDependencies["*"]
      ?: emptyList()

    if (permittedDependencies.contains("*")) return emptyList()

    return module.usesModules
      .asSequence()
      .filter { it !in permittedDependencies }
      .map { IllegalModuleDependency(moduleFrom = module.name, moduleTo = it) }
      .toList()
  }
}

private typealias AllowedDependencies = Map<String, List<String>>
