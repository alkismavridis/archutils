package eu.alkismavridis.codescape.config

class CodeScapeConfiguration(
  val root: String = "",
  val rules: List<CodeScapeConfigurationRule> = emptyList()
)

class CodeScapeConfigurationRule(
  private val regex: String,
  val visibility: NodeVisibility = NodeVisibility.VISIBLE,
  val image: String? = null,
  val color: String? = null,
  val openColor: String? = null,
  val borderColor: String? = null,
  val borderWidth: Float? = null,
  val hideIcon: Boolean = false,
) {
  val compiledRegex by lazy { Regex(regex) }
}
