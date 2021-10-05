package eu.alkismavridis.codescape.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.intellij.util.io.exists
import org.jetbrains.rpc.LOG
import java.nio.file.Files
import java.nio.file.Path

class NioCodeScapeConfigurationService(private val projectRoot: Path) : CodeScapeConfigurationService {
  private val config by lazy { this.loadConfiguration() }

  override fun getOptionsFor(absolutePath: String): NodeOptions {
    return if (absolutePath.endsWith("node_modules")) {
      NodeOptions(NodeVisibility.HIDDEN)
    } else {
      NodeOptions(NodeVisibility.VISIBLE)
    }
  }

  private fun loadConfiguration(): CodeScapeConfiguration {
    val configStream = this.projectRoot
      .resolve("codescape-config.json")
      .takeIf { it.exists() }
      ?.let { Files.newInputStream(it) }
      ?: this::class.java.classLoader.getResourceAsStream("codescape-config.json")
      ?: throw IllegalStateException("No config file found")

    val config = ObjectMapper().registerKotlinModule().readValue<CodeScapeConfiguration>(configStream)
    LOG.info("Loaded config " + config.rules.size)
    return config
  }
}
