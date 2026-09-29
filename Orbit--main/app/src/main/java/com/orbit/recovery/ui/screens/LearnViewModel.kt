package com.orbit.recovery.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.orbit.recovery.data.UserPreferencesRepository
import com.orbit.recovery.data.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class Lesson(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val readTime: String,
    val sections: List<LessonSection>
)

data class LessonSection(
    val heading: String,
    val body: String
)

val libraryLessons = listOf(
    Lesson("1", "Understanding Urges", "Why cravings peak and fade — and the 15-minute rule", "📖", "5", listOf(
        LessonSection("The 15-Minute Wave", "Most urges feel permanent but actually peak and subside within 15 minutes. Understanding this biological fact is your first line of defense."),
        LessonSection("Ride the Wave", "Just like a wave in the ocean, an urge will swell, peak, and crash. You do not have to fight it; you just have to ride it out.")
    )),
    Lesson("2", "The Habit Loop", "Cue, routine, reward: how habits form and how to break them", "🔄", "6", listOf(
        LessonSection("Cue, Routine, Reward", "Every habit has three parts. Changing the routine while keeping the cue and reward is the key to breaking negative cycles."),
        LessonSection("Identifying Cues", "Cues are usually triggered by location, time, emotional state, other people, or an immediately preceding action.")
    )),
    Lesson("3", "Why Relapse Happens", "Shame isn't helpful. Here's what actually works.", "💛", "4", listOf(
        LessonSection("The Role of Shame", "Shame thrives in isolation and keeps the cycle going. Viewing a slip as data rather than a failure removes its power."),
        LessonSection("Data over Drama", "When a setback occurs, get curious. What was the trigger? What were you feeling? Use this information to protect your future self.")
    )),
    Lesson("4", "Breathing & Your Brain", "The vagus nerve, box breathing, and why it works", "🧠", "5", listOf(
        LessonSection("The Vagus Nerve", "Deep, slow breathing directly stimulates the vagus nerve, sending a physical 'safe' signal to your brain, instantly lowering cortisol."),
        LessonSection("Box Breathing", "Inhale for 4, hold for 4, exhale for 4, hold for 4. This pattern is used by everyone from athletes to first responders to regain control.")
    )),
    Lesson("5", "Building a New Identity", "From 'I'm quitting' to 'I am someone who...'", "🌱", "7", listOf(
        LessonSection("Identity Shift", "Focusing on who you are becoming, rather than what you are avoiding, creates lasting intrinsic motivation."),
        LessonSection("Small Votes", "Every time you choose a positive routine, you are casting a vote for the person you want to become.")
    )),
    Lesson("6", "The Power of Streaks", "Why consistency compounds, and how to restart without shame", "🔥", "4", listOf(
        LessonSection("Consistency Compounds", "A streak is a tool, not the goal. It builds neural pathways. If it breaks, the pathways don't disappear overnight."),
        LessonSection("The 2-Day Rule", "If you miss a day or slip, focus entirely on not letting it happen twice in a row. A single drop doesn't empty the bucket.")
    ))
)

class LearnViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application.dataStore)

    private val _completedLessons = MutableStateFlow<Set<String>>(emptySet())
    val completedLessons: StateFlow<Set<String>> = _completedLessons

    init {
        viewModelScope.launch {
            repository.preferencesFlow.collectLatest { prefs ->
                _completedLessons.value = prefs[UserPreferencesRepository.COMPLETED_LESSONS_KEY] ?: emptySet()
            }
        }
    }

    fun markLessonComplete(lessonId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.markLessonComplete(lessonId)
            onSuccess()
        }
    }
}
