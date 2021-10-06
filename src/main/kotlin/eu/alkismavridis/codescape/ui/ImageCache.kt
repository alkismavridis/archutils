package eu.alkismavridis.codescape.ui

import eu.alkismavridis.codescape.fs.FsService
import org.jetbrains.rpc.LOG
import java.awt.Image
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO

class ImageCache(private val fsService: FsService) {
  private val map = ConcurrentHashMap<String, Image>()

  fun getImage(path: String): Image {
    return map.computeIfAbsent(path) {
      LOG.info("Loading image \"$path\"")
      ImageIO.read(fsService.loadContentsOf(path))
    }
  }
}
