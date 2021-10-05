package eu.alkismavridis.codescape.layout

import eu.alkismavridis.codescape.project.FileNode

class CodeScapeNode(
  val file: FileNode,
  val x: Double,
  val y: Double,
  val width: Double,
  val height: Double,
  val parent: CodeScapeNode?,
  var children: List<CodeScapeNode> = emptyList(),
  var loadingState: ChildrenLoadState = ChildrenLoadState.UNCHECKED
) {
  fun unloadChildren() {
    this.children = emptyList()
    if(file.isDirectory) {
      this.loadingState = ChildrenLoadState.UNCHECKED
    }
  }

  fun getAbsoluteX() : Double {
    return if(this.parent == null) {
      this.x
    } else {
      this.x + this.parent.getAbsoluteX()
    }
  }

  fun getAbsoluteY() : Double {
    return if(this.parent == null) {
      this.y
    } else {
      this.y + this.parent.getAbsoluteY()
    }
  }
}

enum class ChildrenLoadState {
  /** Used for non-directories */
  NO_CHILDREN,
  UNCHECKED,
  LOADING,
  LOADED,
  SIZE_TOO_LARGE
}
