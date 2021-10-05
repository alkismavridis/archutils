package eu.alkismavridis.codescape.project

import com.intellij.util.io.isDirectory
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

class NioFsService: FsService {

  override fun getChildrenOf(path: String): Sequence<FileNode> {
    val dirPath = FileSystems.getDefault().getPath(path)

    return Files.walk(dirPath, 1)
      .filter{ it != dirPath }
      .map(this::toFileNode)
      .iterator()
      .asSequence()
  }

  private fun toFileNode(path: Path): FileNode {
    return FileNode(path.fileName.toString(), path.toAbsolutePath().toString(), path.isDirectory())
  }
}
