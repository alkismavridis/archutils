package eu.alkismavridis.codescape.layout

import eu.alkismavridis.codescape.config.CodeScapeConfiguration
import eu.alkismavridis.codescape.config.CodeScapeConfigurationService
import eu.alkismavridis.codescape.project.FileNode
import eu.alkismavridis.codescape.project.FsService
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

class LayoutServiceImpl(
  private val configurationService: CodeScapeConfigurationService,
  private val fsService: FsService
): LayoutService {

  override fun loadChildren(parent: CodeScapeNode, onPresent: () -> Unit) {
    if (!parent.file.isDirectory || parent.loadingState != ChildrenLoadState.UNCHECKED) {
      return
    }

    parent.loadingState = ChildrenLoadState.LOADING
    onPresent()

    val childFiles = this.fsService.getChildrenOf(parent.file.path).take(SIZE_LIMIT + 1).toList()
    if (childFiles.size > SIZE_LIMIT) {
      parent.loadingState = ChildrenLoadState.SIZE_TOO_LARGE
      onPresent()
    } else {
      parent.children = this.layout(parent, childFiles)
      parent.loadingState = ChildrenLoadState.LOADED
      onPresent()
    }
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

    return childFiles.mapIndexed { index, file ->
      this.createChild(parent, file, index, colCount, spacing, childSize)
    }
  }

  private fun createChild(parent: CodeScapeNode, file: FileNode, index: Int, colCount: Double, spacing: Double, size: Double): CodeScapeNode {
    val childRow = floor(index / colCount)
    val childCol = index - childRow * colCount
    val x = spacing + childCol * (size + spacing)
    val y = spacing + childRow * (size + spacing)
    val childrenLoadState = if(file.isDirectory) ChildrenLoadState.UNCHECKED else ChildrenLoadState.NO_CHILDREN

    return CodeScapeNode(file, x, y, size, size, parent, emptyList(), childrenLoadState)
  }

  companion object {
    private const val SPACING_RATIO = 0.05
    private const val SIZE_LIMIT = 100
  }
}

