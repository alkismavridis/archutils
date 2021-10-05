package eu.alkismavridis.codescape.project

import eu.alkismavridis.codescape.config.CodeScapeConfigurationService
import eu.alkismavridis.codescape.config.NodeVisibility
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

class NioFsService(
  private val configService: CodeScapeConfigurationService,
): FsService {

  override fun getChildrenOf(path: String): Sequence<FileNode> {
    val dirPath = FileSystems.getDefault().getPath(path)

    return Files.walk(dirPath, 1)
      .filter { it != dirPath }
      .map(this::toFileNode)
      .filter { it.options.visibility != NodeVisibility.HIDDEN }
      .iterator()
      .asSequence()
  }

  private fun toFileNode(path: Path): FileNode {
    val absPath = path.toAbsolutePath().toString()
    val nodeOptions = this.configService.getOptionsFor(absPath)
    return FileNode(path.fileName.toString(), absPath, Files.isDirectory(path), nodeOptions)
  }
}
