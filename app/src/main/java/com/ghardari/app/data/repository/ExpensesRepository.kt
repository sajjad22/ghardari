package com.ghardari.app.data.repository

import com.ghardari.app.data.db.DatabaseHelper
import com.ghardari.app.data.model.*

class ExpensesRepository(private val dbHelper: DatabaseHelper) {

    fun getCategories(): List<CategoryRecord> = dbHelper.getAllCategories()

    fun addCustomCategory(nameSd: String, nameUr: String, nameEn: String, icon: String, color: String): Long {
        return dbHelper.addCustomCategory(nameSd, nameUr, nameEn, icon, color)
    }

    fun deleteCategory(id: Long) = dbHelper.deleteCategory(id)

    fun getParties(): List<PartyRecord> = dbHelper.getAllParties()

    fun addParty(name: String, phone: String, type: String, initialBalance: Double, notes: String): Long {
        return dbHelper.addParty(name, phone, type, initialBalance, notes)
    }

    fun clearPartyAccount(partyId: Long, partyName: String) {
        dbHelper.clearPartyAccount(partyId, partyName)
    }

    fun addTransaction(
        partyId: Long?,
        partyName: String,
        categoryId: Long?,
        categoryKey: String,
        type: String,
        amount: Double,
        date: String,
        monthKey: String,
        paymentMethod: String,
        status: String = "CLEARED",
        notes: String
    ): Long {
        return dbHelper.addTransaction(
            partyId = partyId,
            partyName = partyName,
            categoryId = categoryId,
            categoryKey = categoryKey,
            type = type,
            amount = amount,
            date = date,
            monthKey = monthKey,
            paymentMethod = paymentMethod,
            status = status,
            notes = notes
        )
    }

    fun getTransactionsByMonth(monthKey: String): List<TransactionRecord> {
        return dbHelper.getTransactionsByMonth(monthKey)
    }

    fun getTransactionsByParty(partyId: Long): List<TransactionRecord> {
        return dbHelper.getTransactionsByParty(partyId)
    }

    fun deleteTransaction(id: Long) = dbHelper.deleteTransaction(id)

    fun getMonthSummary(monthKey: String): MonthSummary = dbHelper.getMonthSummary(monthKey)

    fun getRationItems(monthKey: String): List<RationItem> = dbHelper.getRationItems(monthKey)

    fun addRationItem(
        monthKey: String,
        nameSd: String,
        nameUr: String,
        nameEn: String,
        quantity: Double,
        unit: String,
        estPrice: Double,
        actualPrice: Double,
        isPurchased: Boolean,
        notes: String
    ): Long {
        return dbHelper.addRationItem(monthKey, nameSd, nameUr, nameEn, quantity, unit, estPrice, actualPrice, isPurchased, notes)
    }

    fun updateRationPurchased(id: Long, isPurchased: Boolean, actualPrice: Double) {
        dbHelper.updateRationPurchased(id, isPurchased, actualPrice)
    }

    fun deleteRationItem(id: Long) = dbHelper.deleteRationItem(id)
}
