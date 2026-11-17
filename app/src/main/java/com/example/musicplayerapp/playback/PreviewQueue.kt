package com.example.musicplayerapp.playback

import kotlin.random.Random

/** Selection for artwork-only previews. Real playback uses the Media3 queue directly. */
class PreviewQueue(private val ids: List<String>, private val random: Random = Random.Default) {
    init { require(ids.isNotEmpty() && ids.distinct().size == ids.size) }
    var currentId: String = ids.first()
        private set

    fun select(id: String): String {
        require(id in ids)
        currentId = id
        return currentId
    }
    fun next(): String = select(ids[(ids.indexOf(currentId) + 1) % ids.size])
    fun previous(): String = select(ids[(ids.indexOf(currentId) + ids.size - 1) % ids.size])
    fun randomize(): String = select(ids.filterNot { it == currentId }.let {
        if (it.isEmpty()) currentId else it.random(random)
    })
}
