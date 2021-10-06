package eu.alkismavridis.codescape.fs

import java.io.InputStream

interface FsService {
  fun getChildrenOf(path: String): Sequence<FileNode>
  fun loadContentsOf(path: String): InputStream
}
