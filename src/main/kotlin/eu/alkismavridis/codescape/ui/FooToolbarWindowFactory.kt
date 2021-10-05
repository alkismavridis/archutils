package eu.alkismavridis.codescape.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import eu.alkismavridis.codescape.layout.CodeScapeConfiguration
import eu.alkismavridis.codescape.layout.CodeScapeNode
import eu.alkismavridis.codescape.layout.LayoutServiceImpl
import eu.alkismavridis.codescape.project.FileNode
import eu.alkismavridis.codescape.project.NioFsService

class FooToolbarWindowFactory : ToolWindowFactory {

  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val contentFactory = ContentFactory.SERVICE.getInstance()

    val fsService = NioFsService()
    val conf = CodeScapeConfiguration() // TODO alkis read real configuration
    val layoutService = LayoutServiceImpl(conf, fsService)
    val rootObject = CodeScapeNode(FileNode("", project.basePath ?: "", true), 0.0, 0.0, 1000.0, 1000.0, null)

    val view = CodeScapeView(rootObject, layoutService)
    val content = contentFactory.createContent(view, "Codescape", false)
    toolWindow.contentManager.addContent(content)
  }
}
