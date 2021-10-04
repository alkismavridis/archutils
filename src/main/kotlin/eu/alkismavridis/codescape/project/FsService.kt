package eu.alkismavridis.codescape.project

interface FsService {
  fun getChildrenOf(path: String): Sequence<FileNode>
}
