package eu.alkismavridis.codescape.config

class CodeScapeConfiguration(
  val rules: List<CodeScapeConfigurationRule>
)

class CodeScapeConfigurationRule(
  private val regex: String,
  val visibility: NodeVisibility,
) {
  val compiledRegex by lazy { Regex(regex) }
}
