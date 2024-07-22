package dev.toastbits.kanakt

import dev.toastbits.kanakt.table.KanaRomajiTable

private typealias Reading = Map.Entry<String, String>

object Kana {
    fun romanise(text: String): List<Segment> {
        val segments: MutableList<Segment> = mutableListOf(UnprocessedSegment(text))
        for (reading in KanaRomajiTable.double) {
            while (attemptExtractReadings(segments, reading)) {}
        }
        for (reading in KanaRomajiTable.single) {
            while (attemptExtractReadings(segments, reading)) {}
        }

        return segments.filter { it.text.isNotEmpty() }
    }

    private fun attemptExtractReadings(segments: MutableList<Segment>, reading: Reading): Boolean {
        var i: Int = -1
        while (++i < segments.size) {
            val segment: Segment = segments[i]
            if (segment !is UnprocessedSegment) {
                continue
            }

            val reading_index: Int? = segment.text.getReadingIndex(reading)
            if (reading_index != null) {
                segments[i++] = UnprocessedSegment(segment.text.substring(0, reading_index))
                segments.add(i++, ProcessedSegment(segment.text.substring(reading_index, reading_index + reading.key.length), reading.value))
                segments.add(i, UnprocessedSegment(segment.text.substring(reading_index + reading.key.length)))
                return true
            }
        }

        return false
    }

    private fun String.getReadingIndex(reading: Reading): Int? {
        when (reading.key.length) {
            0 -> throw IllegalStateException()
            1 -> return indexOf(reading.key.first()).takeIf { it != -1 }
            2 -> {
                withIndex().zipWithNext { a, b ->
                    if (reading.key[0] == a.value && reading.key[1] == b.value) {
                        return a.index
                    }
                }
                return null
            }
            else -> throw NotImplementedError("${reading.key} (${reading.key.length})")
        }
    }

    sealed interface Segment {
        val text: String
        val reading: String?
    }

    private data class UnprocessedSegment(override val text: String): Segment {
        override val reading: String? = null
    }

    private data class ProcessedSegment(override val text: String, override val reading: String?): Segment
}
