package eu.alkismavridis.codescape.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.jetbrains.rpc.LOG
import java.nio.file.Files
import java.nio.file.Path

class NioCodeScapeConfigurationService(private val projectRoot: Path) : CodeScapeConfigurationService {
  private val config by lazy { this.loadConfiguration() }

  override fun getOptionsFor(projectPath: String): NodeOptions {
    val rule = this.config.rules.find {
      it.compiledRegex.matches(projectPath)
    }

    return NodeOptions(
      rule?.visibility ?: NodeVisibility.VISIBLE,
      rule?.image
    )
  }

  private fun loadConfiguration(): CodeScapeConfiguration {
    val configStream = this.projectRoot
      .resolve(".codescape/config.json")
      .takeIf { Files.exists(it) }
      ?.let { Files.newInputStream(it) }
      ?: this::class.java.classLoader.getResourceAsStream("codescape-config.json")
      ?: throw IllegalStateException("No config file found")

    val config = ObjectMapper()
      .registerKotlinModule()
      .readValue<CodeScapeConfiguration>(configStream)

    LOG.info("Loaded config with ${config.rules.size} rules")
    return config
  }
}
