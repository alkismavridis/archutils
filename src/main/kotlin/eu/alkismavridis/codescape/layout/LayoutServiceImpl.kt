package eu.alkismavridis.codescape.layout

class LayoutServiceImpl(private val conf: CodeScapeConfiguration): LayoutService {

  override fun loadChildren(obj: CodeScapeNode, presenter: () -> Unit) {
    obj.children
  }
}
