package eu.alkismavridis.codescape.integration

import com.intellij.application.subscribe
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import eu.alkismavridis.codescape.config.*
import eu.alkismavridis.codescape.tree.model.CodeScapeNode
import eu.alkismavridis.codescape.layout.LayoutServiceImpl
import eu.alkismavridis.codescape.tree.NioTreeDataService
import eu.alkismavridis.codescape.tree.model.NodeType
import eu.alkismavridis.codescape.layout.model.MapArea
import eu.alkismavridis.codescape.ui.CodeScapeView
import eu.alkismavridis.codescape.ui.ImageProvider
import java.nio.file.Path

class CodeScapeToolbarFactory : ToolWindowFactory {

  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val projectRoot = this.getProjectRoot(project)
    LOGGER.info("Project root detected: $projectRoot")

    val configurationService = NioCodeScapeConfigurationService(projectRoot)
    val layoutService = LayoutServiceImpl()
    val treeDataService = NioTreeDataService(configurationService, layoutService, projectRoot)
    val rootNodePath = projectRoot.resolve(configurationService.getRootNodePath()).toAbsolutePath().normalize()

    val rootNode = this.createRootNode(project, rootNodePath, configurationService)
    val actionHandler = IdeaCodeScapeActionHandler(project, projectRoot)
    val imageProvider = ImageProvider(projectRoot)
    val view = CodeScapeView(rootNode, treeDataService, imageProvider, actionHandler)
    val fileListener = CodeScapeFileListener { this.reload(view, project, rootNodePath, configurationService) }
    VirtualFileManager.VFS_CHANGES.subscribe(null, fileListener)

    val content = ContentFactory.SERVICE.getInstance().createContent(view, "Codescape", false)
    toolWindow.contentManager.addContent(content)
  }

  private fun createRootNode(project: Project, rootNodePath: Path, configurationService: CodeScapeConfigurationService) : CodeScapeNode {
    val rootArea = MapArea(0.0, 0.0, 1000.0, 1000.0, null)
    val options = configurationService.getOptionsFor("")
    return CodeScapeNode(rootNodePath.toString(), project.name, NodeType.BRANCH, rootArea, false, options)
  }

  private fun reload(view: CodeScapeView, project: Project, rootNodePath: Path, configurationService: CodeScapeConfigurationService) {
    val newRoot = this.createRootNode(project, rootNodePath, configurationService)
    view.reload(newRoot)
  }

  private fun getProjectRoot(project: Project): Path {
    return ProjectRootManager
      .getInstance(project)
      .contentRoots
      .firstOrNull()
      ?.toNioPath()
      ?.toAbsolutePath()
      ?.normalize()
      ?: throw IllegalStateException("No project path found")
  }

  companion object {
    private val LOGGER = Logger.getInstance(CodeScapeToolbarFactory::class.java)
  }
}
