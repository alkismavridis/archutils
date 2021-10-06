package eu.alkismavridis.codescape.fs

import eu.alkismavridis.codescape.config.NodeOptions

class FileNode(
  val name: String,
  val path: String,
  val isDirectory: Boolean,
  val options: NodeOptions
)
