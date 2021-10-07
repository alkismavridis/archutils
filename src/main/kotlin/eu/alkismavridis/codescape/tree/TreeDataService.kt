package eu.alkismavridis.codescape.tree

import java.io.InputStream

interface TreeDataService {
  fun loadChildren(parent: CodeScapeNode, onPresent: () -> Unit)
  fun loadContentsOf(path: String): InputStream
}
