package eu.alkismavridis.codescape.project

import java.nio.file.FileSystems
import java.nio.file.Files
import java.util.stream.Collectors.toList

class NioFsService: FsService {

  override fun getChildrenOf(path: String): Sequence<FileNode> {
    val dirPath = FileSystems.getDefault().getPath(path)

    return Files.walk(dirPath)
      .map { FileNode(it.fileName.toString(), it.toAbsolutePath().toString()) }
      .collect(toList()) // TODO alkis fix this
      .asSequence()
  }
}