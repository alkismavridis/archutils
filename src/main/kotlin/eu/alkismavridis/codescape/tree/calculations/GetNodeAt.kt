package eu.alkismavridis.codescape.tree.calculations

import eu.alkismavridis.codescape.layout.calculations.containsPoint
import eu.alkismavridis.codescape.tree.model.ChildrenLoadState
import eu.alkismavridis.codescape.tree.model.CodeScapeNode
import eu.alkismavridis.codescape.tree.model.NodeType

fun CodeScapeNode.getNodeAt(absX: Double, absY: Double, prioritiseChild: Boolean): CodeScapeNode? {
  if (!this.area.containsPoint(absX, absY)) return null
  if (!prioritiseChild || this.type != NodeType.BRANCH || this.loadingState != ChildrenLoadState.LOADED) {
    return this
  }

  return this.children
    .asSequence()
    .mapNotNull { it.getNodeAt(absX, absY, prioritiseChild) }
    .firstOrNull()
}
