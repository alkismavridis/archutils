package eu.alkismavridis.codescape.integration

import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import org.jetbrains.rpc.LOG
import java.nio.file.Path

class IdeaCodeScapeActionHandler(private val project: Project, private val projectRoot: Path): CodeScapeActionHandler {

  override fun handleOpenFile(path: String) {
    val virtualFile = VirtualFileManager.getInstance().findFileByNioPath(projectRoot.resolve(path))
    if (virtualFile == null) {
      LOG.warn("File clicked, but not found: $path")
    } else {
      OpenFileDescriptor(this.project, virtualFile, 0).navigate(true)
    }
  }
}
