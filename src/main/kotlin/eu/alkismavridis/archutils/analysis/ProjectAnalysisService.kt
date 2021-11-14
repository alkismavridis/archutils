package eu.alkismavridis.archutils.analysis

import eu.alkismavridis.archutils.analysis.config.ArchutilsConfiguration
import eu.alkismavridis.archutils.analysis.config.PathConfiguration
import eu.alkismavridis.archutils.modules.ModuleStats
import eu.alkismavridis.archutils.modules.ModuleStatsBuilder
import eu.alkismavridis.archutils.validation.CyclicDependencyService
import eu.alkismavridis.archutils.validation.DependencyValidationService
import java.util.function.Consumer

class ProjectAnalysisService(
  private val config: ArchutilsConfiguration,
  private val validationService: DependencyValidationService,
  private val cyclicDependencyService: CyclicDependencyService,
) {
  fun analyseProject(
    projectRelativePath: String,
    projectAbsolutePath: String,
    dependencyProvider: Consumer<ModuleStatsBuilder>,
    presenter: Consumer<String>
  ): AnalysisResult {
    val pathConfig = this.findConfigForPath(projectRelativePath)

    presenter.accept("Collecting dependencies...")
    val moduleData = this.getModuleData(projectAbsolutePath, pathConfig, dependencyProvider)

    presenter.accept("Analysing dependencies...")
    val illegalDependencies = this.validationService.findIllegalDependencies(moduleData, pathConfig.allowedDependencies)

    presenter.accept("Searching for cyclic dependencies...")
    val cyclicDependencies = this.cyclicDependencyService.detectCycles(moduleData)

    return AnalysisResult(moduleData, illegalDependencies, cyclicDependencies, projectRelativePath, pathConfig)
  }

  private fun findConfigForPath(projectPath: String): PathConfiguration {
    return this.config.rules.find { it.path == projectPath }
      ?: PathConfiguration.allowAll()
  }

  private fun getModuleData(projectPath: String, pathConfig: PathConfiguration, dependencyProvider: Consumer<ModuleStatsBuilder>) : List<ModuleStats> {
    val builder = ModuleStatsBuilder(projectPath, pathConfig.includedSuffixes)
    dependencyProvider.accept(builder)
    return builder.build()
  }
}
