package eu.alkismavridis.codescape.integration

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import eu.alkismavridis.codescape.config.CodeScapeConfigurationService
import eu.alkismavridis.codescape.config.NioCodeScapeConfigurationService
import eu.alkismavridis.codescape.layout.LayoutServiceImpl
import eu.alkismavridis.codescape.layout.model.MapArea
import eu.alkismavridis.codescape.tree.NioTreeDataService
import eu.alkismavridis.codescape.tree.model.CodeScapeNode
import eu.alkismavridis.codescape.tree.model.NodeType
import eu.alkismavridis.codescape.tree.model.OpenState
import eu.alkismavridis.codescape.ui.CodeScapeView
import eu.alkismavridis.codescape.ui.ImageProvider
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.JPanel

class CodeScapeApplicationContext(
  val project: Project,
  val rootComponent: JPanel,
  private val onReload: (CodeScapeApplicationContext) -> Unit
) {
  private val projectRoot = this.getProjectRoot(project)

  private val configurationService = NioCodeScapeConfigurationService(projectRoot)
  private val layoutService = LayoutServiceImpl()
  private val treeDataService = NioTreeDataService(configurationService, layoutService, projectRoot)
  private val rootNodePath = projectRoot.resolve(configurationService.getRootNodePath()).toAbsolutePath().normalize()

  private val rootNode = this.createRootNode(project, rootNodePath, configurationService)
  private val actionHandler = IdeaCodeScapeActionHandler(project, projectRoot) { this.onReload(this) }
  private val imageProvider = ImageProvider(projectRoot)

  val view = CodeScapeView(rootNode, treeDataService, configurationService.getColorPalette(), imageProvider, actionHandler)


  private fun createRootNode(project: Project, rootNodePath: Path, configurationService: CodeScapeConfigurationService) : CodeScapeNode {
    val rootArea = MapArea(0.0, 0.0, 1000.0, 1000.0, null)
    val options = configurationService.getOptionsFor("")
    return CodeScapeNode(rootNodePath.toString(), project.name, NodeType.AUTO_LOADING_BRANCH, rootArea, OpenState.CLOSED, options = options)
  }

  private fun getProjectRoot(project: Project): Path {
    val contentRootPaths = ProjectRootManager.getInstance(project)
      .contentRoots
      .map { it.toNioPath().toAbsolutePath().normalize().toString() }

    val topLevelPaths = getTopLevelPaths(contentRootPaths)

    val result = topLevelPaths
      .firstOrNull()
      ?.let { Paths.get(it) }
      ?: throw IllegalStateException("Could not extract root path")

    LOGGER.info("Project root detected: $result")
    return result
  }

  companion object {
    private val LOGGER = Logger.getInstance(CodeScapeApplicationContext::class.java)
  }
}
