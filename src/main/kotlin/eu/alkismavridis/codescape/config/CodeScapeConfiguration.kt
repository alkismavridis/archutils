package eu.alkismavridis.codescape.config

class CodeScapeConfiguration(
  val root: String = "",
  val style: StyleConfiguration = StyleConfiguration(),
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

class StyleConfiguration(
  val fileColor: String = "#7A8001",
  val closedDirColor: String = "#AF7903",
  val openDirColor: String = "#6A6A6A",
  val lockedDirColor: String = "#AF7903",
  val loadingDirColor: String = "#969696",
  val labelColor: String = "#000000",
  val labelBackground: String = "#C8C8C8",
  val borderColor: String = "#000000",
  val borderWidth: Float = 1f,
  val autoOpenDirPx: Int = 250,
  val autoCloseDirPx: Int = 70,
)
