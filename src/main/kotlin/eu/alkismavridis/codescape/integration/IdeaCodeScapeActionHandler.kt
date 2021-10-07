package eu.alkismavridis.codescape.integration

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import java.nio.file.Path
import com.intellij.openapi.diagnostic.Logger

class IdeaCodeScapeActionHandler(private val project: Project, private val projectRoot: Path): CodeScapeActionHandler {

  override fun handleNodeClick(nodeId: String) {
    LOGGER.info("Node clicked: $nodeId")

    val virtualFile = VirtualFileManager.getInstance().findFileByNioPath(projectRoot.resolve(nodeId))
    if (virtualFile == null) {
      LOGGER.warn("File clicked, but not found: $nodeId")
    } else {
      LOGGER.info("Opening file: $nodeId")
      OpenFileDescriptor(this.project, virtualFile, 0).navigate(true)
    }
  }

  override fun runReadOnlyTask(runnable: () -> Unit) {
    ApplicationManager.getApplication().runReadAction(runnable)
  }

  companion object {
    private val LOGGER = Logger.getInstance(IdeaCodeScapeActionHandler::class.java)
  }
}
