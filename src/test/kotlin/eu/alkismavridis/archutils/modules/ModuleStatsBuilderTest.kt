package eu.alkismavridis.archutils.modules

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ModuleStatsBuilderTest {

  @Test
  fun `should be empty if no dependencies are registered`() {
    val builder = ModuleStatsBuilder("/foo/bar/", ALLOW_ALL_EXTENSIONS)
    assertThat(builder.build()).isEmpty()
  }

  @Test
  fun `should register internal dependency`() {
    val builder = ModuleStatsBuilder("/foo/bar/", ALLOW_ALL_EXTENSIONS)
      .addFile("/foo/bar/module1/File1a", listOf("/foo/bar/module1/File1b"))
      .addFile("/foo/bar/module1/File1b", listOf("/foo/bar/module1/inner/File1c"))
      .build()

    assertThat(builder).hasSize(1)
    builder[0].assertStats("module1", 2, 2, 0, 2, 0, 0)
  }

  @Test
  fun `should register cross-module dependencies`() {
    val builder = ModuleStatsBuilder("/foo/bar/", ALLOW_ALL_EXTENSIONS)
      .addFile("/foo/bar/module2/File2a", listOf("/foo/bar/module1/File1a"))
      .addFile("/foo/bar/module1/File1b", listOf("/foo/bar/module3/inner/File3a", "/foo/bar/module2/inner/File2b"))
      .build()

    assertThat(builder).hasSize(3)
    builder[0].assertStats("module1", 1, 0, 1, 0, 2, 1)
    builder[1].assertStats("module2", 1, 0, 1, 0, 1, 1)
    builder[2].assertStats("module3", 0, 0, 0, 0, 0, 1)
  }

  @Test
  fun `should assign direct children of root module to root`() {
    val builder = ModuleStatsBuilder("/foo/bar/", ALLOW_ALL_EXTENSIONS)
      .addFile("/foo/bar/Direct1", listOf("/foo/bar/module1/File1a", "/foo/bar/Direct2"))
      .addFile("/foo/bar/module1/File1a", listOf("/foo/bar/module2/inner/File2b"))
      .addFile("/foo/bar/module2/File2a", listOf("/foo/bar/Direct3"))
      .build()

    assertThat(builder).hasSize(3)
    builder[0].assertStats("<ROOT>", 1, 1, 1, 1, 1, 1)
    builder[1].assertStats("module1", 1, 0, 1, 0, 1, 1)
    builder[2].assertStats("module2", 1, 0, 1, 0, 1, 1)
  }

  @Test
  fun `root path with no leading slash should be treated the same`() {
    val builder = ModuleStatsBuilder("/foo/bar", ALLOW_ALL_EXTENSIONS)
      .addFile("/foo/bar/Direct1", listOf("/foo/bar/module1/File1a", "/foo/bar/Direct2", "/foo/bar/module2/File2a"))
      .addFile("/foo/bar/module1/File1a", listOf("/foo/bar/module2/inner/File2b"))
      .addFile("/foo/bar/module2/File2a", listOf("/foo/bar/Direct3"))
      .build()

    assertThat(builder).hasSize(3)
    assertThat(builder[0].usedByModules).containsExactly("module1", "module2")
    assertThat(builder[1].usedByModules).containsExactly("module2")
    assertThat(builder[2].usedByModules).containsExactly("<ROOT>")

    assertThat(builder[0].usesModules).containsExactly("module2")
    assertThat(builder[1].usesModules).containsExactly("<ROOT>")
    assertThat(builder[2].usesModules).containsExactly("<ROOT>", "module1")
  }

  @Test
  fun `should track used and dependent modules`() {
    val builder = ModuleStatsBuilder("/foo/bar", ALLOW_ALL_EXTENSIONS)
      .addFile("/foo/bar/module2/File2a", listOf("/foo/bar/module1/File1a"))
      .addFile("/foo/bar/module1/File1b", listOf("/foo/bar/module3/inner/File3a", "/foo/bar/module2/inner/File2c"))
      .build()

    assertThat(builder).hasSize(3)
    builder[0].assertStats("module1", 1, 0, 1, 0, 2, 1)
    builder[1].assertStats("module2", 1, 0, 1, 0, 1, 1)
    builder[2].assertStats("module3", 0, 0, 0, 0, 0, 1)
  }
  
  @Test
  fun `files ending with allowed extensions should be accepted`() {
    val builder = ModuleStatsBuilder("/foo/bar", setOf(".java", ".ts", ".scss"))
    
    assertThat(builder.accepts("/foo/bar/zoo/MyClass.java")).isTrue
    assertThat(builder.accepts("/foo/bar/zoo/MyFile.ts")).isTrue
    assertThat(builder.accepts("/foo/bar/zoo/MyStyles.scss")).isTrue
  }

  @Test
  fun `files ending with forbidden extensions should be discarded`() {
    val builder = ModuleStatsBuilder("/foo/bar", setOf(".java", ".ts", ".scss"))

    assertThat(builder.accepts("/foo/bar/zoo/MyClass.js")).isFalse
    assertThat(builder.accepts("/foo/bar/zoo/MyFile.xml")).isFalse
    assertThat(builder.accepts("/foo/bar/zoo/MyStyles.png")).isFalse
  }

  @Test
  fun `wildcard extension should accept everything`() {
    val builder = ModuleStatsBuilder("/foo/bar", setOf("*"))

    assertThat(builder.accepts("/foo/bar/zoo/MyClass.js")).isTrue
    assertThat(builder.accepts("/foo/bar/zoo/MyFile.kt")).isTrue
    assertThat(builder.accepts("/foo/bar/zoo/MyStyles.java")).isTrue
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

  companion object {
    private val ALLOW_ALL_EXTENSIONS = setOf("*")
  }
}
