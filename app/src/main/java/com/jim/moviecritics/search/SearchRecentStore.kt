package com.jim.moviecritics.search

import android.content.Context
import com.jim.moviecritics.MovieApplication
import org.json.JSONArray

object SearchRecentStore {

    private const val PREFS_NAME = "search_history"
    private const val KEY_RECENT_QUERIES = "recent_queries"
    private const val MAX_RECENT_QUERIES = 10

    fun load(): List<String> {
        val rawValue = prefs().getString(KEY_RECENT_QUERIES, null).orEmpty()

        if (rawValue.isBlank()) {
            return emptyList()
        }

        return try {
            val jsonArray = JSONArray(rawValue)
            buildList {
                for (index in 0 until jsonArray.length()) {
                    add(jsonArray.getString(index))
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun save(query: String): List<String> {
        val normalizedQuery = query.trim()

        if (normalizedQuery.isBlank()) {
            return load()
        }

        val updatedQueries = buildList {
            add(normalizedQuery)
            addAll(load().filterNot { it.equals(normalizedQuery, ignoreCase = true) })
        }.take(MAX_RECENT_QUERIES)

        persist(updatedQueries)
        return updatedQueries
    }

    private fun persist(queries: List<String>) {
        val jsonArray = JSONArray()
        queries.forEach(jsonArray::put)
        prefs().edit().putString(KEY_RECENT_QUERIES, jsonArray.toString()).apply()
    }

    private fun prefs() = MovieApplication.instance
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
