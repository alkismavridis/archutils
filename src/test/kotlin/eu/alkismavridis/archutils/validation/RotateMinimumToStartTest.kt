package eu.alkismavridis.archutils.validation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class RotateMinimumToStartTest {
  @Test
  fun `two empty paths should be equal`() {
    val path1 = emptyList<String>().rotateMinimumToStart()
    val path2 = emptyList<String>().rotateMinimumToStart()

    assertThat(path1).isEqualTo(path2)
    assertThat(path1.hashCode()).isEqualTo(path2.hashCode())
  }

  @Test
  fun `should not be equal to an object of different class`() {
    val path = listOf("foo", "bar").rotateMinimumToStart()
    assertThat(path).isNotEqualTo("Hello")
  }

  @Test
  fun `same paths of size 1 should be equal`() {
    val path1 = listOf("foo").rotateMinimumToStart()
    val path2 = listOf("foo").rotateMinimumToStart()

    assertThat(path1).isEqualTo(path2)
    assertThat(path1.hashCode()).isEqualTo(path2.hashCode())
  }

  @Test
  fun `different paths of size 1 should not be equal`() {
    val path1 = listOf("foo").rotateMinimumToStart()
    val path2 = listOf("bar").rotateMinimumToStart()

    assertThat(path1).isNotEqualTo(path2)
  }

  @Test
  fun `paths of same cyclic order  should be equal`() {
    val path1 = listOf("foo", "bar", "zed").rotateMinimumToStart()
    val path2 = listOf("bar", "zed", "foo").rotateMinimumToStart()
    val path3 = listOf("zed", "foo", "bar").rotateMinimumToStart()

    assertThat(path1).isEqualTo(path2).isEqualTo(path3)
    assertThat(path2).isEqualTo(path1).isEqualTo(path3)
    assertThat(path3).isEqualTo(path1).isEqualTo(path2)

    assertThat(path1.hashCode()).isEqualTo(path2.hashCode()).isEqualTo(path3.hashCode())
    assertThat(path2.hashCode()).isEqualTo(path1.hashCode()).isEqualTo(path3.hashCode())
    assertThat(path3.hashCode()).isEqualTo(path1.hashCode()).isEqualTo(path2.hashCode())
  }

  @Test
  fun `paths of different cyclic orders should not be equal`() {
    val path1 = listOf("foo", "bar", "zed").rotateMinimumToStart()
    val path2 = listOf("foo", "zed", "bar").rotateMinimumToStart()
    assertThat(path1).isNotEqualTo(path2)
  }

  @Test
  fun `paths of different size orders should not be equal`() {
    val path1 = listOf("foo", "bar", "zed").rotateMinimumToStart()
    val path2 = listOf("foo", "bar").rotateMinimumToStart()
    val path3 = listOf("bar", "zed").rotateMinimumToStart()
    assertThat(path1).isNotEqualTo(path2).isNotEqualTo(path3)
    assertThat(path2).isNotEqualTo(path1)
    assertThat(path3).isNotEqualTo(path1)
  }
}
