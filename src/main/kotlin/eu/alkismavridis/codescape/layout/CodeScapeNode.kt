package eu.alkismavridis.codescape.layout

import eu.alkismavridis.codescape.project.FileNode
import org.jetbrains.rpc.LOG

class CodeScapeNode(
  val file: FileNode,
  val x: Double,
  val y: Double,
  val width: Double,
  val height: Double,
  private val parent: CodeScapeNode?,
  var children: List<CodeScapeNode> = emptyList(),
  var loadingState: ChildrenLoadState = ChildrenLoadState.UNCHECKED
) {
  fun unloadChildren() {
    this.children = emptyList()
    if(file.isDirectory) {
      this.loadingState = ChildrenLoadState.UNCHECKED
    }
  }

  fun getAbsoluteX() : Double {
    return if(this.parent == null) {
      this.x
    } else {
      this.x + this.parent.getAbsoluteX()
    }
  }

  fun getAbsoluteY() : Double {
    return if(this.parent == null) {
      this.y
    } else {
      this.y + this.parent.getAbsoluteY()
    }
  }

  fun getNodeAt(x: Double, y: Double, prioritiseChild: Boolean): CodeScapeNode? {
    val absX = this.getAbsoluteX()
    val absY = this.getAbsoluteY()

    if (x < absX || x > absX + this.width) return null
    if (y < absY || y > absY + this.height) return null
    if (!prioritiseChild || this.loadingState != ChildrenLoadState.LOADED) {
      return this
    }

    return this.children
      .asSequence()
      .mapNotNull { it.getNodeAt(x, y, prioritiseChild) }
      .firstOrNull()
  }
}

enum class ChildrenLoadState {
  /** Used for non-directories */
  NO_CHILDREN,
  UNCHECKED,
  LOADING,
  LOADED,
  SIZE_TOO_LARGE,
  CLOSED
}
