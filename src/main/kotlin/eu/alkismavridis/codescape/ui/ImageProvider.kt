package eu.alkismavridis.codescape.ui

import com.intellij.openapi.diagnostic.Logger
import java.awt.Image
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO

class ImageProvider(private val projectRoot: Path) {
  private val cache = ConcurrentHashMap<String, Image>()

  fun getImage(path: String): Image {
    return cache.computeIfAbsent(path) { this.loadImage(path) }
  }

  private fun loadImage(path: String): Image {
    LOGGER.info("Loading image \"$path\"")
    val imageStream = Files.newInputStream(this.projectRoot.resolve(path))
    return ImageIO.read(imageStream)
  }

  companion object {
    private val LOGGER = Logger.getInstance(ImageProvider::class.java)
  }
}
