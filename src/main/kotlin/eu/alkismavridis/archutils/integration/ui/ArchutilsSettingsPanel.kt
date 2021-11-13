package eu.alkismavridis.archutils.integration.ui

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

class ArchutilsSettingsPanel(
  private val project: Project,
  private val initialConfigPath: String
): JPanel() {
  private val fileChooser = TextFieldWithBrowseButton()

  val currentPath get() = this.fileChooser.text
  init { this.setup() }

  private fun setup() {
    this.layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
    this.alignmentX = LEFT_ALIGNMENT

    val label = JLabel("Configuration file path:").also {
      it.alignmentX = LEFT_ALIGNMENT
    }

    fileChooser.text = initialConfigPath
    fileChooser.alignmentX = LEFT_ALIGNMENT
    fileChooser.maximumSize = Dimension(600, 40)

    val fd = FileChooserDescriptorFactory.createSingleFileDescriptor()
    fileChooser.addBrowseFolderListener(TextBrowseFolderListener(fd, this.project))

    this.add(label)
    this.add(fileChooser)
  }
}
