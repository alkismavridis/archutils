package eu.alkismavridis.codescape.tree.model

import eu.alkismavridis.codescape.config.NodeOptions
import eu.alkismavridis.codescape.layout.model.MapArea

class CodeScapeNode(
  val id: String,
  val label: String,
  val type: NodeType,
  val area: MapArea,
  val autoLoad: Boolean,
  var isOpen: Boolean,
  val options: NodeOptions,
  var children: List<CodeScapeNode> = emptyList(),
  var loadingState: ChildrenLoadState = ChildrenLoadState.UNCHECKED
)

