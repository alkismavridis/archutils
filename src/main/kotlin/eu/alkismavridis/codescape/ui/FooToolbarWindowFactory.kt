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


fun createDummyProject() = CodeScapeObject(0.0, 0.0, 0.0, 0.0, listOf(
  CodeScapeObject(0.0, 0.0, 100.0, 100.0, listOf(
    CodeScapeObject(10.0, 10.0, 20.0, 20.0),
    CodeScapeObject(40.0, 10.0, 20.0, 20.0),
    CodeScapeObject(70.0, 10.0, 20.0, 20.0),
  )),
  CodeScapeObject(200.0, 200.0, 100.0, 100.0, listOf(
    CodeScapeObject(10.0, 10.0, 20.0, 20.0),
    CodeScapeObject(70.0, 10.0, 20.0, 20.0),
  )),
  CodeScapeObject(600.0, 600.0, 100.0, 100.0, listOf(
    CodeScapeObject(10.0, 10.0, 20.0, 20.0),
  )),
))
