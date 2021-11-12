package eu.alkismavridis.archutils.cycles

import eu.alkismavridis.archutils.analysis.model.ModuleStats
import eu.alkismavridis.archutils.analysis.testutils.DummyModuleStats
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CyclicDependencyServiceTest {

  @Test
  fun `zero modules should return zero cycles`() {
    val modules = emptyList<ModuleStats>()
    val service = createService()
    val result = service.detectCycles(modules)

    assertThat(result).isEmpty()
  }

  @Test
  fun `should detect simple cycle`() {
    val modules = listOf(
      DummyModuleStats("module1", usesModules = setOf("module2")),
      DummyModuleStats("module2", usesModules = setOf("module1")),
    )

    val service = createService()
    val result = service.detectCycles(modules)

    assertThat(result).hasSize(1)
    assertThat(result[0]).containsExactly("module1", "module2")
  }


  private fun createService() = CyclicDependencyService()
}
