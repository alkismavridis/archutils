package eu.alkismavridis.archutils.analysis

import eu.alkismavridis.archutils.analysis.model.ModuleStats
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ModuleStatsBuilderTest {

  @Test
  fun `should be empty if no dependencies are registered`() {
    val result = ModuleStatsBuilder("/foo/bar/")
    assertThat(result.build()).isEmpty()
  }

  @Test
  fun `should register internal dependency`() {
    val result = ModuleStatsBuilder("/foo/bar/")
      .addFile("/foo/bar/module1/File1a", listOf("/foo/bar/module1/File1b"))
      .addFile("/foo/bar/module1/File1b", listOf("/foo/bar/module1/inner/File1c"))
      .build()

    assertThat(result).hasSize(1)
    result[0].assertStats("module1", 2, 2, 0, 2, 0, 0)
  }

  @Test
  fun `should register cross-module dependencies`() {
    val result = ModuleStatsBuilder("/foo/bar/")
      .addFile("/foo/bar/module2/File2a", listOf("/foo/bar/module1/File1a"))
      .addFile("/foo/bar/module1/File1b", listOf("/foo/bar/module3/inner/File3a", "/foo/bar/module2/inner/File2b"))
      .build()

    assertThat(result).hasSize(3)
    result[0].assertStats("module1", 1, 0, 1, 0, 2, 1)
    result[1].assertStats("module2", 1, 0, 1, 0, 1, 1)
    result[2].assertStats("module3", 0, 0, 0, 0, 0, 1)
  }

  @Test
  fun `should assign direct children of root module to root`() {
    val result = ModuleStatsBuilder("/foo/bar/")
      .addFile("/foo/bar/Direct1", listOf("/foo/bar/module1/File1a", "/foo/bar/Direct2"))
      .addFile("/foo/bar/module1/File1a", listOf("/foo/bar/module2/inner/File2b"))
      .addFile("/foo/bar/module2/File2a", listOf("/foo/bar/Direct3"))
      .build()

    assertThat(result).hasSize(3)
    result[0].assertStats("<ROOT>", 1, 1, 1, 1, 1, 1)
    result[1].assertStats("module1", 1, 0, 1, 0, 1, 1)
    result[2].assertStats("module2", 1, 0, 1, 0, 1, 1)
  }

  @Test
  fun `root path with no leading slash should be treated the same`() {
    val result = ModuleStatsBuilder("/foo/bar")
      .addFile("/foo/bar/Direct1", listOf("/foo/bar/module1/File1a", "/foo/bar/Direct2", "/foo/bar/module2/File2a"))
      .addFile("/foo/bar/module1/File1a", listOf("/foo/bar/module2/inner/File2b"))
      .addFile("/foo/bar/module2/File2a", listOf("/foo/bar/Direct3"))
      .build()

    assertThat(result).hasSize(3)
    assertThat(result[0].usedByModules).containsExactly("module1", "module2")
    assertThat(result[1].usedByModules).containsExactly("module2")
    assertThat(result[2].usedByModules).containsExactly("<ROOT>")

    assertThat(result[0].usesModules).containsExactly("module2")
    assertThat(result[1].usesModules).containsExactly("<ROOT>")
    assertThat(result[2].usesModules).containsExactly("<ROOT>", "module1")
  }

  @Test
  fun `should track used and dependent modules`() {
    val result = ModuleStatsBuilder("/foo/bar")
      .addFile("/foo/bar/module2/File2a", listOf("/foo/bar/module1/File1a"))
      .addFile("/foo/bar/module1/File1b", listOf("/foo/bar/module3/inner/File3a", "/foo/bar/module2/inner/File2c"))
      .build()

    assertThat(result).hasSize(3)
    result[0].assertStats("module1", 1, 0, 1, 0, 2, 1)
    result[1].assertStats("module2", 1, 0, 1, 0, 1, 1)
    result[2].assertStats("module3", 0, 0, 0, 0, 0, 1)
  }


  private fun ModuleStats.assertStats(
    name: String,
    files: Int,
    internallyUsedFile: Int,
    externallyUsedFile: Int,
    internalDependencies: Int,
    dependenciesComingIn: Int,
    dependenciesGoingOut: Int
  ) {
    assertThat(this.name).isEqualTo(name)
    assertThat(this.files).isEqualTo(files)
    assertThat(this.internallyUsedFiles).isEqualTo(internallyUsedFile)
    assertThat(this.externallyUsedFiles).isEqualTo(externallyUsedFile)
    assertThat(this.internalDependencies).isEqualTo(internalDependencies)
    assertThat(this.dependenciesGoingOut).isEqualTo(dependenciesGoingOut)
    assertThat(this.dependenciesComingIn).isEqualTo(dependenciesComingIn)
  }
}
