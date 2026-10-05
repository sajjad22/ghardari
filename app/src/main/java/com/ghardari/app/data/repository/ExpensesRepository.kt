package com.ghardari.app.data.repository

import com.ghardari.app.data.db.DatabaseHelper
import com.ghardari.app.data.model.*

class ExpensesRepository(private val dbHelper: DatabaseHelper) {

    // 1. Udhar Khata
    fun getParties(): List<PartyRecord> = dbHelper.getAllParties()
    fun addParty(name: String, phone: String, type: String, initialBalance: Double, notes: String): Long =
        dbHelper.addParty(name, phone, type, initialBalance, notes)
    fun addKhataTransaction(partyId: Long, partyName: String, type: String, amount: Double, notes: String): Long =
        dbHelper.addKhataTransaction(partyId, partyName, type, amount, notes)
    fun clearPartyAccount(partyId: Long, partyName: String) =
        dbHelper.clearPartyAccount(partyId, partyName)
    fun deleteParty(partyId: Long) = dbHelper.deleteParty(partyId)
    fun getPartyTransactions(partyId: Long): List<KhataTransaction> =
        dbHelper.getPartyTransactions(partyId)

    // 2. Monthly House Expenditures (Fixed / Todo)
    fun getMonthlyExpenditures(monthKey: String): List<MonthlyExpenditure> =
        dbHelper.getMonthlyExpenditures(monthKey)
    fun addMonthlyExpenditure(monthKey: String, title: String, amount: Double, notes: String): Long =
        dbHelper.addMonthlyExpenditure(monthKey, title, amount, notes)
    fun toggleMonthlyExpenditurePaid(id: Long, isPaid: Boolean) =
        dbHelper.toggleMonthlyExpenditurePaid(id, isPaid)
    fun deleteMonthlyExpenditure(id: Long) =
        dbHelper.deleteMonthlyExpenditure(id)
    fun copyMonthlyExpendituresFromPreviousMonth(fromMonthKey: String, toMonthKey: String): Int =
        dbHelper.copyMonthlyExpendituresFromPreviousMonth(fromMonthKey, toMonthKey)

    // 3. Daily Expenses Log
    fun getDailyExpenses(monthKey: String): List<DailyExpense> =
        dbHelper.getDailyExpenses(monthKey)
    fun addDailyExpense(title: String, amount: Double, paymentMethod: String, notes: String): Long =
        dbHelper.addDailyExpense(title, amount, paymentMethod, notes)
    fun deleteDailyExpense(id: Long) =
        dbHelper.deleteDailyExpense(id)

    // 4. Monthly Ration / Grocery Checklist
    fun getRationItems(monthKey: String): List<RationItem> =
        dbHelper.getRationItems(monthKey)
    fun addRationItem(monthKey: String, name: String, qty: Double, unit: String, estPrice: Double, notes: String): Long =
        dbHelper.addRationItem(monthKey, name, qty, unit, estPrice, notes)
    fun updateRationPurchased(id: Long, isPurchased: Boolean, actualPrice: Double) =
        dbHelper.updateRationPurchased(id, isPurchased, actualPrice)
    fun deleteRationItem(id: Long) =
        dbHelper.deleteRationItem(id)

    // 5. Custom Cards
    fun getCustomCards(): List<CustomCard> = dbHelper.getCustomCards()
    fun addCustomCard(title: String, targetType: String, targetId: Long?, icon: String, color: String): Long =
        dbHelper.addCustomCard(title, targetType, targetId, icon, color)
    fun deleteCustomCard(id: Long) = dbHelper.deleteCustomCard(id)

    // Dashboard Summary
    fun getDashboardSummary(monthKey: String): DashboardSummary =
        dbHelper.getDashboardSummary(monthKey)
}
