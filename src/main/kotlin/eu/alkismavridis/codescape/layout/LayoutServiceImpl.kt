package eu.alkismavridis.codescape.layout

import eu.alkismavridis.codescape.project.FileNode
import eu.alkismavridis.codescape.project.FsService
import org.jetbrains.rpc.LOG
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

class LayoutServiceImpl(
  private val conf: CodeScapeConfiguration,
  private val fsService: FsService
): LayoutService {

  override fun loadChildren(parent: CodeScapeNode, onPresent: () -> Unit) {
    if (parent.loadingState != CodeScapeNodeLoadingState.UNCHECKED) {
      return
    }

    parent.loadingState = CodeScapeNodeLoadingState.LOADING
    onPresent()

    val childFiles = this.fsService.getChildrenOf(parent.file.path).toList()

    parent.children = this.layout(parent, childFiles)
    parent.loadingState = CodeScapeNodeLoadingState.LOADED
    onPresent()
  }

  private fun layout(parent: CodeScapeNode, childFiles: List<FileNode>): List<CodeScapeNode> {
    if(childFiles.isEmpty()) return emptyList()

    val parentAspectRatio = parent.width / parent.height
    val spacing = min(parent.width, parent.height) * SPACING_RATIO
    val rowCount = floor( sqrt(childFiles.size / parentAspectRatio) )
    val colCount = floor(childFiles.size / rowCount)
    val childSize = min(
      (parent.width - spacing) / colCount - spacing,
      (parent.height - spacing) / rowCount - spacing,
    )

    LOG.info("\n\n\nparentAspectRatio $parentAspectRatio\nspacing: $spacing\n, rowCount: $rowCount\n colCount: $colCount\nchildSize: $childSize\n\n\n")

    return childFiles.mapIndexed { index, file ->
      this.createChild(file, index, colCount, spacing, childSize)
    }
  }

  private fun createChild(file: FileNode, index: Int, colCount: Double, spacing: Double, size: Double): CodeScapeNode {
    val childRow = floor(index / colCount)
    val childCol = index - childRow * colCount
    val x = spacing + childCol * (size + spacing)
    val y = spacing + childRow * (size + spacing)

    return CodeScapeNode(file, x, y, size, size)
  }

  companion object {
    private const val SPACING_RATIO = 0.05
  }
}

