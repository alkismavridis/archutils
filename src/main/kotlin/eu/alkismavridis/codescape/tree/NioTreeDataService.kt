package eu.alkismavridis.codescape.tree

import eu.alkismavridis.codescape.config.CodeScapeConfigurationService
import eu.alkismavridis.codescape.config.NodeOptions
import eu.alkismavridis.codescape.config.NodeVisibility
import eu.alkismavridis.codescape.layout.LayoutService
import eu.alkismavridis.codescape.layout.model.MapArea
import eu.alkismavridis.codescape.tree.actions.unloadChildren
import eu.alkismavridis.codescape.tree.model.ChildrenLoadState
import eu.alkismavridis.codescape.tree.model.CodeScapeNode
import eu.alkismavridis.codescape.tree.model.NodeType
import eu.alkismavridis.codescape.tree.model.OpenState
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors.toList

class NioTreeDataService(
  private val configService: CodeScapeConfigurationService,
  private val layoutService: LayoutService,
  private val projectRoot: Path,
): TreeDataService {

  override fun loadChildren(parent: CodeScapeNode, onPresent: () -> Unit) {
    if (parent.loadingState != ChildrenLoadState.UNCHECKED || parent.type in CLOSED_NODE_TYPES) {
      return
    }

    parent.loadingState = ChildrenLoadState.LOADING
    onPresent()

    val files = this.getFiles(parent.id)
    if (files.size > SIZE_LIMIT) {
      parent.type = NodeType.SIMPLE_BRANCH
      parent.loadingState = ChildrenLoadState.LOADED
      onPresent()
    } else {
      parent.children = this.createChildren(parent, files)
      parent.loadingState = ChildrenLoadState.LOADED
      onPresent()
    }
  }

  override fun openNode(node: CodeScapeNode, isExplicit: Boolean, onPresent: () -> Unit) {
    if(node.openState.isOpen) return

    if (isExplicit || node.options.visibility == NodeVisibility.VISIBLE) {
      node.openState = if(isExplicit) OpenState.EXPLICITLY_OPEN else OpenState.OPEN
    }
  }

  override fun closeNode(node: CodeScapeNode, isExplicit: Boolean, onPresent: () -> Unit) {
    if (!node.openState.isOpen) return

    node.openState = if(isExplicit) OpenState.EXPLICITLY_CLOSED else OpenState.CLOSED
    node.unloadChildren()
    onPresent()
  }

  private fun getFiles(parentPath: String) : List<FileData> {
    return Files.walk(this.projectRoot.resolve(parentPath), 1)
      .skip(1)
      .map(this::toFileData)
      .filter { it.options.visibility != NodeVisibility.HIDDEN }
      .limit(SIZE_LIMIT + 1L)
      .sorted { f1, f2 -> f1.name.compareTo(f2.name) }
      .collect(toList())
  }

  private fun toFileData(path: Path): FileData {
    val projectPath = projectRoot.relativize(path).toString()
    val nodeOptions = this.configService.getOptionsFor(projectPath)
    return FileData(path.fileName.toString(), projectPath, Files.isDirectory(path), nodeOptions)
  }

  private fun createChildren(parent: CodeScapeNode, files: List<FileData>): List<CodeScapeNode> {
    return this.layoutService
      .layout(parent.area, files)
      .map { this.createNode(it.data, it.area) }
      .toList()
  }

  private fun createNode(fileData: FileData, area: MapArea) : CodeScapeNode {
    val nodeType = when {
      !fileData.isDirectory -> NodeType.LEAF
      fileData.options.visibility == NodeVisibility.CLOSED -> NodeType.SIMPLE_BRANCH
      else -> NodeType.AUTO_LOADING_BRANCH
    }

    val childLoadState = if(fileData.isDirectory) ChildrenLoadState.UNCHECKED else ChildrenLoadState.LOADED

    return CodeScapeNode(
      fileData.path,
      fileData.name,
      nodeType,
      area,
      OpenState.CLOSED,
      fileData.options,
      emptyList(),
      childLoadState
    )
  }

  private class FileData(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val options: NodeOptions
  )

  companion object {
    private const val SIZE_LIMIT = 100
    private val CLOSED_NODE_TYPES = listOf(NodeType.LEAF, NodeType.LOCKED_BRANCH)
  }
}
