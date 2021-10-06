package eu.alkismavridis.codescape.map

import eu.alkismavridis.codescape.fs.FileNode
import eu.alkismavridis.codescape.map.calculations.containsPoint
import eu.alkismavridis.codescape.map.model.MapArea

class CodeScapeNode(
  val file: FileNode,
  val area: MapArea,
  var children: List<CodeScapeNode> = emptyList(),
  var loadingState: ChildrenLoadState = ChildrenLoadState.UNCHECKED
) {
  fun unloadChildren() {
    if(this.children.isNotEmpty()) {
      this.children = emptyList()
      this.loadingState = ChildrenLoadState.UNCHECKED
    }
  }

  fun getNodeAt(absX: Double, absY: Double, prioritiseChild: Boolean): CodeScapeNode? {
    if (!this.area.containsPoint(absX, absY)) return null
    if (!prioritiseChild || this.loadingState != ChildrenLoadState.LOADED) {
      return this
    }

    return this.children
      .asSequence()
      .mapNotNull { it.getNodeAt(absX, absY, prioritiseChild) }
      .firstOrNull()
  }
}

enum class ChildrenLoadState {
  /** Used for non-directories */
  NO_CHILDREN,
  UNCHECKED,
  LOADING,
  LOADED,
  SIZE_TOO_LARGE,
  CLOSED
}
