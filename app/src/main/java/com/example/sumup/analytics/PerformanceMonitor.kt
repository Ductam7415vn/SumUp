package com.example.sumup.analytics

import android.util.Log
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.ktx.performance
import com.google.firebase.perf.ktx.trace
import com.google.firebase.perf.metrics.Trace
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Performance Monitoring Manager
 * Implements NFR-1: Performance monitoring and optimization
 */
@Singleton
class PerformanceMonitor @Inject constructor() {

    private val performance: FirebasePerformance by lazy {
        Firebase.performance
    }

    companion object {
        private const val TAG = "PerformanceMonitor"

        // Trace names
        const val TRACE_SUMMARIZATION = "summarization"
        const val TRACE_PDF_PROCESSING = "pdf_processing"
        const val TRACE_OCR_PROCESSING = "ocr_processing"
        const val TRACE_EXPORT = "export"
        const val TRACE_DATABASE_QUERY = "database_query"
        const val TRACE_NETWORK_REQUEST = "network_request"
        const val TRACE_APP_START = "app_start"
        const val TRACE_SCREEN_LOAD = "screen_load"

        // Metric names
        const val METRIC_WORD_COUNT = "word_count"
        const val METRIC_PAGE_COUNT = "page_count"
        const val METRIC_FILE_SIZE_KB = "file_size_kb"
        const val METRIC_ITEMS_COUNT = "items_count"

        // Attribute keys
        const val ATTR_PERSONA = "persona"
        const val ATTR_FORMAT = "format"
        const val ATTR_SOURCE = "source"
        const val ATTR_SUCCESS = "success"
        const val ATTR_ERROR_TYPE = "error_type"
    }

    /**
     * Start a performance trace
     * AC-NFR1.1: Track operation performance
     */
    fun startTrace(traceName: String): Trace {
        return performance.newTrace(traceName).apply {
            start()
            Log.d(TAG, "Started trace: $traceName")
        }
    }

    /**
     * Stop a performance trace
     */
    fun stopTrace(trace: Trace) {
        trace.stop()
        Log.d(TAG, "Stopped trace: ${trace.name}")
    }

    /**
     * Execute a block with automatic tracing
     * AC-NFR1.2: Automatic performance tracking
     */
    suspend fun <T> traced(traceName: String, block: suspend (Trace) -> T): T {
        val trace = performance.newTrace(traceName)
        trace.start()
        return try {
            withContext(Dispatchers.Default) {
                val result = block(trace)
                trace.putAttribute(ATTR_SUCCESS, "true")
                result
            }
        } catch (e: Exception) {
            trace.putAttribute(ATTR_SUCCESS, "false")
            trace.putAttribute(ATTR_ERROR_TYPE, e.javaClass.simpleName)
            throw e
        } finally {
            trace.stop()
        }
    }

    /**
     * Track summarization performance
     * AC-006.6: Summarization completes < 5s for 5000 chars
     */
    suspend fun <T> traceSummarization(
        persona: String,
        wordCount: Int,
        block: suspend (Trace) -> T
    ): T {
        return traced(TRACE_SUMMARIZATION) { trace ->
            trace.putAttribute(ATTR_PERSONA, persona)
            trace.putMetric(METRIC_WORD_COUNT, wordCount.toLong())
            val result = block(trace)
            trace.putAttribute(ATTR_SUCCESS, "true")
            result
        }
    }

    /**
     * Track PDF processing performance
     * AC-005.4: PDF processing < 10s for 50 pages
     */
    suspend fun <T> tracePdfProcessing(
        pageCount: Int,
        fileSizeKb: Long,
        block: suspend (Trace) -> T
    ): T {
        return traced(TRACE_PDF_PROCESSING) { trace ->
            trace.putMetric(METRIC_PAGE_COUNT, pageCount.toLong())
            trace.putMetric(METRIC_FILE_SIZE_KB, fileSizeKb)
            val result = block(trace)
            trace.putAttribute(ATTR_SUCCESS, "true")
            result
        }
    }

    /**
     * Track OCR processing performance
     * AC-004.4: OCR processing < 3s
     */
    suspend fun <T> traceOcrProcessing(
        block: suspend (Trace) -> T
    ): T {
        return traced(TRACE_OCR_PROCESSING) { trace ->
            val result = block(trace)
            trace.putAttribute(ATTR_SUCCESS, "true")
            result
        }
    }

    /**
     * Track export performance
     * AC-006.4: PDF generation < 3s
     */
    suspend fun <T> traceExport(
        format: String,
        wordCount: Int,
        block: suspend (Trace) -> T
    ): T {
        return traced(TRACE_EXPORT) { trace ->
            trace.putAttribute(ATTR_FORMAT, format)
            trace.putMetric(METRIC_WORD_COUNT, wordCount.toLong())
            val result = block(trace)
            trace.putAttribute(ATTR_SUCCESS, "true")
            result
        }
    }

    /**
     * Track database query performance
     */
    suspend fun <T> traceDatabaseQuery(
        operation: String,
        block: suspend (Trace) -> T
    ): T {
        return traced("$TRACE_DATABASE_QUERY-$operation") { trace ->
            block(trace)
        }
    }

    /**
     * Track network request performance
     * AC-NFR1.3: Monitor API response times
     */
    suspend fun <T> traceNetworkRequest(
        endpoint: String,
        block: suspend (Trace) -> T
    ): T {
        return traced("$TRACE_NETWORK_REQUEST-$endpoint") { trace ->
            block(trace)
        }
    }

    /**
     * Track screen loading performance
     * AC-NFR1.4: Monitor UI performance
     */
    suspend fun <T> traceScreenLoad(
        screenName: String,
        block: suspend (Trace) -> T
    ): T {
        return traced("$TRACE_SCREEN_LOAD-$screenName") { trace ->
            block(trace)
        }
    }

    /**
     * Add custom metric to a trace
     */
    fun addMetric(trace: Trace, metricName: String, value: Long) {
        trace.putMetric(metricName, value)
    }

    /**
     * Increment a metric in a trace
     */
    fun incrementMetric(trace: Trace, metricName: String, incrementBy: Long = 1) {
        trace.incrementMetric(metricName, incrementBy)
    }

    /**
     * Add custom attribute to a trace
     */
    fun addAttribute(trace: Trace, attribute: String, value: String) {
        trace.putAttribute(attribute, value)
    }

    /**
     * Enable/disable performance monitoring
     * AC-NFR6.12: User control over monitoring
     */
    fun setPerformanceCollectionEnabled(enabled: Boolean) {
        performance.isPerformanceCollectionEnabled = enabled
    }

    /**
     * Check if performance monitoring is enabled
     */
    fun isPerformanceCollectionEnabled(): Boolean {
        return performance.isPerformanceCollectionEnabled
    }
}

/**
 * Extension function for easy trace usage
 */
suspend inline fun <T> PerformanceMonitor.trace(
    traceName: String,
    crossinline block: suspend () -> T
): T {
    return traced(traceName) { block() }
}
