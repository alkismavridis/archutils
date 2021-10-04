package eu.alkismavridis.codescape.project

class CodeScapeObject(
  val left: Double,
  val top: Double,
  val width: Double,
  val height: Double,
  val children: List<CodeScapeObject> = emptyList()
)
