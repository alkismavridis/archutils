package eu.alkismavridis.codescape.tree

import eu.alkismavridis.codescape.layout.calculations.containsPoint
import eu.alkismavridis.codescape.layout.model.MapArea

class CodeScapeNode(
  val id: String,
  val label: String,
  val imageId: String?,
  var type: NodeType,
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
    if (!prioritiseChild || this.type != NodeType.BRANCH || this.loadingState != ChildrenLoadState.LOADED) {
      return this
    }

    return this.children
      .asSequence()
      .mapNotNull { it.getNodeAt(absX, absY, prioritiseChild) }
      .firstOrNull()
  }
}

enum class ChildrenLoadState { UNCHECKED, LOADING, LOADED }
enum class NodeType { BRANCH, LOCKED_BRANCH, LEAF }
