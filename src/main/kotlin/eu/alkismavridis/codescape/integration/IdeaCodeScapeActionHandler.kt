package eu.alkismavridis.codescape.integration

import com.intellij.ide.FileSelectInContext
import com.intellij.ide.SelectInManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import java.nio.file.Path
import com.intellij.openapi.diagnostic.Logger

class IdeaCodeScapeActionHandler(
  private val project: Project,
  private val projectRoot: Path,
  private val onReload: () -> Unit
): CodeScapeActionHandler {

  override fun openLeafNode(nodeId: String) {
    LOGGER.info("Node clicked: $nodeId")

    val virtualFile = VirtualFileManager.getInstance().findFileByNioPath(projectRoot.resolve(nodeId))
    if (virtualFile == null) {
      LOGGER.warn("File clicked, but not found: $nodeId")
    } else {
      LOGGER.info("Opening file: $nodeId")
      OpenFileDescriptor(this.project, virtualFile, 0).navigate(true)
    }
  }

  override fun showNodeInViewer(nodeId: String) {
    val virtualFile = VirtualFileManager.getInstance().findFileByNioPath(projectRoot.resolve(nodeId))
    if (virtualFile == null) {
      LOGGER.warn("File clicked, but not found: $nodeId")
    } else {
      LOGGER.info("Showing file: $nodeId in project tree")
      val ctx = FileSelectInContext(this.project, virtualFile, null)
      SelectInManager.getInstance(project).targetList.find {
        ctx.selectIn(it, true)
      }
    }
  }

  override fun runReadOnlyTask(runnable: () -> Unit) {
    ApplicationManager.getApplication().runReadAction(runnable)
  }

  override fun handleReload() = this.onReload()

  companion object {
    private val LOGGER = Logger.getInstance(IdeaCodeScapeActionHandler::class.java)
  }
}
