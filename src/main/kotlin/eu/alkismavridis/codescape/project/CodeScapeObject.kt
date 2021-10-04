package eu.alkismavridis.codescape.project

class CodeScapeObject(
  val left: Int,
  val top: Int,
  val width: Int,
  val height: Int,
  val children: List<CodeScapeObject> = emptyList()
)
