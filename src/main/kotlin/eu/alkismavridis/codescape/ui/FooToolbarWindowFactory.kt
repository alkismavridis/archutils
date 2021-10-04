package eu.alkismavridis.codescape.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import eu.alkismavridis.codescape.project.CodeScapeObject
import eu.alkismavridis.codescape.ui.swing.CodeScapeView

class FooToolbarWindowFactory : ToolWindowFactory {

  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

    val contentFactory = ContentFactory.SERVICE.getInstance()
    val rootObject = createDummyProject()
    val view = CodeScapeView(rootObject)
    val content = contentFactory.createContent(view, "Codescape", false)
    toolWindow.contentManager.addContent(content)
  }
}


fun createDummyProject() = CodeScapeObject(0, 0, 0, 0, listOf(
  CodeScapeObject(0, 0, 100, 100, listOf(
    CodeScapeObject(10, 10, 20, 20),
    CodeScapeObject(40, 10, 20, 20),
    CodeScapeObject(70, 10, 20, 20),
  )),
  CodeScapeObject(200, 200, 100, 100, listOf(
    CodeScapeObject(10, 10, 20, 20),
    CodeScapeObject(70, 10, 20, 20),
  )),
  CodeScapeObject(600, 600, 100, 100, listOf(
    CodeScapeObject(10, 10, 20, 20),
  )),
))
