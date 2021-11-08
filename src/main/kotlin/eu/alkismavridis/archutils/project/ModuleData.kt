package eu.alkismavridis.archutils.project

interface ModuleData {
  val name: String
  val files: Int
  val internallyUsedFiles: Int
  val externallyUsedFiles: Int
  val internalDependencies: Int
  val incomingDependencies: Int
  val outgoingDependencies: Int
}
