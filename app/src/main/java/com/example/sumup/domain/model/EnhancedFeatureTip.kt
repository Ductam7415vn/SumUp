package com.example.sumup.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
// AppState is now in TooltipModels.kt in the same package

/**
 * Enhanced feature tip model with rich content and interactions
 */
data class EnhancedFeatureTip(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector? = null,
    val category: TipCategory = TipCategory.GENERAL,
    val trigger: TooltipTrigger,
    val sections: List<TooltipSection> = emptyList(),
    val interactiveElements: List<InteractiveElement> = emptyList(),
    val showBackdrop: Boolean = false,
    val requiresAction: Boolean = false,
    val priority: Int = 0,
    val tags: List<String> = emptyList()
)

/**
 * Tooltip trigger conditions
 */
sealed class TooltipTrigger {
    object Manual : TooltipTrigger()
    data class OnAction(val action: String) : TooltipTrigger()
    data class OnCondition(val condition: (AppState) -> Boolean) : TooltipTrigger()
    data class AfterDelay(val delayMs: Long) : TooltipTrigger()
    data class OnFirstUse(val feature: String) : TooltipTrigger()
}

// AppState and UserLevel are imported from TooltipModels.kt in the same package

/**
 * Categories for organizing tips
 */
enum class TipCategory {
    BASIC,
    GENERAL,
    ADVANCED,
    PRO_TIP,
    SHORTCUT,
    BEST_PRACTICE
}

/**
 * Content sections within a tooltip
 */
sealed class TooltipSection {
    data class BulletPoints(val points: List<String>) : TooltipSection()
    data class Code(val code: String, val language: String = "kotlin") : TooltipSection()
    data class Image(val resourceId: Int, val description: String) : TooltipSection()
}

/**
 * Interactive elements within tooltips
 */
sealed class InteractiveElement {
    data class Button(
        val label: String,
        val action: String,
        val icon: ImageVector? = null,
        val style: ButtonStyle = ButtonStyle.PRIMARY
    ) : InteractiveElement()
    
    data class Checkbox(
        val label: String,
        val action: String,
        val initialValue: Boolean = false
    ) : InteractiveElement()
    
    enum class ButtonStyle {
        PRIMARY,
        SECONDARY,
        TEXT
    }
}

/**
 * Predefined enhanced feature tips
 */
object EnhancedFeatureTips {
    
    val welcomeTip = EnhancedFeatureTip(
        id = "welcome",
        title = "Welcome to SumUp! 👋",
        description = "Let's take a quick tour of the key features to help you get started.",
        icon = Icons.Default.Celebration,
        category = TipCategory.BASIC,
        trigger = TooltipTrigger.OnCondition { state ->
            state.summaryCount == 0 && state.textLength == 0
        },
        sections = listOf(
            TooltipSection.BulletPoints(
                listOf(
                    "AI-powered text summarization",
                    "Multiple input methods",
                    "Customizable summary lengths",
                    "Save and organize your summaries"
                )
            )
        ),
        interactiveElements = listOf(
            InteractiveElement.Button(
                label = "Start Tour",
                action = "start_tour",
                icon = Icons.Default.PlayArrow
            ),
            InteractiveElement.Checkbox(
                label = "Don't show tips automatically",
                action = "disable_auto_tips"
            )
        ),
        showBackdrop = true,
        priority = 100
    )
    
    val textInputTip = EnhancedFeatureTip(
        id = "text_input",
        title = "Enter Your Text",
        description = "Type or paste any text you want to summarize. Works best with 100-2000 words.",
        icon = Icons.Default.TextFields,
        category = TipCategory.BASIC,
        trigger = TooltipTrigger.OnAction("text_field_focused"),
        sections = listOf(
            TooltipSection.BulletPoints(
                listOf(
                    "Minimum: 50 characters",
                    "Maximum: 30,000 characters",
                    "Supports multiple languages"
                )
            )
        ),
        interactiveElements = listOf(
            InteractiveElement.Button(
                label = "Try Sample Text",
                action = "insert_sample_text",
                icon = Icons.Default.ContentPaste
            )
        )
    )
    
    val summarizeButtonTip = EnhancedFeatureTip(
        id = "summarize_button",
        title = "Quick Summarize",
        description = "Click here to instantly generate an AI-powered summary of your text.",
        icon = Icons.Default.AutoAwesome,
        category = TipCategory.BASIC,
        trigger = TooltipTrigger.OnCondition { state ->
            state.textLength >= 50 && state.summaryCount == 0
        },
        sections = listOf(
            TooltipSection.Code(
                code = "Tip: Use Ctrl+Enter (Cmd+Enter on Mac) for quick summarization",
                language = "text"
            )
        ),
        showBackdrop = true
    )
    
    val pdfUploadTip = EnhancedFeatureTip(
        id = "pdf_upload",
        title = "PDF Support",
        description = "Upload PDF documents for instant summarization. Perfect for research papers, reports, and long documents.",
        icon = Icons.Default.PictureAsPdf,
        category = TipCategory.GENERAL,
        trigger = TooltipTrigger.OnAction("pdf_button_clicked"),
        sections = listOf(
            TooltipSection.BulletPoints(
                listOf(
                    "Maximum file size: 50MB",
                    "Text extraction from scanned PDFs",
                    "Preserves document structure"
                )
            )
        ),
        interactiveElements = listOf(
            InteractiveElement.Button(
                label = "Choose PDF",
                action = "open_file_picker",
                icon = Icons.Default.FolderOpen
            )
        )
    )
    
    val summaryLengthTip = EnhancedFeatureTip(
        id = "summary_length",
        title = "Customize Summary Length",
        description = "Choose the perfect summary length for your needs.",
        icon = Icons.Default.Tune,
        category = TipCategory.GENERAL,
        trigger = TooltipTrigger.OnAction("length_selector_clicked"),
        sections = listOf(
            TooltipSection.BulletPoints(
                listOf(
                    "Brief: 10-20% of original",
                    "Moderate: 20-30% of original",
                    "Detailed: 30-40% of original"
                )
            )
        )
    )
    
    val ocrButtonTip = EnhancedFeatureTip(
        id = "ocr_button",
        title = "Scan Text with Camera",
        description = "Use your camera to capture and summarize printed text instantly.",
        icon = Icons.Default.CameraAlt,
        category = TipCategory.GENERAL,
        trigger = TooltipTrigger.OnAction("ocr_button_clicked"),
        sections = listOf(
            TooltipSection.BulletPoints(
                listOf(
                    "Auto-detect text in real-time",
                    "Support for multiple languages",
                    "Works with books, documents, signs"
                )
            )
        ),
        requiresAction = true
    )
    
    val advancedPersonaTip = EnhancedFeatureTip(
        id = "persona_selection",
        title = "Summary Personas",
        description = "Different AI personas provide unique summary styles tailored to your needs.",
        icon = Icons.Default.Psychology,
        category = TipCategory.ADVANCED,
        trigger = TooltipTrigger.OnCondition { state ->
            state.summaryCount >= 3
        },
        sections = listOf(
            TooltipSection.BulletPoints(
                listOf(
                    "Academic: Formal, citation-ready",
                    "Business: Professional, action-oriented",
                    "Casual: Simple, easy to understand",
                    "Technical: Preserves technical details"
                )
            )
        ),
        priority = 50
    )
    
    val keyboardShortcutsTip = EnhancedFeatureTip(
        id = "keyboard_shortcuts",
        title = "Keyboard Shortcuts",
        description = "Speed up your workflow with these handy shortcuts:",
        icon = Icons.Default.Keyboard,
        category = TipCategory.SHORTCUT,
        trigger = TooltipTrigger.OnCondition { state ->
            state.summaryCount >= 5
        },
        sections = listOf(
            TooltipSection.Code(
                """
                Ctrl/Cmd + Enter : Summarize
                Ctrl/Cmd + L     : Change length
                Ctrl/Cmd + K     : Clear text
                Ctrl/Cmd + /     : Show shortcuts
                """.trimIndent()
            )
        )
    )
    
    val batchProcessingTip = EnhancedFeatureTip(
        id = "batch_processing",
        title = "Pro Tip: Batch Processing",
        description = "Process multiple documents efficiently by queuing them up.",
        icon = Icons.Default.QueuePlayNext,
        category = TipCategory.PRO_TIP,
        trigger = TooltipTrigger.OnCondition { state ->
            state.summaryCount >= 10
        },
        sections = listOf(
            TooltipSection.BulletPoints(
                listOf(
                    "Add multiple files to queue",
                    "Process overnight for large batches",
                    "Export all summaries at once"
                )
            )
        ),
        interactiveElements = listOf(
            InteractiveElement.Button(
                label = "Learn More",
                action = "open_batch_guide"
            )
        )
    )
    
    // List of all tips for easy access
    val allTips = listOf(
        welcomeTip,
        textInputTip,
        summarizeButtonTip,
        pdfUploadTip,
        summaryLengthTip,
        ocrButtonTip,
        advancedPersonaTip,
        keyboardShortcutsTip,
        batchProcessingTip
    )
    
    // Categorized tips
    val basicTips = allTips.filter { it.category == TipCategory.BASIC }
    val generalTips = allTips.filter { it.category == TipCategory.GENERAL }
    val advancedTips = allTips.filter { it.category == TipCategory.ADVANCED }
    val proTips = allTips.filter { it.category == TipCategory.PRO_TIP }
}