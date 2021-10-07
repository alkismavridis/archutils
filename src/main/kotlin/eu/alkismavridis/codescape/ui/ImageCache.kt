package eu.alkismavridis.codescape.ui

import com.intellij.openapi.diagnostic.Logger
import eu.alkismavridis.codescape.tree.TreeDataService
import java.awt.Image
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO

class ImageCache(private val treeDataService: TreeDataService) {
  private val map = ConcurrentHashMap<String, Image>()

  fun getImage(path: String): Image {
    return map.computeIfAbsent(path) {
      LOGGER.info("Loading image \"$path\"")
      ImageIO.read(treeDataService.loadContentsOf(path))
    }
  }

  companion object {
    private val LOGGER = Logger.getInstance(ImageCache::class.java)
  }
}
