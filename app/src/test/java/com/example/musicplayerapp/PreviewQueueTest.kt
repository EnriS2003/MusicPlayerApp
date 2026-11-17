package com.example.musicplayerapp

import com.example.musicplayerapp.playback.PreviewQueue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import kotlin.random.Random

class PreviewQueueTest {
    private val ids = listOf("a", "b", "c", "d")

    @Test fun nextAndPreviousWrapAtTheEnds() {
        val queue = PreviewQueue(ids)
        assertEquals("d", queue.previous())
        assertEquals("a", queue.next())
    }

    @Test fun nextAndPreviousFollowTheRandomlySelectedTrack() {
        val queue = PreviewQueue(ids, Random(7))
        repeat(100) {
            val before = queue.currentId
            val selected = queue.randomize()
            assertNotEquals(before, selected)
            val index = ids.indexOf(selected)
            assertEquals(ids[(index + 1) % ids.size], queue.next())
            assertEquals(selected, queue.previous())
        }
    }

    @Test fun selectionFromQueueUpdatesTheNavigationPosition() {
        val queue = PreviewQueue(ids)
        queue.select("c")
        assertEquals("d", queue.next())
        assertEquals("c", queue.previous())
    }

    @Test fun oneTrackCanBeShuffledAndNavigated() {
        val queue = PreviewQueue(listOf("only"))
        assertEquals("only", queue.randomize())
        assertEquals("only", queue.next())
        assertEquals("only", queue.previous())
    }

    @Test(expected = IllegalArgumentException::class)
    fun unknownIdCannotChangeSelection() { PreviewQueue(ids).select("missing") }
}
