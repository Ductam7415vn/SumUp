package com.example.sumup.domain.usecase

import com.example.sumup.domain.model.PdfDocument
import com.example.sumup.domain.model.PdfExtractionResult
import com.example.sumup.domain.model.PdfProcessingState
import com.example.sumup.domain.repository.PdfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

data class AdvancedPdfResult(
    val extractionResult: PdfExtractionResult,
    val structuredData: StructuredPdfData,
    val processingMetrics: ProcessingMetrics
)

data class StructuredPdfData(
    val sections: List<DocumentSection>,
    val tables: List<TableData>,
    val metadata: DocumentMetadata,
    val keyPoints: List<String>
)

data class DocumentSection(
    val title: String,
    val content: String,
    val pageNumber: Int,
    val sectionType: SectionType
)

enum class SectionType {
    HEADER, PARAGRAPH, LIST, TABLE, FOOTER, CAPTION
}

data class TableData(
    val headers: List<String>,
    val rows: List<List<String>>,
    val pageNumber: Int
)

data class DocumentMetadata(
    val documentType: DocumentType,
    val language: String,
    val readingLevel: ReadingLevel,
    val topicCategories: List<String>
)

enum class DocumentType {
    ACADEMIC_PAPER, REPORT, CONTRACT, MANUAL, ARTICLE, BOOK, OTHER
}

enum class ReadingLevel {
    ELEMENTARY, INTERMEDIATE, ADVANCED, PROFESSIONAL
}

data class ProcessingMetrics(
    val extractionTimeMs: Long,
    val analysisTimeMs: Long,
    val textQualityScore: Float,
    val structureComplexity: Float
)

@Singleton
class AdvancedPdfProcessingUseCase @Inject constructor(
    private val pdfRepository: PdfRepository,
    private val extractPdfTextUseCase: ExtractPdfTextUseCase
) {
    
    fun processAdvanced(pdfDocument: PdfDocument): Flow<AdvancedPdfResult> = flow {
        val startTime = System.currentTimeMillis()
        
        // Step 1: Basic extraction
        val extractionResult = extractPdfTextUseCase(pdfDocument).getOrThrow()
        val extractionTime = System.currentTimeMillis() - startTime
        
        // Step 2: Structure analysis
        val analysisStartTime = System.currentTimeMillis()
        val structuredData = analyzeDocumentStructure(extractionResult.extractedText)
        val analysisTime = System.currentTimeMillis() - analysisStartTime
        
        // Step 3: Calculate metrics
        val metrics = ProcessingMetrics(
            extractionTimeMs = extractionTime,
            analysisTimeMs = analysisTime,
            textQualityScore = extractionResult.confidence,
            structureComplexity = calculateComplexity(structuredData)
        )
        
        emit(AdvancedPdfResult(extractionResult, structuredData, metrics))
    }
    
    private fun analyzeDocumentStructure(text: String): StructuredPdfData {
        val sections = extractSections(text)
        val tables = extractTables(text)
        val metadata = analyzeMetadata(text, sections)
        val keyPoints = extractKeyPoints(sections)
        
        return StructuredPdfData(sections, tables, metadata, keyPoints)
    }
    
    private fun extractSections(text: String): List<DocumentSection> {
        val sections = mutableListOf<DocumentSection>()
        val lines = text.split("\n")
        var currentSection = StringBuilder()
        var currentType = SectionType.PARAGRAPH
        var pageNumber = 1
        
        for (line in lines) {
            when {
                // Page break detection
                line.contains("Page", ignoreCase = true) && line.matches(Regex(".*\\d+.*")) -> {
                    if (currentSection.isNotEmpty()) {
                        sections.add(DocumentSection(
                            title = generateSectionTitle(currentSection.toString(), currentType),
                            content = currentSection.toString().trim(),
                            pageNumber = pageNumber,
                            sectionType = currentType
                        ))
                        currentSection.clear()
                    }
                    pageNumber++
                }
                // Header detection (all caps or title case)
                line.length < 100 && line.isNotBlank() && 
                (line == line.uppercase() || line.split(" ").all { it.firstOrNull()?.isUpperCase() == true }) -> {
                    if (currentSection.isNotEmpty()) {
                        sections.add(DocumentSection(
                            title = generateSectionTitle(currentSection.toString(), currentType),
                            content = currentSection.toString().trim(),
                            pageNumber = pageNumber,
                            sectionType = currentType
                        ))
                        currentSection.clear()
                    }
                    currentType = SectionType.HEADER
                    currentSection.append(line).append("\n")
                }
                // List detection
                line.matches(Regex("^[•\\-*\\d+.]\\s+.*")) -> {
                    if (currentType != SectionType.LIST) {
                        if (currentSection.isNotEmpty()) {
                            sections.add(DocumentSection(
                                title = generateSectionTitle(currentSection.toString(), currentType),
                                content = currentSection.toString().trim(),
                                pageNumber = pageNumber,
                                sectionType = currentType
                            ))
                            currentSection.clear()
                        }
                        currentType = SectionType.LIST
                    }
                    currentSection.append(line).append("\n")
                }
                // Table detection
                line.contains("\t") || line.matches(Regex(".*\\|.*\\|.*")) -> {
                    if (currentType != SectionType.TABLE) {
                        currentType = SectionType.TABLE
                    }
                    currentSection.append(line).append("\n")
                }
                // Regular paragraph
                else -> {
                    if (currentType != SectionType.PARAGRAPH && line.isNotBlank()) {
                        if (currentSection.isNotEmpty()) {
                            sections.add(DocumentSection(
                                title = generateSectionTitle(currentSection.toString(), currentType),
                                content = currentSection.toString().trim(),
                                pageNumber = pageNumber,
                                sectionType = currentType
                            ))
                            currentSection.clear()
                        }
                        currentType = SectionType.PARAGRAPH
                    }
                    currentSection.append(line).append("\n")
                }
            }
        }
        
        // Add last section
        if (currentSection.isNotEmpty()) {
            sections.add(DocumentSection(
                title = generateSectionTitle(currentSection.toString(), currentType),
                content = currentSection.toString().trim(),
                pageNumber = pageNumber,
                sectionType = currentType
            ))
        }
        
        return sections
    }
    
    private fun generateSectionTitle(content: String, type: SectionType): String {
        val firstLine = content.lines().firstOrNull()?.trim() ?: ""
        return when (type) {
            SectionType.HEADER -> firstLine
            SectionType.LIST -> "List: ${firstLine.take(50)}..."
            SectionType.TABLE -> "Table Data"
            else -> firstLine.take(50) + if (firstLine.length > 50) "..." else ""
        }
    }
    
    private fun extractTables(text: String): List<TableData> {
        val tables = mutableListOf<TableData>()
        val lines = text.split("\n")
        var inTable = false
        var currentTable = mutableListOf<String>()
        var pageNumber = 1
        
        for (line in lines) {
            when {
                line.contains("Page", ignoreCase = true) && line.matches(Regex(".*\\d+.*")) -> pageNumber++
                line.contains("\t") || line.contains("|") -> {
                    inTable = true
                    currentTable.add(line)
                }
                inTable && line.isBlank() -> {
                    if (currentTable.isNotEmpty()) {
                        tables.add(parseTable(currentTable, pageNumber))
                        currentTable.clear()
                    }
                    inTable = false
                }
            }
        }
        
        return tables
    }
    
    private fun parseTable(tableLines: List<String>, pageNumber: Int): TableData {
        if (tableLines.isEmpty()) return TableData(emptyList(), emptyList(), pageNumber)
        
        val separator = if (tableLines.first().contains("|")) "|" else "\t"
        val headers = tableLines.first().split(separator).map { it.trim() }
        val rows = tableLines.drop(1).map { line ->
            line.split(separator).map { it.trim() }
        }
        
        return TableData(headers, rows, pageNumber)
    }
    
    private fun analyzeMetadata(text: String, sections: List<DocumentSection>): DocumentMetadata {
        val documentType = detectDocumentType(text, sections)
        val language = detectLanguage(text)
        val readingLevel = calculateReadingLevel(text)
        val topicCategories = extractTopicCategories(text)
        
        return DocumentMetadata(documentType, language, readingLevel, topicCategories)
    }
    
    private fun detectDocumentType(text: String, sections: List<DocumentSection>): DocumentType {
        val lowerText = text.lowercase()
        return when {
            lowerText.contains("abstract") && lowerText.contains("references") -> DocumentType.ACADEMIC_PAPER
            lowerText.contains("executive summary") || lowerText.contains("recommendations") -> DocumentType.REPORT
            lowerText.contains("terms and conditions") || lowerText.contains("agreement") -> DocumentType.CONTRACT
            lowerText.contains("user guide") || lowerText.contains("instructions") -> DocumentType.MANUAL
            sections.any { it.sectionType == SectionType.HEADER } && sections.size > 5 -> DocumentType.ARTICLE
            else -> DocumentType.OTHER
        }
    }
    
    private fun detectLanguage(text: String): String {
        // Simple language detection based on common words
        val englishWords = setOf("the", "and", "is", "in", "of", "to", "a")
        val words = text.lowercase().split(Regex("\\s+"))
        val englishCount = words.count { it in englishWords }
        
        return if (englishCount > words.size * 0.01) "English" else "Unknown"
    }
    
    private fun calculateReadingLevel(text: String): ReadingLevel {
        val words = text.split(Regex("\\s+"))
        val avgWordLength = words.map { it.length }.average()
        val sentences = text.split(Regex("[.!?]+"))
        val avgSentenceLength = if (sentences.isNotEmpty()) words.size.toDouble() / sentences.size else 0.0
        
        return when {
            avgWordLength < 4 && avgSentenceLength < 15 -> ReadingLevel.ELEMENTARY
            avgWordLength < 5 && avgSentenceLength < 20 -> ReadingLevel.INTERMEDIATE
            avgWordLength < 6 && avgSentenceLength < 25 -> ReadingLevel.ADVANCED
            else -> ReadingLevel.PROFESSIONAL
        }
    }
    
    private fun extractTopicCategories(text: String): List<String> {
        val categories = mutableListOf<String>()
        val lowerText = text.lowercase()
        
        // Simple keyword-based categorization
        if (lowerText.contains("technology") || lowerText.contains("software")) categories.add("Technology")
        if (lowerText.contains("business") || lowerText.contains("management")) categories.add("Business")
        if (lowerText.contains("science") || lowerText.contains("research")) categories.add("Science")
        if (lowerText.contains("education") || lowerText.contains("learning")) categories.add("Education")
        if (lowerText.contains("health") || lowerText.contains("medical")) categories.add("Health")
        
        return categories.ifEmpty { listOf("General") }
    }
    
    private fun extractKeyPoints(sections: List<DocumentSection>): List<String> {
        return sections
            .filter { it.sectionType == SectionType.LIST || it.sectionType == SectionType.HEADER }
            .map { it.content.lines().first().take(100) }
            .take(5)
    }
    
    private fun calculateComplexity(data: StructuredPdfData): Float {
        val sectionScore = (data.sections.size / 10f).coerceIn(0f, 1f)
        val tableScore = (data.tables.size / 3f).coerceIn(0f, 1f)
        val typeScore = when (data.metadata.documentType) {
            DocumentType.ACADEMIC_PAPER, DocumentType.CONTRACT -> 0.9f
            DocumentType.REPORT, DocumentType.MANUAL -> 0.7f
            else -> 0.5f
        }
        
        return (sectionScore + tableScore + typeScore) / 3f
    }
}