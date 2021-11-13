package eu.alkismavridis.archutils.integration

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import eu.alkismavridis.archutils.garbage.integration.relativizeToProjectRoot
import java.awt.Dimension
import javax.swing.*
import javax.swing.BoxLayout.PAGE_AXIS

class SettingsConfigurable(private val project: Project): Configurable {
  private val fileChooser = TextFieldWithBrowseButton()

  override fun getDisplayName() = "Archutils Settings"

  override fun createComponent(): JComponent {
    val result = JPanel().also {
      it.layout = BoxLayout(it, PAGE_AXIS)
      it.alignmentX = JPanel.LEFT_ALIGNMENT
    }

    val label = JLabel("Configuration file path:").also {
      it.alignmentX = JPanel.LEFT_ALIGNMENT
    }

    fileChooser.text = this.loadConfigPath() ?: ""
    fileChooser.alignmentX = JPanel.LEFT_ALIGNMENT
    fileChooser.maximumSize = Dimension(600, 40)

    val fd = FileChooserDescriptorFactory.createSingleFileDescriptor()
    fileChooser.addBrowseFolderListener(TextBrowseFolderListener(fd, this.project))

    result.add(label)
    result.add(fileChooser)
    return result
  }

  override fun isModified(): Boolean {
    val oldValue = this.loadConfigPath()
    val newValue = relativizeToProjectRoot(this.fileChooser.text, this.project)
    return oldValue != newValue
  }

  override fun apply() {
    val newValue = relativizeToProjectRoot(this.fileChooser.text, this.project)
    thisLogger().info("Changing configuration path to: $newValue")
    this.storeConfigPath(newValue)
  }

  private fun loadConfigPath() = PropertiesComponent.getInstance(this.project).getValue(STORAGE_KEY)
  private fun storeConfigPath(newValue: String) = PropertiesComponent.getInstance(this.project).setValue(STORAGE_KEY, newValue)


  companion object {
    const val STORAGE_KEY = "eu.alkismavridis.archutils.garbage.integration.CONFIG_FILE_PATH"
  }
}
