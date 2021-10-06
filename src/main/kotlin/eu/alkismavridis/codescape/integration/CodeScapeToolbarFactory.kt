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
import eu.alkismavridis.codescape.project.FileNode
import eu.alkismavridis.codescape.project.NioFsService
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





    val rootObject = this.createRootNode(project)
    val actionHandler = IdeaCodeScapeActionHandler(project, projectRoot)
    val imageCache = ImageCache(projectRoot)
    val view = CodeScapeView(rootObject, layoutService, imageCache, actionHandler)
    val fileListener = CodeScapeFileListener { this.reload(view, project) }
    VirtualFileManager.VFS_CHANGES.subscribe(null, fileListener)

    val content = ContentFactory.SERVICE.getInstance().createContent(view, "Codescape", false)
    toolWindow.contentManager.addContent(content)
  }

  private fun createRootNode(project: Project) : CodeScapeNode {
    val rootFile = FileNode(project.name, project.basePath ?: "", true, NodeOptions(NodeVisibility.VISIBLE, null))
    return CodeScapeNode(rootFile, 0.0, 0.0, 1000.0, 1000.0, null)
  }

  private fun reload(view: CodeScapeView, project: Project) {
    val newRoot = this.createRootNode(project)
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
