package eu.alkismavridis.archutils.garbage.integration

import com.intellij.openapi.project.Project
import java.nio.file.Path
import java.nio.file.Paths

fun relativizeToProjectRoot(pathString: String, project: Project): String {
  if (pathString.isEmpty()) return ""
  val path = Paths.get(pathString).toAbsolutePath().normalize()
  val projectRoot = project.workspaceFile?.toNioPath()?.parent?.parent?.toAbsolutePath()?.normalize()

  return if (projectRoot == null || !path.startsWith(projectRoot)) {
    path.toAbsolutePath().normalize().toString()
  } else {
    projectRoot.relativize(path).toString()
  }
}

fun getAbsolutePath(pathString: String, project: Project): Path {
  val projectRoot = project.workspaceFile?.toNioPath()?.parent?.parent
  val path = Paths.get(pathString)

  return if (projectRoot == null) {
    path.toAbsolutePath().normalize()
  } else {
    projectRoot.resolve(path).toAbsolutePath().normalize()
  }
}
