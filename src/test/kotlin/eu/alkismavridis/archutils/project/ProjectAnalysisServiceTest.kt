package eu.alkismavridis.archutils.project

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ProjectAnalysisServiceTest {

  @Test
  fun `should return incoming modules`() {
    val modules = listOf(
      DummyModuleStats("foo", internalDependencies = 5),
      DummyModuleStats("bar", dependenciesGoingOut = 8)
    )

    val request = AnalysisRequest("src", DependencyRules.allowAll())
    val result = ProjectAnalysisService().analyse(request, modules)
    assertThat(result.moduleStats).isEqualTo(modules)
  }



  private class DummyModuleStats(
    override val name: String,
    override val files: Int = 0,
    override val internallyUsedFiles: Int = 0,
    override val externallyUsedFiles: Int = 0,
    override val internalDependencies: Int = 0,
    override val dependenciesComingIn: Int = 0,
    override val dependenciesGoingOut: Int = 0,
    override val usesModules: Set<String> = emptySet(),
    override val usedByModules: Set<String> = emptySet()
  ) : ModuleStats
}
