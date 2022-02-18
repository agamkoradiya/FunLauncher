package com.example.funlauncher.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup


class AppCategoryService {
    companion object {
        private const val APP_URL = "https://play.google.com/store/apps/details?id="
        private const val CAT_SIZE = 9
        private const val CATEGORY_STRING = "category/"
    }

    suspend fun fetchCategory(packageName: String): AppCategory {
        val url =
            "$APP_URL$packageName&hl=en" //https://play.google.com/store/apps/details?id=com.example.app&hl=en
        val categoryRaw = parseAndExtractCategory(url) ?: return AppCategory.OTHER
        return AppCategory.fromCategoryName(categoryRaw)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun parseAndExtractCategory(url: String): String? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val text =
                    Jsoup.connect(url).get()?.select("a[itemprop=genre]") ?: return@withContext null
                val href = text.attr("abs:href")

                if (href != null && href.length > 4 && href.contains(CATEGORY_STRING)) {
                    getCategoryTypeByHref(href)
                } else {
                    null
                }
            } catch (e: Throwable) {
                null
            }
        }

    private fun getCategoryTypeByHref(href: String) =
        href.substring(href.indexOf(CATEGORY_STRING) + CAT_SIZE, href.length)
}