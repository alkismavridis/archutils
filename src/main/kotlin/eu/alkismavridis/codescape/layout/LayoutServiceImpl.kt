package eu.alkismavridis.codescape.layout

import eu.alkismavridis.codescape.project.FsService
import java.util.stream.Collectors.toList

class LayoutServiceImpl(
  private val conf: CodeScapeConfiguration,
  private val fsService: FsService
): LayoutService {

  override fun loadChildren(obj: CodeScapeNode, onPresent: () -> Unit) {
    if (obj.loadingState != CodeScapeNodeLoadingState.UNCHECKED) {
      return
    }

    obj.loadingState = CodeScapeNodeLoadingState.LOADING
    onPresent()

    val childFiles = this.fsService.getChildrenOf(obj.file.path).toList()
  }
}
