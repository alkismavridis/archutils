package eu.alkismavridis.archutils.validation

data class IllegalModuleDependency(
  val moduleFrom: String,
  val moduleTo: String,
)
