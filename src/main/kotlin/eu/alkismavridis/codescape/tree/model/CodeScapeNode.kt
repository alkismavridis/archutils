package eu.alkismavridis.codescape.tree.model

import eu.alkismavridis.codescape.layout.model.MapArea

class CodeScapeNode(
  val id: String,
  val label: String,
  val imageId: String?,
  val color: String?,
  var type: NodeType,
  val area: MapArea,
  var isOpen: Boolean,
  var children: List<CodeScapeNode> = emptyList(),
  var loadingState: ChildrenLoadState = ChildrenLoadState.UNCHECKED
)

