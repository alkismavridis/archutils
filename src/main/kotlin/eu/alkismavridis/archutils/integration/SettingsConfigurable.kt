package eu.alkismavridis.archutils.integration

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField
import org.apache.commons.io.filefilter.SuffixFileFilter
import java.awt.Dimension
import java.io.FileFilter
import javax.swing.*
import javax.swing.BoxLayout.LINE_AXIS
import javax.swing.BoxLayout.PAGE_AXIS
import javax.swing.filechooser.FileNameExtensionFilter

class SettingsConfigurable(private val project: Project): Configurable {
  private val textField = JBTextField()

  override fun getDisplayName() = "Archutils Settings"

  override fun createComponent(): JComponent {
    val result = JPanel().also {
      it.layout = BoxLayout(it, PAGE_AXIS)
      it.alignmentX = JPanel.LEFT_ALIGNMENT
    }

    val label = JLabel("Configuration file path:").also {
      it.alignmentX = JPanel.LEFT_ALIGNMENT
    }

    textField.also {
      it.alignmentX = JPanel.LEFT_ALIGNMENT
      it.maximumSize = Dimension(600, 40)
      it.text = this.loadConfigPath()
    }

    val button = JButton("...").also {
      it.addActionListener { this.readFile() }
    }

    val row = JPanel().also {
      it.layout = BoxLayout(it, LINE_AXIS)
      it.alignmentX = JPanel.LEFT_ALIGNMENT
      it.add(textField)
      it.add(button)
    }

    result.add(label)
    result.add(row)
    return result
  }

  override fun isModified(): Boolean {
    val oldValue = this.loadConfigPath()
    val newValue = this.textField.text
    return oldValue != newValue
  }

  override fun apply() {
    val newValue = textField.text
    thisLogger().info("Changing configuration path to: $newValue")
    this.storeConfigPath(newValue)
  }

  private fun readFile() {
    val parentDir = getAbsolutePath(this.textField.text, this.project)?.parent
    val chooser = JFileChooser(parentDir?.toFile()).also {
      it.fileSelectionMode = JFileChooser.FILES_ONLY
      it.fileFilter = FileNameExtensionFilter("Json", "json")
    }

    val result = chooser.showOpenDialog(this.textField)
    if(result == JFileChooser.APPROVE_OPTION) {
      this.textField.text = relativizeToProjectRootIfPossible(chooser.selectedFile?.toPath(), this.project)
    }
  }

  private fun loadConfigPath() = PropertiesComponent.getInstance(this.project).getValue(STORAGE_KEY)
  private fun storeConfigPath(newValue: String) = PropertiesComponent.getInstance(this.project).setValue(STORAGE_KEY, newValue)


  companion object {
    const val STORAGE_KEY = "eu.alkismavridis.archutils.integration.CONFIG_FILE_PATH"
  }
}
