package eu.alkismavridis.archutils.project

interface ModuleStats {
  val name: String
  val files: Int
  val internallyUsedFiles: Int
  val externallyUsedFiles: Int
  val internalDependencies: Int
  val dependenciesComingIn: Int
  val dependenciesGoingOut: Int
  val usesModules: Set<String>
  val usedByModules: Set<String>
}
