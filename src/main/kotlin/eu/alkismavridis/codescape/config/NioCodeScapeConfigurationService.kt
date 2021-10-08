package eu.alkismavridis.codescape.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.diagnostic.Logger
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class NioCodeScapeConfigurationService(private val projectRoot: Path) : CodeScapeConfigurationService {
  private var config = this.loadConfiguration()

  override fun getOptionsFor(projectPath: String): NodeOptions {
    val rule = this.config.rules.findLast {
      it.compiledRegex.matches(projectPath)
    }

    return NodeOptions(
      rule?.visibility ?: NodeVisibility.VISIBLE,
      rule?.image,
      rule?.color,
      rule?.openColor,
      rule?.borderColor,
      rule?.borderWidth,
      rule?.hideIcon ?: false,
    )
  }

  override fun getRootNodePath() = config.root

  override fun getColorPalette() = config.style

  override fun reload() {
    this.config = loadConfiguration()
  }

  private fun loadConfiguration(): CodeScapeConfiguration {
    try {
      val loadedConfig = this.projectRoot
        .resolve(".codescape/config.json")
        .takeIf { Files.exists(it) }
        ?.let { Files.newInputStream(it) }
        ?.let(this::parseConfig)
        ?: CodeScapeConfiguration()

      LOGGER.info("Loaded config with ${loadedConfig.rules.size} rules. Root: ${loadedConfig.root}")
      return loadedConfig
    } catch (e: Exception) {
      Notifications.Bus.notify(Notification("CodescapeNotification", "Could not load Codescape configuration: ${e.message}", NotificationType.WARNING))
      return CodeScapeConfiguration()
    }
  }

  private fun parseConfig(input: InputStream): CodeScapeConfiguration {
    return ObjectMapper()
      .registerKotlinModule()
      .readValue(input)
  }

  companion object {
    private val LOGGER = Logger.getInstance(NioCodeScapeConfigurationService::class.java)
  }
}
