package com.ghardari.app.data.model

enum class AppLanguage(val code: String, val displayName: String, val isRtl: Boolean) {
    SINDHI("sd", "سنڌي", true),
    URDU("ur", "اردو", true),
    ENGLISH("en", "English", false)
}

data class CategoryRecord(
    val id: Long = 0,
    val key: String,
    val nameSd: String,
    val nameUr: String,
    val nameEn: String,
    val icon: String = "ShoppingBag",
    val color: String = "#0F766E",
    val isCustom: Boolean = false
) {
    fun localizedName(lang: AppLanguage): String = when (lang) {
        AppLanguage.SINDHI -> nameSd.ifBlank { nameEn }
        AppLanguage.URDU -> nameUr.ifBlank { nameEn }
        AppLanguage.ENGLISH -> nameEn.ifBlank { nameSd }
    }
}

data class PartyRecord(
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val type: String = "LENDER", // "LENDER" (we owe them / Miss XYZ) or "BORROWER" (they owe us / loan given)
    val currentBalance: Double = 0.0,
    val updatedAt: String = "",
    val notes: String = ""
)

data class TransactionRecord(
    val id: Long = 0,
    val partyId: Long? = null,
    val partyName: String = "",
    val categoryId: Long? = null,
    val categoryKey: String = "",
    val type: String, // INFLOW, OUTFLOW, KHATA_GIVE, KHATA_RECEIVE, KHATA_CLEAR
    val amount: Double,
    val date: String,
    val monthKey: String,
    val paymentMethod: String = "Cash", // Cash, Easypaisa, Bank, JazzCash
    val status: String = "CLEARED", // PENDING, CLEARED
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class RationItem(
    val id: Long = 0,
    val monthKey: String,
    val nameSd: String,
    val nameUr: String,
    val nameEn: String,
    val quantity: Double,
    val unit: String, // kg, gm, liter, ml, bottle, packet, dozen, piece
    val estimatedPrice: Double = 0.0,
    val actualPrice: Double = 0.0,
    val isPurchased: Boolean = false,
    val notes: String = ""
) {
    fun localizedName(lang: AppLanguage): String = when (lang) {
        AppLanguage.SINDHI -> nameSd.ifBlank { nameEn }
        AppLanguage.URDU -> nameUr.ifBlank { nameEn }
        AppLanguage.ENGLISH -> nameEn.ifBlank { nameSd }
    }
}

data class MonthSummary(
    val monthKey: String,
    val totalInflow: Double = 0.0,
    val totalOutflow: Double = 0.0,
    val netBalance: Double = 0.0,
    val khataOwedToUs: Double = 0.0, // People owe us
    val khataWeOwe: Double = 0.0,     // We owe people (Miss XYZ, etc.)
    val rationTotal: Double = 0.0,
    val rationBoughtCount: Int = 0,
    val rationTotalCount: Int = 0
)
