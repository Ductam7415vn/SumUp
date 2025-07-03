package com.example.sumup.domain.model

/**
 * Represents a section of a document for smart sectioning
 */
data class DocumentSection(
    val title: String,
    val content: String,
    val summary: Summary,
    val startIndex: Int,
    val endIndex: Int
) {
    val characterCount: Int get() = content.length
    
    /**
     * Get a preview of the section (first 200 characters)
     */
    val preview: String get() = content.take(200) + if (content.length > 200) "..." else ""
}

/**
 * Represents a document that has been sectioned and summarized
 */
data class SectionedSummary(
    val sections: List<DocumentSection>,
    val overallSummary: Summary,
    val totalCharacters: Int,
    val sectionCount: Int
) {
    /**
     * Get all section summaries concatenated
     */
    val allSectionSummaries: String
        get() = sections.joinToString("\n\n") { section ->
            "${section.title}:\n${section.summary.summary}"
        }
    
    /**
     * Get all key points from all sections
     */
    val allKeyPoints: List<String>
        get() = sections.flatMap { it.summary.bulletPoints }
    
    /**
     * Check if document was actually sectioned
     */
    val wasSectioned: Boolean get() = sections.size > 1
}