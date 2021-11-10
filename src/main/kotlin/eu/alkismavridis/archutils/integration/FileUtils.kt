package eu.alkismavridis.archutils.integration

import com.intellij.openapi.project.Project
import java.nio.file.Path
import java.nio.file.Paths

fun relativizeToProjectRootIfPossible(path: Path?, project: Project): String {
  if (path == null) return ""
  val projectRoot = project.workspaceFile?.toNioPath()?.parent?.parent

  return if (projectRoot == null || !path.startsWith(projectRoot)) {
    path.toAbsolutePath().normalize().toString()
  } else {
    projectRoot.relativize(path).toString()
  }
}

fun getAbsolutePath(pathString: String?, project: Project): Path? {
  if (pathString.isNullOrBlank()) return null
  val projectRoot = project.workspaceFile?.toNioPath()?.parent?.parent
  val path = Paths.get(pathString)

  return if (projectRoot == null) {
    path.toAbsolutePath().normalize()
  } else {
    projectRoot.resolve(path).toAbsolutePath().normalize()
  }
}
