package eu.alkismavridis.codescape.map

interface LayoutService {
  fun loadChildren(parent: CodeScapeNode, onPresent: () -> Unit)
}
