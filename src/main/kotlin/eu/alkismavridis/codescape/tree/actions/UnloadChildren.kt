package eu.alkismavridis.codescape.tree.actions

import eu.alkismavridis.codescape.tree.model.ChildrenLoadState
import eu.alkismavridis.codescape.tree.model.CodeScapeNode

fun CodeScapeNode.unloadChildren() {
  if(this.children.isNotEmpty()) {
    this.children = emptyList()
    this.loadingState = ChildrenLoadState.UNCHECKED
  }
}
