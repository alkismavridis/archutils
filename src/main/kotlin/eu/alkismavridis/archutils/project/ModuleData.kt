package eu.alkismavridis.archutils.project

interface ModuleData {
  val name: String
  val internalDependencyCount: Int
  val incomingDependencyCount: Int
  val outgoingDependencyCount: Int
}
