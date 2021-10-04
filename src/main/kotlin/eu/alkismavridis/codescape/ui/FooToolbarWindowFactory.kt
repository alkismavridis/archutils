package eu.alkismavridis.codescape.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import eu.alkismavridis.codescape.layout.CodeScapeNode
import eu.alkismavridis.codescape.project.FileNode
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


private fun createDummyProject() = CodeScapeNode(DUMMY_FILE, 0.0, 0.0, 0.0, 0.0, listOf(
  CodeScapeNode(DUMMY_FILE, 0.0, 0.0, 100.0, 100.0, listOf(
    CodeScapeNode(DUMMY_FILE, 10.0, 10.0, 20.0, 20.0),
    CodeScapeNode(DUMMY_FILE, 40.0, 10.0, 20.0, 20.0),
    CodeScapeNode(DUMMY_FILE, 70.0, 10.0, 20.0, 20.0),
  )),
  CodeScapeNode(DUMMY_FILE, 200.0, 200.0, 100.0, 100.0, listOf(
    CodeScapeNode(DUMMY_FILE, 10.0, 10.0, 20.0, 20.0),
    CodeScapeNode(DUMMY_FILE, 70.0, 10.0, 20.0, 20.0),
  )),
  CodeScapeNode(DUMMY_FILE, 600.0, 600.0, 100.0, 100.0, listOf(
    CodeScapeNode(DUMMY_FILE, 10.0, 10.0, 20.0, 20.0),
  )),
))

private val DUMMY_FILE = FileNode("foo", "/bar/foo")
