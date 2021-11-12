package eu.alkismavridis.archutils.analysis.testutils

import eu.alkismavridis.archutils.analysis.model.ModuleStats

class DummyModuleStats(
  override val name: String,
  override val files: Int = 0,
  override val internallyUsedFiles: Int = 0,
  override val externallyUsedFiles: Int = 0,
  override val internalDependencies: Int = 0,
  override val dependenciesComingIn: Int = 0,
  override val dependenciesGoingOut: Int = 0,
  override val usesModules: Set<String> = emptySet(),
  override val usedByModules: Set<String> = emptySet()
) : ModuleStats
