package com.ghardari.app.data.model

enum class AppLanguage(val code: String, val displayName: String, val isRtl: Boolean) {
    SINDHI("sd", "سنڌي", true),
    URDU("ur", "اردو", true),
    ENGLISH("en", "English", false)
}

// 1. Udhar Khata (Parties / Persons)
data class PartyRecord(
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val type: String = "LENDER", // "LENDER" (we owe them / bank) or "BORROWER" (they owe us)
    val currentBalance: Double = 0.0,
    val updatedAt: String = "",
    val notes: String = ""
)

data class KhataTransaction(
    val id: Long = 0,
    val partyId: Long,
    val partyName: String,
    val type: String, // "DEBIT" (ڏنا / We gave them), "CREDIT" (ورتا / We took from them), "CLEAR" (صاف)
    val amount: Double,
    val date: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// 2. Monthly House Expenditures (Fixed monthly bills, Todo style)
data class MonthlyExpenditure(
    val id: Long = 0,
    val monthKey: String, // "YYYY-MM"
    val title: String, // e.g. "بجليءَ جو بل", "پاڻي بل", "اسڪول رڪشا", "اسڪول فيس"
    val amount: Double,
    val isPaid: Boolean = false,
    val paidDate: String = "",
    val notes: String = ""
)

// 3. Daily Expenses Log
data class DailyExpense(
    val id: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val monthKey: String, // "YYYY-MM"
    val title: String, // e.g. "رات جي ماني", "سبزي", "پاڻي جون بوتلون"
    val amount: Double,
    val paymentMethod: String = "Cash",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// 4. Monthly Ration / Grocery Checklist
data class RationItem(
    val id: Long = 0,
    val monthKey: String, // "YYYY-MM"
    val name: String,
    val company: String = "", // Brand or Company e.g. Sunsilk, Lifebuoy, Dalda
    val quantity: Double,
    val unit: String, // kg, gm, liter, bottle, packet, dozen, piece
    val estimatedPrice: Double = 0.0,
    val actualPrice: Double = 0.0,
    val isPurchased: Boolean = false,
    val notes: String = ""
)

// Overall Dashboard & Reports Summary
data class DashboardSummary(
    val monthKey: String,
    val monthlyBillsTotal: Double = 0.0,
    val monthlyBillsPaid: Double = 0.0,
    val monthlyBillsUnpaid: Double = 0.0,
    val monthlyBillsPendingCount: Int = 0,
    val dailyExpensesTotal: Double = 0.0,
    val rationTotal: Double = 0.0,
    val rationBoughtCount: Int = 0,
    val rationTotalCount: Int = 0,
    val khataWeOwe: Double = 0.0,
    val khataOwedToUs: Double = 0.0,
    val grandTotalExpense: Double = 0.0 // monthlyBillsPaid + dailyExpensesTotal + rationTotal
)
