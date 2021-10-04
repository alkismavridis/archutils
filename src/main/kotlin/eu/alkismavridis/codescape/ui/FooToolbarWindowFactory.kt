package eu.alkismavridis.codescape.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import eu.alkismavridis.codescape.project.CodeScapeProject
import eu.alkismavridis.codescape.ui.panel.CodeScapeView

class FooToolbarWindowFactory : ToolWindowFactory {

  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

    val contentFactory = ContentFactory.SERVICE.getInstance()
    val project = CodeScapeProject()
    val view = CodeScapeView(project)
    val content = contentFactory.createContent(view, "Codescape", false)
    toolWindow.contentManager.addContent(content)
  }
}
