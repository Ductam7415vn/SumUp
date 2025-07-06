package com.example.sumup.presentation.screens.main.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.sumup.domain.usecase.DocumentProcessorFactory

@Composable
fun rememberMultiFormatDocumentLauncher(
    onDocumentSelected: (uri: String) -> Unit
): androidx.activity.result.ActivityResultLauncher<Array<String>> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            onDocumentSelected(it.toString())
        }
    }
}

/**
 * Returns the list of supported MIME types for document selection
 */
fun getSupportedMimeTypes(): Array<String> {
    return arrayOf(
        "application/pdf",
        "text/plain", // TXT
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // DOCX - Now supported with Mammoth
        // DOC format still requires server-side processing or API 26+
        // "application/msword", // DOC
        // "application/rtf", // RTF
        // "text/rtf", // RTF alternative
    )
}