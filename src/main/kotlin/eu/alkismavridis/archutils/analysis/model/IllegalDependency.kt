package eu.alkismavridis.archutils.analysis.model

data class IllegalModuleDependency(
  val moduleFrom: String,
  val moduleTo: String,
)
