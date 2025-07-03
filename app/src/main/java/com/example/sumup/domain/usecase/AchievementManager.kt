package com.example.sumup.domain.usecase

import com.example.sumup.domain.model.*
import com.example.sumup.domain.repository.SummaryRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementManager @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()
    
    private val _newAchievements = MutableSharedFlow<Achievement>()
    val newAchievements: SharedFlow<Achievement> = _newAchievements.asSharedFlow()
    
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()
    
    private val _lastUsageDate = MutableStateFlow(System.currentTimeMillis())
    val lastUsageDate: StateFlow<Long> = _lastUsageDate.asStateFlow()
    
    private var totalSummaries = 0
    private var totalPdfProcessed = 0
    private var totalOcrProcessed = 0
    private var totalExports = 0
    private var totalTimeSaved = 0
    private var totalWordsProcessed = 0
    private var personasUsed = mutableSetOf<SummaryPersona>()
    
    init {
        loadAchievements()
        observeSummaries()
    }
    
    private fun loadAchievements() {
        _achievements.value = AchievementDefinitions.getAllAchievements()
    }
    
    private fun observeSummaries() {
        summaryRepository.getAllSummaries()
            .onEach { summaries ->
                updateStats(summaries)
                checkAchievements()
            }
            .launchIn(kotlinx.coroutines.GlobalScope) // Use proper scope in production
    }
    
    private fun updateStats(summaries: List<Summary>) {
        totalSummaries = summaries.size
        // TODO: Add sourceType to Summary model
        // totalPdfProcessed = summaries.count { it.sourceType == "PDF" }
        // totalOcrProcessed = summaries.count { it.sourceType == "OCR" }
        totalPdfProcessed = 0 // Temporarily set to 0
        totalOcrProcessed = 0 // Temporarily set to 0
        totalTimeSaved = summaries.sumOf { 
            it.metrics.originalReadingTime - it.metrics.summaryReadingTime 
        }
        totalWordsProcessed = summaries.sumOf { it.metrics.originalWordCount }
        
        // Update streak
        val today = System.currentTimeMillis() / (24 * 60 * 60 * 1000)
        val lastUsed = _lastUsageDate.value / (24 * 60 * 60 * 1000)
        
        if (today - lastUsed == 1L) {
            _currentStreak.value++
        } else if (today - lastUsed > 1L) {
            _currentStreak.value = 1
        }
        
        _lastUsageDate.value = System.currentTimeMillis()
    }
    
    suspend fun onPersonaUsed(persona: SummaryPersona) {
        personasUsed.add(persona)
        checkAchievements()
    }
    
    suspend fun onExport() {
        totalExports++
        checkAchievements()
    }
    
    private suspend fun checkAchievements() {
        val updatedAchievements = _achievements.value.map { achievement ->
            val progress = when (achievement.type) {
                AchievementType.FIRST_SUMMARY -> totalSummaries
                AchievementType.SPEED_READER -> totalSummaries
                AchievementType.PDF_MASTER -> totalPdfProcessed
                AchievementType.OCR_WIZARD -> totalOcrProcessed
                AchievementType.STREAK_KEEPER -> _currentStreak.value
                AchievementType.EXPORT_EXPERT -> totalExports
                AchievementType.PERSONA_EXPLORER -> personasUsed.size
                AchievementType.TIME_SAVER -> totalTimeSaved
                AchievementType.WORD_CRUNCHER -> totalWordsProcessed
                AchievementType.POWER_USER -> totalSummaries // Simplified
            }
            
            val wasUnlocked = achievement.isUnlocked
            val isNowUnlocked = progress >= achievement.requirement
            
            if (!wasUnlocked && isNowUnlocked) {
                _newAchievements.emit(
                    achievement.copy(
                        currentProgress = progress,
                        isUnlocked = true,
                        unlockedAt = System.currentTimeMillis()
                    )
                )
            }
            
            achievement.copy(
                currentProgress = progress.coerceAtMost(achievement.requirement),
                isUnlocked = isNowUnlocked,
                unlockedAt = if (isNowUnlocked && achievement.unlockedAt == null) {
                    System.currentTimeMillis()
                } else {
                    achievement.unlockedAt
                }
            )
        }
        
        _achievements.value = updatedAchievements
    }
    
    fun getUnlockedAchievements(): List<Achievement> {
        return _achievements.value.filter { it.isUnlocked }
    }
    
    fun getProgressAchievements(): List<Achievement> {
        return _achievements.value.filter { 
            !it.isUnlocked && it.currentProgress > 0 
        }
    }
    
    fun getLockedAchievements(): List<Achievement> {
        return _achievements.value.filter { 
            !it.isUnlocked && it.currentProgress == 0 
        }
    }
    
    fun getTotalPoints(): Int {
        return getUnlockedAchievements().sumOf { 
            10 * it.tier.multiplier 
        }
    }
    
    fun getNextMilestone(): Achievement? {
        return _achievements.value
            .filter { !it.isUnlocked }
            .minByOrNull { it.requirement - it.currentProgress }
    }
    
    suspend fun unlock(type: AchievementType) {
        val achievement = _achievements.value.find { it.type == type && !it.isUnlocked }
        if (achievement != null) {
            val unlocked = achievement.copy(
                isUnlocked = true,
                currentProgress = achievement.requirement,
                unlockedAt = System.currentTimeMillis()
            )
            _achievements.update { list ->
                list.map { if (it.id == achievement.id) unlocked else it }
            }
            _newAchievements.emit(unlocked)
        }
    }
}