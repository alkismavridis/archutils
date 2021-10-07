package eu.alkismavridis.codescape.tree

import eu.alkismavridis.codescape.config.CodeScapeConfigurationService
import eu.alkismavridis.codescape.config.NodeOptions
import eu.alkismavridis.codescape.config.NodeVisibility
import eu.alkismavridis.codescape.layout.LayoutService
import eu.alkismavridis.codescape.layout.model.MapArea
import eu.alkismavridis.codescape.tree.model.ChildrenLoadState
import eu.alkismavridis.codescape.tree.model.CodeScapeNode
import eu.alkismavridis.codescape.tree.model.NodeType
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors.toList

class NioTreeDataService(
  private val configService: CodeScapeConfigurationService,
  private val layoutService: LayoutService,
  private val projectRoot: Path,
): TreeDataService {

  override fun loadChildren(parent: CodeScapeNode, onPresent: () -> Unit) {
    if (parent.loadingState != ChildrenLoadState.UNCHECKED) {
      return
    } else if (parent.type == NodeType.LEAF || parent.type == NodeType.LOCKED_BRANCH) {
      parent.loadingState = ChildrenLoadState.LOADED
      return
    }

    parent.loadingState = ChildrenLoadState.LOADING
    onPresent()

    val files = Files.walk(this.projectRoot.resolve(parent.id), 1)
      .skip(1)
      .map(this::toFileNode)
      .filter { it.options.visibility != NodeVisibility.HIDDEN }
      .limit(SIZE_LIMIT + 1L)
      .collect(toList())

    if (files.size > SIZE_LIMIT) {
      parent.loadingState = ChildrenLoadState.LOADED
      parent.type = NodeType.LOCKED_BRANCH
      onPresent()
    } else {
      parent.children = this.layoutService
        .layoutIt(parent.area, files.size)
        .mapIndexed { index, area -> this.createNode(files[index], area)}
        .toList()

      parent.loadingState = ChildrenLoadState.LOADED
      onPresent()
    }
  }

  override fun loadContentsOf(path: String): InputStream {
    return Files.newInputStream(this.projectRoot.resolve(path))
  }

  private fun toFileNode(path: Path): FileNode {
    val projectPath = projectRoot.relativize(path).toString()
    val nodeOptions = this.configService.getOptionsFor(projectPath)
    return FileNode(path.fileName.toString(), projectPath, Files.isDirectory(path), nodeOptions)
  }

  private fun createNode(fileData: FileNode, area: MapArea) : CodeScapeNode {
    val childLoadState = if(fileData.isDirectory) ChildrenLoadState.UNCHECKED else ChildrenLoadState.LOADED
    val nodeType = when {
      !fileData.isDirectory -> NodeType.LEAF
      fileData.options.visibility == NodeVisibility.CLOSED -> NodeType.LOCKED_BRANCH
      else -> NodeType.BRANCH
    }

    return CodeScapeNode(fileData.path, fileData.name, fileData.options.image, nodeType, area, emptyList(), childLoadState)
  }

  private class FileNode(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val options: NodeOptions
  )

  companion object {
    private const val SIZE_LIMIT = 100
  }
}
