// In com.koin.data.coin package (e.g., in its own TimeRange.kt file for clarity)
package com.koin.data.coin

enum class TimeRange(val days: Int?, val displayName: String) { // 'days' is now nullable for 'ALL' or '1H'
    // For 1 hour, you might use 0 or null and handle it specially,
    // as CoinGecko's range API typically uses days.
    // For simplicity, let's assume CoinGecko's 'from'/'to' handles sub-day ranges if given appropriately.
    // If 1H isn't directly supported by 'days' parameter, you'd need custom logic for it.
    // For now, let's represent it conceptually.

    ONE_HOUR(null, "1H"), // Null days for 1 hour, will need special handling in repo
    ONE_DAY(1, "1D"),
    ONE_WEEK(7, "1W"),
    ONE_MONTH(30, "1M"),
    ONE_YEAR(365, "1Y"),
    ALL(null, "ALL"); // Null days for 'ALL', will need special handling in repo

    // You might need a helper function to get appropriate `from` based on timeRange
    fun getStartTimeSeconds(endTimeSeconds: Long): Long {
        return when (this) {
            ONE_HOUR -> endTimeSeconds - (60 * 60) // 1 hour ago
            ONE_DAY -> endTimeSeconds - (24 * 60 * 60) // 1 day ago
            ONE_WEEK -> endTimeSeconds - (7 * 24 * 60 * 60) // 7 days ago
            ONE_MONTH -> endTimeSeconds - (30 * 24 * 60 * 60) // 30 days ago
            ONE_YEAR -> endTimeSeconds - (365 * 24 * 60 * 60) // 365 days ago
            ALL -> 0 // Or some very early date for "all time"
        }
    }
}