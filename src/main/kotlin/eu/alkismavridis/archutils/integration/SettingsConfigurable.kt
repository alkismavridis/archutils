package eu.alkismavridis.archutils.integration

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import eu.alkismavridis.archutils.integration.io.IoService
import eu.alkismavridis.archutils.integration.ui.ArchutilsSettingsPanel

class SettingsConfigurable(private val project: Project): Configurable {
  private val ioService = IoService(this.project)
  private val settingsPanel = ArchutilsSettingsPanel(this.project, ioService.getConfigPath())

  override fun getDisplayName() = "Archutils Settings"
  override fun createComponent() = this.settingsPanel

  override fun isModified(): Boolean {
    val oldValue = this.ioService.getConfigPath()
    val newValue = this.ioService.relativizeToProjectRoot(this.settingsPanel.currentPath)
    return oldValue != newValue
  }

  override fun apply() {
    val newValue = this.ioService.relativizeToProjectRoot(this.settingsPanel.currentPath)
    this.ioService.setConfigPath(newValue)
  }
}
