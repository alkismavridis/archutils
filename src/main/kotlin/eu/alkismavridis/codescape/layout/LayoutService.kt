package eu.alkismavridis.codescape.layout

interface LayoutService {
  fun loadChildren(obj: CodeScapeNode, presenter: () -> Unit)
}
