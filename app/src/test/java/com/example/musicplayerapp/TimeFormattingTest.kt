package com.example.musicplayerapp

import com.example.musicplayerapp.ui.player.formatTime
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormattingTest {
    @Test fun handlesUnknownDurationsAndHourLongAudio() {
        assertEquals("0:00", formatTime(-1))
        assertEquals("0:59", formatTime(59_999))
        assertEquals("3:05", formatTime(185_000))
        assertEquals("1:01:01", formatTime(3_661_000))
    }
}
