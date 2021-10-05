package eu.alkismavridis.codescape.config

class CodeScapeConfiguration(
  val rules: List<CodeScapeConfigurationRule>
)

class CodeScapeConfigurationRule(
  val regex: String,
  val visibility: NodeVisibility,
)
