package eu.alkismavridis.codescape.tree

import eu.alkismavridis.codescape.tree.model.CodeScapeNode
import java.io.InputStream

interface TreeDataService {
  fun loadChildren(parent: CodeScapeNode, onPresent: () -> Unit)
  fun openNode(node: CodeScapeNode, isExplicit: Boolean, onPresent: () -> Unit)
  fun closeNode(node: CodeScapeNode, isExplicit: Boolean, onPresent: () -> Unit)
}
