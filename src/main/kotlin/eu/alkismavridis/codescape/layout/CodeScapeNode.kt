package eu.alkismavridis.codescape.layout

import eu.alkismavridis.codescape.project.FileNode

class CodeScapeNode(
  val file: FileNode,
  val x: Double,
  val y: Double,
  val width: Double,
  val height: Double,
  var children: List<CodeScapeNode> = emptyList(),
  var loadingState: CodeScapeNodeLoadingState = CodeScapeNodeLoadingState.UNCHECKED
)

enum class CodeScapeNodeLoadingState {
  UNCHECKED, LOADING, LOADED, SIZE_TOO_LARGE
}
