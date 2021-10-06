package eu.alkismavridis.codescape.integration

import com.intellij.application.subscribe
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import eu.alkismavridis.codescape.config.*
import eu.alkismavridis.codescape.map.CodeScapeNode
import eu.alkismavridis.codescape.map.LayoutServiceImpl
import eu.alkismavridis.codescape.fs.FileNode
import eu.alkismavridis.codescape.fs.NioFsService
import eu.alkismavridis.codescape.map.model.MapArea
import eu.alkismavridis.codescape.ui.CodeScapeView
import eu.alkismavridis.codescape.ui.ImageCache
import org.jetbrains.rpc.LOG
import java.nio.file.Path

class CodeScapeToolbarFactory : ToolWindowFactory {

  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val projectRoot = this.getProjectRoot(project)
    LOG.info("Project root detected: $projectRoot")

    val configurationService = NioCodeScapeConfigurationService(projectRoot)
    val fsService = NioFsService(configurationService, projectRoot)
    val layoutService = LayoutServiceImpl(configurationService, fsService)
    val rootNodePath = projectRoot.resolve(configurationService.getRootNodePath()).toAbsolutePath().normalize()

    val rootNode = this.createRootNode(project, rootNodePath)
    val actionHandler = IdeaCodeScapeActionHandler(project, projectRoot)
    val imageCache = ImageCache(fsService)
    val view = CodeScapeView(rootNode, layoutService, imageCache, actionHandler)
    val fileListener = CodeScapeFileListener { this.reload(view, project, rootNodePath) }
    VirtualFileManager.VFS_CHANGES.subscribe(null, fileListener)

    val content = ContentFactory.SERVICE.getInstance().createContent(view, "Codescape", false)
    toolWindow.contentManager.addContent(content)
  }

  private fun createRootNode(project: Project, rootNodePath: Path) : CodeScapeNode {
    val rootFile = FileNode(project.name, rootNodePath.toString(), true, NodeOptions(NodeVisibility.VISIBLE, null))
    val rootArea = MapArea(0.0, 0.0, 1000.0, 1000.0, null)
    return CodeScapeNode(rootFile, rootArea)
  }

  private fun reload(view: CodeScapeView, project: Project, rootNodePath: Path) {
    val newRoot = this.createRootNode(project, rootNodePath)
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
}
