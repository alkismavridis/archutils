package eu.alkismavridis.archutils.analysis.config

class ArchutilsConfiguration(
  val name: String = "Default Archutils Configuration",
  val outputFile: String = "",
  val rules: List<PathConfiguration> = emptyList()
)

