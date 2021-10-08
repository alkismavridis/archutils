package eu.alkismavridis.codescape.integration

import com.intellij.application.subscribe
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import javax.swing.JPanel

class CodeScapeToolbarFactory : ToolWindowFactory {

  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val rootComponent = JPanel(BorderLayout())
    val ctx = CodeScapeApplicationContext(project, rootComponent, this::reload)
    rootComponent.add(ctx.view)

    val content = ContentFactory.SERVICE.getInstance().createContent(rootComponent, "Codescape", false)
    toolWindow.contentManager.addContent(content)

    val fileListener = CodeScapeFileListener(project) { this.reload(ctx) }
    VirtualFileManager.VFS_CHANGES.subscribe(null, fileListener)
  }

  private fun reload(ctx: CodeScapeApplicationContext) {
    ctx.rootComponent.removeAll()

    LOGGER.info("Codescape is reloading...")
    val newCtx = CodeScapeApplicationContext(ctx.project, ctx.rootComponent, this::reload)
    ctx.rootComponent.add(newCtx.view)
  }

  companion object {
    private val LOGGER = Logger.getInstance(CodeScapeToolbarFactory::class.java)
  }
}

