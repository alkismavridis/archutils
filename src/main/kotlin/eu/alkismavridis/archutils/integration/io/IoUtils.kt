package eu.alkismavridis.archutils.integration.io

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import eu.alkismavridis.archutils.analysis.config.ArchutilsConfiguration
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class IoService(private val project: Project) {
  fun getConfigPath(): String {
    return PropertiesComponent.getInstance(this.project).getValue(STORAGE_KEY) ?: ""
  }

  fun setConfigPath(newValue: String) {
    thisLogger().info("Changing configuration path to: $newValue")
    PropertiesComponent.getInstance(this.project).setValue(STORAGE_KEY, newValue)
  }

  fun loadConfiguration(): ArchutilsConfiguration {
    val pathString = PropertiesComponent
      .getInstance(this.project)
      .getValue(STORAGE_KEY)
      ?.ifEmpty { null }
      ?: return ArchutilsConfiguration()

    try {
      val path = getAbsolutePath(pathString)
      return ObjectMapper()
        .registerKotlinModule()
        .readValue(Files.newInputStream(path))
    } catch (e: Exception) {
      Messages.showMessageDialog(project, e.message, "Invalid Archutils Configuration ", Messages.getWarningIcon())
      return ArchutilsConfiguration()
    }
  }

  fun relativizeToProjectRoot(pathString: String): String {
    if (pathString.isEmpty()) return ""
    val path = Paths.get(pathString).toAbsolutePath().normalize()
    val projectRoot = this.project.workspaceFile?.toNioPath()?.parent?.parent?.toAbsolutePath()?.normalize()

    return if (projectRoot == null || !path.startsWith(projectRoot)) {
      path.toAbsolutePath().normalize().toString()
    } else {
      projectRoot.relativize(path).toString()
    }
  }

  private fun getAbsolutePath(projectRelativePath: String): Path {
    val projectRoot = this.project.workspaceFile?.toNioPath()?.parent?.parent
    val path = Paths.get(projectRelativePath)

    return if (projectRoot == null) {
      path.toAbsolutePath().normalize()
    } else {
      projectRoot.resolve(path).toAbsolutePath().normalize()
    }
  }

  companion object {
    private const val STORAGE_KEY = "eu.alkismavridis.archutils.CONFIG_FILE_PATH"
  }
}
