package eu.alkismavridis.codescape.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import eu.alkismavridis.codescape.config.*
import eu.alkismavridis.codescape.layout.CodeScapeNode
import eu.alkismavridis.codescape.layout.LayoutServiceImpl
import eu.alkismavridis.codescape.project.FileNode
import eu.alkismavridis.codescape.project.NioFsService

class FooToolbarWindowFactory : ToolWindowFactory {

  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val contentFactory = ContentFactory.SERVICE.getInstance()
    val basePath = project.projectFile?.toNioPath() ?: throw IllegalStateException("No project path found")

    val configurationService = NioCodeScapeConfigurationService(basePath)
    val fsService = NioFsService(configurationService)
    val layoutService = LayoutServiceImpl(configurationService, fsService)

    val rootObject = this.createRootNode(project)
    val view = CodeScapeView(rootObject, layoutService)
    val content = contentFactory.createContent(view, "Codescape", false)
    toolWindow.contentManager.addContent(content)
  }

  private fun createRootNode(project: Project) : CodeScapeNode {
    val rootFile = FileNode(project.name, project.basePath ?: "", true, NodeOptions(NodeVisibility.VISIBLE))
    return CodeScapeNode(rootFile, 0.0, 0.0, 1000.0, 1000.0, null)
  }
}
