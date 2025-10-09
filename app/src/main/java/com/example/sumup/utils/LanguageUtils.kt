package com.example.sumup.utils

/**
 * Utility functions for language detection and processing
 */
object LanguageUtils {

    /**
     * Detects the language of the given text and returns appropriate reading speed in WPM
     *
     * Reading speeds vary by language:
     * - English: ~200-250 WPM (using 225)
     * - Vietnamese: ~145-175 WPM (using 160)
     * - Chinese/Japanese/Korean: ~200-250 characters per minute (~150 WPM equivalent)
     * - Spanish/French/German: ~180-220 WPM (using 200)
     * - Arabic: ~120-150 WPM (using 135)
     *
     * @param text The text to analyze
     * @return Words per minute reading speed for detected language
     */
    fun getReadingSpeed(text: String): Int {
        val language = detectLanguage(text)
        return when (language) {
            Language.ENGLISH -> 225
            Language.VIETNAMESE -> 160
            Language.CHINESE, Language.JAPANESE, Language.KOREAN -> 150
            Language.SPANISH, Language.FRENCH, Language.GERMAN -> 200
            Language.ARABIC -> 135
            Language.UNKNOWN -> 200 // Default fallback
        }
    }

    /**
     * Detects the primary language of a text based on character patterns
     *
     * @param text The text to analyze
     * @return Detected language
     */
    fun detectLanguage(text: String): Language {
        if (text.isBlank()) return Language.UNKNOWN

        // Sample first 500 characters for performance
        val sample = text.take(500)

        val chineseChars = sample.count { it in '\u4E00'..'\u9FFF' }
        val japaneseChars = sample.count { it in '\u3040'..'\u309F' || it in '\u30A0'..'\u30FF' }
        val koreanChars = sample.count { it in '\uAC00'..'\uD7AF' }
        val arabicChars = sample.count { it in '\u0600'..'\u06FF' }
        val vietnameseChars = sample.count {
            it in "ăâđêôơưĂÂĐÊÔƠƯáàảãạắằẳẵặấầẩẫậéèẻẽẹếềểễệíìỉĩịóòỏõọốồổỗộớờởỡợúùủũụứừửữựýỳỷỹỵ"
        }

        val totalChars = sample.length

        // If >30% of characters are from a specific script, consider it that language
        return when {
            chineseChars > totalChars * 0.3 -> Language.CHINESE
            japaneseChars > totalChars * 0.2 -> Language.JAPANESE
            koreanChars > totalChars * 0.3 -> Language.KOREAN
            arabicChars > totalChars * 0.3 -> Language.ARABIC
            vietnameseChars > totalChars * 0.1 -> Language.VIETNAMESE
            // Detect Latin-based languages by common words
            sample.contains(Regex("\\b(the|and|is|in|to|of|a|for|on|with)\\b", RegexOption.IGNORE_CASE)) -> Language.ENGLISH
            sample.contains(Regex("\\b(el|la|de|que|es|en|un|por|con)\\b", RegexOption.IGNORE_CASE)) -> Language.SPANISH
            sample.contains(Regex("\\b(le|la|de|et|un|dans|est|pour|des)\\b", RegexOption.IGNORE_CASE)) -> Language.FRENCH
            sample.contains(Regex("\\b(der|die|das|und|in|von|ist|zu|den)\\b", RegexOption.IGNORE_CASE)) -> Language.GERMAN
            else -> Language.ENGLISH // Default to English for unknown Latin scripts
        }
    }

    /**
     * Calculates reading time in minutes based on word count and detected language
     *
     * @param wordCount Number of words in the text
     * @param text The actual text (for language detection)
     * @return Reading time in minutes (minimum 1 minute)
     */
    fun calculateReadingTime(wordCount: Int, text: String): Int {
        val wpm = getReadingSpeed(text)
        return (wordCount.toFloat() / wpm).toInt().coerceAtLeast(1)
    }
}

/**
 * Supported languages for reading speed calculation
 */
enum class Language {
    ENGLISH,
    VIETNAMESE,
    CHINESE,
    JAPANESE,
    KOREAN,
    SPANISH,
    FRENCH,
    GERMAN,
    ARABIC,
    UNKNOWN
}
