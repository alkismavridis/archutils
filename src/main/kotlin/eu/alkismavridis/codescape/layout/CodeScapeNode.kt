package eu.alkismavridis.codescape.layout

class CodeScapeNode(
  val path: String,
  val x: Double,
  val y: Double,
  val width: Double,
  val height: Double,
  val children: List<CodeScapeNode> = emptyList()
)
