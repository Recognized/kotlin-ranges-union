package com.github.recognized.kotlin.ranges.union

import com.github.recognized.kotlin.ranges.extensions.from
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LongRangeUnionTest {
  private fun createTestModel(): LongRangeUnion {
    val model = LongRangeUnion()
    for (i in 0L..9L) {
      model.union(LongRange.from(i * 20L, 10L))
    }
    return model
  }

  @Test
  fun `test exclude all ranges`() {
    val model = createTestModel()
    for (i in 0L..9L) {
      model.exclude(LongRange.from(i * 20L, 10L))
    }
    assertEquals(0, model.ranges.size)
  }

  @Test
  fun `test exclude one unit from each range`() {
    val model = createTestModel()
    for (i in 10L downTo 0L) {
      model.exclude(LongRange.from(i * 20L + 10L, 10L))
    }
    assertEquals(10, model.ranges.size)
  }

  @Test
  fun `test exclude range that cover three ranges in model`() {
    val model = createTestModel()
    model.exclude(25L..89L)
    assertEquals(7, model.ranges.size)
  }

  @Test
  fun `test exclude part of ranges`() {
    val model = createTestModel()
    model.exclude(10L..59L)
    assertEquals(8, model.ranges.size)
  }

  @Test
  fun `test add range that covers all ranges`() {
    val model = createTestModel()
    val added = -100L..100000L
    model.union(added)
    assertEquals(1, model.ranges.size)
    assertEquals(added, model.ranges[0])
  }

  @Test
  fun `test add intersecting ranges`() {
    val model = LongRangeUnion()
    model.union(0L..10L)
    model.union(20L..30L)
    model.union(9L..29L)
    assertEquals(1, model.ranges.size)
    assertEquals(0L..30L, model.ranges[0])
  }

  @Test
  fun `test fill spaces between ranges`() {
    val model = createTestModel()
    model.union(10L..179L)
    assertEquals(1, model.ranges.size)
    assertEquals(0L..189L, model.ranges[0])
  }

  @Test
  fun `test add intersecting ranges incrementally`() {
    val model = LongRangeUnion()
    model.union(LongRange.from(1L, 30L))
    assertEquals(1, model.ranges.size)
    model.union(LongRange.from(0L, 50L))
    assertEquals(1, model.ranges.size)
    model.union(LongRange.from(5L, 60L))
    assertEquals(1, model.ranges.size)
    model.union(LongRange.from(40L, 10L))
    assertEquals(1, model.ranges.size)
  }

  @Test
  fun `test divide range by excluding center part`() {
    val model = LongRangeUnion()
    model.union(LongRange.from(1L, 30L))
    model.exclude(LongRange.from(5L, 5L))
    model.exclude(LongRange.from(12L, 5L))
    assertEquals(3, model.ranges.size)
  }

  @Test
  fun `test exclude outer and inner parts from range`() {
    val model = LongRangeUnion()
    model.union(LongRange.from(1L, 100L))
    model.exclude(LongRange.from(5L, 5L))
    model.exclude(LongRange.from(12L, 5L))
    model.exclude(LongRange.from(0L, 3L))
    model.exclude(LongRange.from(90L, 20L))
    assertEquals(3, model.ranges.size)
  }

  @Test
  fun `test impose on border`() {
    val model = LongRangeUnion()
    model.union(40L..60L)
    assertEquals(30L..39L, model.impose(30L..40L))
  }

  @Test
  fun `test impose on multiple ranges`() {
    val model = createTestModel()
    assertEquals(20L..45L, model.impose(45L..95L))
  }

  @Test
  fun `test impose on containing range`() {
    val model = LongRangeUnion()
    model.union(10L..100L)
    assertTrue(model.impose(50L..60L).isEmpty())
  }

  @Test
  fun `test impose on no ranges`() {
    val model = LongRangeUnion()
    assertEquals(model.impose(40L..60L), model.impose(40L..60L))
  }

  @Test
  fun `test impose on empty model results in same range`() {
    val model = LongRangeUnion()
    val gen = Random(System.currentTimeMillis())
    for (i in 0L..99L) {
      val rand = LongRange.from(gen.nextInt() % 300L, gen.nextInt() % 300L)
      assertEquals(rand, model.impose(rand))
    }
  }

  @Test
  fun `test impose cache`() {
    val gen = Random(System.currentTimeMillis())
    val ranges = mutableListOf<LongRange>()
    for (i in 0L..99L) {
      ranges.add(LongRange.from(gen.nextInt() % 300L, gen.nextInt() % 300L))
    }
    val resultsWithClear = mutableListOf<LongRange>()
    val resultsNoClear = mutableListOf<LongRange>()
    for (x in ranges) {
      val model = createTestModel()
      resultsWithClear.add(model.impose(x))
    }
    val model = createTestModel()
    for (x in ranges) {
      resultsNoClear.add(model.impose(x))
    }
    assertEquals(resultsNoClear, resultsWithClear)
  }

  @Test
  fun `test impose single dot`() {
    with(LongRangeUnion()) {
      union(11..100L)
      assertEquals(20, impose(110..110L).start)
    }
  }
}