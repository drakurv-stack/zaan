package com.orbit.recovery.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        val NAME_KEY = stringPreferencesKey("user_name")
        val REASONS_KEY = stringSetPreferencesKey("reasons")
        val HABIT_KEY = stringPreferencesKey("target_habit")
        val AGE_KEY = stringPreferencesKey("age_group")
        val ROUTINE_DURATION_KEY = stringPreferencesKey("routine_duration")
        val USAGE_INCREASED_KEY = stringPreferencesKey("usage_increased")
        val HABIT_CHANGES_KEY = stringPreferencesKey("habit_changes")
        val FINANCIAL_IMPACT_KEY = stringPreferencesKey("financial_impact")
        val RELIGION_KEY = stringPreferencesKey("religion")
        val ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("onboarding_complete")
        val COACH_MESSAGES_KEY = stringPreferencesKey("coach_messages")
        
        val STREAK_KEY = intPreferencesKey("streak")
        val ORBS_KEY = intPreferencesKey("orbs")
        val CHECKINS_KEY = stringPreferencesKey("checkins_json")
        val COMPLETED_LESSONS_KEY = stringSetPreferencesKey("completed_lessons")
        
        val APP_LIMITS_KEY = stringPreferencesKey("app_limits")
        val APP_OVERRIDES_KEY = stringPreferencesKey("app_overrides")
        val VPN_ENABLED_KEY = booleanPreferencesKey("vpn_enabled")
        val START_DATE_KEY = longPreferencesKey("start_date")
        val PROTECTION_PIN_KEY = stringPreferencesKey("protection_pin")
    }

    val preferencesFlow: Flow<Preferences> = dataStore.data

    suspend fun savePreferences(
        name: String,
        reasons: Set<String>,
        habit: String,
        age: String,
        duration: String,
        increased: String,
        changes: String,
        financial: String,
        religion: String
    ) {
        dataStore.edit { prefs ->
            prefs[NAME_KEY] = name
            prefs[REASONS_KEY] = reasons
            prefs[HABIT_KEY] = habit
            prefs[AGE_KEY] = age
            prefs[ROUTINE_DURATION_KEY] = duration
            prefs[USAGE_INCREASED_KEY] = increased
            prefs[HABIT_CHANGES_KEY] = changes
            prefs[FINANCIAL_IMPACT_KEY] = financial
            prefs[RELIGION_KEY] = religion
        }
    }

    suspend fun completeOnboarding() {
        dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETE_KEY] = true
        }
    }

    suspend fun saveCoachMessages(json: String) {
        dataStore.edit { prefs ->
            prefs[COACH_MESSAGES_KEY] = json
        }
    }

    suspend fun saveDailyCheckin(json: String, stayedStrong: Boolean) {
        dataStore.edit { prefs ->
            prefs[CHECKINS_KEY] = json
            
            val currentStreak = prefs[STREAK_KEY] ?: 0
            val currentOrbs = prefs[ORBS_KEY] ?: 0
            
            if (stayedStrong) {
                prefs[STREAK_KEY] = currentStreak + 1
            } else {
                prefs[STREAK_KEY] = 0
            }
            prefs[ORBS_KEY] = currentOrbs + 10
        }
    }

    suspend fun markLessonComplete(lessonId: String) {
        dataStore.edit { prefs ->
            val current = prefs[COMPLETED_LESSONS_KEY] ?: emptySet()
            prefs[COMPLETED_LESSONS_KEY] = current + lessonId
        }
    }

    suspend fun saveAppLimits(json: String) {
        dataStore.edit { prefs -> prefs[APP_LIMITS_KEY] = json }
    }

    suspend fun saveAppOverrides(json: String) {
        dataStore.edit { prefs -> prefs[APP_OVERRIDES_KEY] = json }
    }

    suspend fun setVpnEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[VPN_ENABLED_KEY] = enabled }
    }

    suspend fun saveProtectionPin(pin: String) {
        dataStore.edit { prefs -> prefs[PROTECTION_PIN_KEY] = pin }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
