package eu.alkismavridis.codescape.integration

import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import org.jetbrains.rpc.LOG
import java.nio.file.Paths

class IdeaCodeScapeActionHandler(private val project: Project): CodeScapeActionHandler {

  override fun handleOpenFile(path: String) {
    val virtualFile = VirtualFileManager.getInstance().findFileByNioPath(Paths.get(path))
    if (virtualFile == null) {
      LOG.warn("File clicked, but not found: $path")
    } else {
      OpenFileDescriptor(this.project, virtualFile, 0).navigate(true)
    }
  }
}
