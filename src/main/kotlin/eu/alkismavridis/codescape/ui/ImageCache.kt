package eu.alkismavridis.codescape.ui

import org.jetbrains.rpc.LOG
import java.awt.Image
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO

class ImageCache(private val projectRoot: Path) {
  private val map = ConcurrentHashMap<String, Image>()

  fun getImage(path: String): Image {
    return map.computeIfAbsent(path) {
      LOG.info("Loading image \"$path\"")
      ImageIO.read(Files.newInputStream(projectRoot.resolve(path)))
    }
  }
}
