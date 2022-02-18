package com.example.funlauncher.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

fun <T, S> LiveData<T?>.combineWith(other: LiveData<S?>): LiveData<Pair<T?, S?>> =
    MediatorLiveData<Pair<T?, S?>>().apply {
        addSource(this@combineWith) { value = Pair(it, other.value) }
        addSource(other) { value = Pair(this@combineWith.value, it) }
    }


fun <T> LiveData<T>.toMutableLiveData(): MutableLiveData<T> {
    val mediatorLiveData = MediatorLiveData<T>()
    mediatorLiveData.addSource(this) {
        mediatorLiveData.value = it
    }
    return mediatorLiveData
}

// for mutable list
operator fun <T> MutableLiveData<MutableList<T>>.plusAssign(item: T) {
    val value = this.value ?: mutableListOf()
    value.add(item)
    this.value = value
}

// Note: Enum name matches API value and should not be changed

enum class AppCategory {
    OTHER,
    ART_AND_DESIGN,
    AUTO_AND_VEHICLES,
    BEAUTY,
    BOOKS_AND_REFERENCE,
    BUSINESS,
    COMICS,
    COMMUNICATION,
    DATING,
    EDUCATION,
    ENTERTAINMENT,
    EVENTS,
    FINANCE,
    FOOD_AND_DRINK,
    HEALTH_AND_FITNESS,
    HOUSE_AND_HOME,
    LIBRARIES_AND_DEMO,
    LIFESTYLE,
    MAPS_AND_NAVIGATION,
    MEDICAL,
    MUSIC_AND_AUDIO,
    NEWS_AND_MAGAZINES,
    PARENTING,
    PERSONALIZATION,
    PHOTOGRAPHY,
    PRODUCTIVITY,
    SHOPPING,
    SOCIAL,
    SPORTS,
    TOOLS,
    TRAVEL_AND_LOCAL,
    VIDEO_PLAYERS,
    WEATHER,
    GAMES;

    companion object {
        private val map = values().associateBy(AppCategory::name)
        private const val CATEGORY_GAME_STRING = "GAME_" // All games start with this prefix

        fun fromCategoryName(name: String): AppCategory {
            if (name.contains(CATEGORY_GAME_STRING)) return GAMES
            return map[name.uppercase(Locale.ROOT)] ?: OTHER
        }
    }
}


