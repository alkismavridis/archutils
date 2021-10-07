package eu.alkismavridis.codescape.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.intellij.openapi.diagnostic.Logger
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class NioCodeScapeConfigurationService(projectRoot: Path) : CodeScapeConfigurationService {
  private val config = this.loadConfiguration(projectRoot)

  override fun getOptionsFor(projectPath: String): NodeOptions {
    val rule = this.config.rules.findLast {
      it.compiledRegex.matches(projectPath)
    }

    return NodeOptions(
      rule?.visibility ?: NodeVisibility.VISIBLE,
      rule?.image,
      rule?.color,
    )
  }

  override fun getRootNodePath() = config.root

  private fun loadConfiguration(projectRoot: Path): CodeScapeConfiguration {
    val loadedConfig = projectRoot
      .resolve(".codescape/config.json")
      .takeIf { Files.exists(it) }
      ?.let { Files.newInputStream(it) }
      ?.let(this::parseConfig)
      ?: CodeScapeConfiguration()

    LOGGER.info("Loaded config with ${loadedConfig.rules.size} rules. Root: ${loadedConfig.root}")
    return loadedConfig
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
