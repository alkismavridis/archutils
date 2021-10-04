package eu.alkismavridis.codescape.layout

interface LayoutService {
  fun loadChildren(parent: CodeScapeNode, onPresent: () -> Unit)
}
