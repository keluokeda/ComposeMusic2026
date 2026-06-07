package com.ke.music.app.ui.screen.player

data class LrcLine(
    val time: Long,
    val content: String
)

object LrcParser {
    // 匹配单个时间标签 [mm:ss.SS] 或 [mm:ss.SSS]
    private val timeRegex = Regex("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})]")

    fun parse(lrc: String?): List<LrcLine> {
        if (lrc.isNullOrBlank()) return emptyList()

        val result = mutableListOf<LrcLine>()

        lrc.lines().forEach { line ->
            // 查找这一行中所有的符合时间格式的标签
            val matches = timeRegex.findAll(line).toList()
            if (matches.isNotEmpty()) {
                // 歌词内容是最后一个时间标签之后的所有文本
                val lastMatch = matches.last()
                val content = line.substring(lastMatch.range.last + 1).trim()
                
                // 为每一个时间标签创建一个 LrcLine 实例
                matches.forEach { match ->
                    val min = match.groupValues[1].toLong()
                    val sec = match.groupValues[2].toLong()
                    val msStr = match.groupValues[3]
                    // 处理两位或三位毫秒
                    val ms = if (msStr.length == 2) msStr.toLong() * 10 else msStr.toLong()
                    
                    val time = min * 60 * 1000 + sec * 1000 + ms
                    result.add(LrcLine(time, content))
                }
            }
        }
        
        // 最后按时间排序，确保滚动逻辑正确
        return result.sortedBy { it.time }
    }
}
