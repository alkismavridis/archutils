package eu.alkismavridis.codescape.project

import java.util.stream.Stream

interface FsService {
  fun getChildrenOf(path: String): Stream<FileNode>
}
