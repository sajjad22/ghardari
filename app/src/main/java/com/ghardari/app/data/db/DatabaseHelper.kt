package com.ghardari.app.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ghardari.app.data.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "ghardari_v3.db"
        const val DATABASE_VERSION = 1

        // Table Parties (Udhar Khata)
        const val TABLE_PARTIES = "parties"
        const val COL_PARTY_ID = "id"
        const val COL_PARTY_NAME = "name"
        const val COL_PARTY_PHONE = "phone"
        const val COL_PARTY_TYPE = "type" // "LENDER" or "BORROWER"
        const val COL_PARTY_BALANCE = "current_balance"
        const val COL_PARTY_UPDATED_AT = "updated_at"
        const val COL_PARTY_NOTES = "notes"

        // Table Khata Transactions
        const val TABLE_KHATA_TRX = "khata_transactions"
        const val COL_KT_ID = "id"
        const val COL_KT_PARTY_ID = "party_id"
        const val COL_KT_PARTY_NAME = "party_name"
        const val COL_KT_TYPE = "type" // "DEBIT", "CREDIT", "CLEAR"
        const val COL_KT_AMOUNT = "amount"
        const val COL_KT_DATE = "date"
        const val COL_KT_NOTES = "notes"
        const val COL_KT_CREATED_AT = "created_at"

        // Table Monthly Expenditures (Fixed / Salaried Todo)
        const val TABLE_MONTHLY_EXP = "monthly_expenditures"
        const val COL_ME_ID = "id"
        const val COL_ME_MONTH_KEY = "month_key"
        const val COL_ME_TITLE = "title"
        const val COL_ME_AMOUNT = "amount"
        const val COL_ME_IS_PAID = "is_paid"
        const val COL_ME_PAID_DATE = "paid_date"
        const val COL_ME_NOTES = "notes"

        // Table Daily Expenses Log
        const val TABLE_DAILY_EXP = "daily_expenses"
        const val COL_DE_ID = "id"
        const val COL_DE_DATE = "date"
        const val COL_DE_MONTH_KEY = "month_key"
        const val COL_DE_TITLE = "title"
        const val COL_DE_AMOUNT = "amount"
        const val COL_DE_PAY_METHOD = "payment_method"
        const val COL_DE_NOTES = "notes"
        const val COL_DE_CREATED_AT = "created_at"

        // Table Ration Checklist
        const val TABLE_RATION = "ration_items"
        const val COL_RAT_ID = "id"
        const val COL_RAT_MONTH_KEY = "month_key"
        const val COL_RAT_NAME = "name"
        const val COL_RAT_COMPANY = "company"
        const val COL_RAT_QTY = "quantity"
        const val COL_RAT_UNIT = "unit"
        const val COL_RAT_EST_PRICE = "est_price"
        const val COL_RAT_ACT_PRICE = "act_price"
        const val COL_RAT_PURCHASED = "is_purchased"
        const val COL_RAT_NOTES = "notes"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_PARTIES (
                $COL_PARTY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PARTY_NAME TEXT NOT NULL,
                $COL_PARTY_PHONE TEXT DEFAULT '',
                $COL_PARTY_TYPE TEXT NOT NULL,
                $COL_PARTY_BALANCE REAL DEFAULT 0.0,
                $COL_PARTY_UPDATED_AT TEXT NOT NULL,
                $COL_PARTY_NOTES TEXT DEFAULT ''
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_KHATA_TRX (
                $COL_KT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_KT_PARTY_ID INTEGER NOT NULL,
                $COL_KT_PARTY_NAME TEXT NOT NULL,
                $COL_KT_TYPE TEXT NOT NULL,
                $COL_KT_AMOUNT REAL NOT NULL,
                $COL_KT_DATE TEXT NOT NULL,
                $COL_KT_NOTES TEXT DEFAULT '',
                $COL_KT_CREATED_AT INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_MONTHLY_EXP (
                $COL_ME_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ME_MONTH_KEY TEXT NOT NULL,
                $COL_ME_TITLE TEXT NOT NULL,
                $COL_ME_AMOUNT REAL NOT NULL,
                $COL_ME_IS_PAID INTEGER DEFAULT 0,
                $COL_ME_PAID_DATE TEXT DEFAULT '',
                $COL_ME_NOTES TEXT DEFAULT ''
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_DAILY_EXP (
                $COL_DE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DE_DATE TEXT NOT NULL,
                $COL_DE_MONTH_KEY TEXT NOT NULL,
                $COL_DE_TITLE TEXT NOT NULL,
                $COL_DE_AMOUNT REAL NOT NULL,
                $COL_DE_PAY_METHOD TEXT DEFAULT 'Cash',
                $COL_DE_NOTES TEXT DEFAULT '',
                $COL_DE_CREATED_AT INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_RATION (
                $COL_RAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_RAT_MONTH_KEY TEXT NOT NULL,
                $COL_RAT_NAME TEXT NOT NULL,
                $COL_RAT_COMPANY TEXT DEFAULT '',
                $COL_RAT_QTY REAL NOT NULL,
                $COL_RAT_UNIT TEXT NOT NULL,
                $COL_RAT_EST_PRICE REAL DEFAULT 0.0,
                $COL_RAT_ACT_PRICE REAL DEFAULT 0.0,
                $COL_RAT_PURCHASED INTEGER DEFAULT 0,
                $COL_RAT_NOTES TEXT DEFAULT ''
            )
            """.trimIndent()
        )

        seedInitialCleanData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    private fun seedInitialCleanData(db: SQLiteDatabase) {
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

        // 1. In Udhar Khata: add "My Bank / منهنجي بئنڪ" by default
        val bankCv = ContentValues().apply {
            put(COL_PARTY_NAME, "منهنجي بئنڪ (My Bank)")
            put(COL_PARTY_PHONE, "")
            put(COL_PARTY_TYPE, "LENDER")
            put(COL_PARTY_BALANCE, 0.0)
            put(COL_PARTY_UPDATED_AT, now)
            put(COL_PARTY_NOTES, "بئنڪ اڪائونٽ کاتو")
        }
        db.insert(TABLE_PARTIES, null, bankCv)

        // 2. In Ration: Only Flour and Sugar as initial samples
        val defaultRation = listOf(
            Triple("اٽو (Flour)", 10.0, "kg"),
            Triple("کنڊ (Sugar)", 3.0, "kg")
        )
        for (item in defaultRation) {
            val ratCv = ContentValues().apply {
                put(COL_RAT_MONTH_KEY, currentMonth)
                put(COL_RAT_NAME, item.first)
                put(COL_RAT_COMPANY, "")
                put(COL_RAT_QTY, item.second)
                put(COL_RAT_UNIT, item.third)
                put(COL_RAT_EST_PRICE, 0.0)
                put(COL_RAT_ACT_PRICE, 0.0)
                put(COL_RAT_PURCHASED, 0)
                put(COL_RAT_NOTES, "")
            }
            db.insert(TABLE_RATION, null, ratCv)
        }
    }

    // ==========================================
    // Safe Cursor Helpers (Guaranteed never null)
    // ==========================================
    private fun Cursor.getSafeString(colName: String, default: String = ""): String {
        val idx = getColumnIndex(colName)
        return if (idx >= 0 && !isNull(idx)) getString(idx) ?: default else default
    }

    private fun Cursor.getSafeDouble(colName: String, default: Double = 0.0): Double {
        val idx = getColumnIndex(colName)
        return if (idx >= 0 && !isNull(idx)) getDouble(idx) else default
    }

    private fun Cursor.getSafeLong(colName: String, default: Long = 0L): Long {
        val idx = getColumnIndex(colName)
        return if (idx >= 0 && !isNull(idx)) getLong(idx) else default
    }

    private fun Cursor.getSafeInt(colName: String, default: Int = 0): Int {
        val idx = getColumnIndex(colName)
        return if (idx >= 0 && !isNull(idx)) getInt(idx) else default
    }

    // ==========================================
    // 1. Udhar Khata
    // ==========================================
    fun getAllParties(): List<PartyRecord> {
        val list = mutableListOf<PartyRecord>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PARTIES ORDER BY ABS($COL_PARTY_BALANCE) DESC, $COL_PARTY_NAME ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    PartyRecord(
                        id = it.getSafeLong(COL_PARTY_ID),
                        name = it.getSafeString(COL_PARTY_NAME).ifBlank { "Party" },
                        phone = it.getSafeString(COL_PARTY_PHONE),
                        type = it.getSafeString(COL_PARTY_TYPE, "LENDER"),
                        currentBalance = it.getSafeDouble(COL_PARTY_BALANCE),
                        updatedAt = it.getSafeString(COL_PARTY_UPDATED_AT),
                        notes = it.getSafeString(COL_PARTY_NOTES)
                    )
                )
            }
        }
        return list
    }

    fun addParty(name: String, phone: String, type: String, initialBalance: Double, notes: String): Long {
        val db = writableDatabase
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val cv = ContentValues().apply {
            put(COL_PARTY_NAME, name)
            put(COL_PARTY_PHONE, phone)
            put(COL_PARTY_TYPE, type)
            put(COL_PARTY_BALANCE, initialBalance)
            put(COL_PARTY_UPDATED_AT, now)
            put(COL_PARTY_NOTES, notes)
        }
        val pId = db.insert(TABLE_PARTIES, null, cv)
        if (initialBalance > 0) {
            val trxType = if (type == "LENDER") "CREDIT" else "DEBIT"
            addKhataTransaction(pId, name, trxType, initialBalance, "شروعاتي بيلنس")
        }
        return pId
    }

    fun addKhataTransaction(partyId: Long, partyName: String, type: String, amount: Double, notes: String): Long {
        val db = writableDatabase
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val cv = ContentValues().apply {
            put(COL_KT_PARTY_ID, partyId)
            put(COL_KT_PARTY_NAME, partyName)
            put(COL_KT_TYPE, type)
            put(COL_KT_AMOUNT, amount)
            put(COL_KT_DATE, today)
            put(COL_KT_NOTES, notes)
            put(COL_KT_CREATED_AT, System.currentTimeMillis())
        }
        val trxId = db.insert(TABLE_KHATA_TRX, null, cv)
        recalculatePartyBalance(partyId)
        return trxId
    }

    fun clearPartyAccount(partyId: Long, partyName: String): Long {
        val db = writableDatabase
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val cv = ContentValues().apply {
            put(COL_KT_PARTY_ID, partyId)
            put(COL_KT_PARTY_NAME, partyName)
            put(COL_KT_TYPE, "CLEAR")
            put(COL_KT_AMOUNT, 0.0)
            put(COL_KT_DATE, today)
            put(COL_KT_NOTES, "حساب صاف ٿيو (Settled)")
            put(COL_KT_CREATED_AT, System.currentTimeMillis())
        }
        val trxId = db.insert(TABLE_KHATA_TRX, null, cv)
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val partyCv = ContentValues().apply {
            put(COL_PARTY_BALANCE, 0.0)
            put(COL_PARTY_UPDATED_AT, now)
        }
        db.update(TABLE_PARTIES, partyCv, "$COL_PARTY_ID = ?", arrayOf(partyId.toString()))
        return trxId
    }

    fun deleteParty(partyId: Long) {
        val db = writableDatabase
        db.delete(TABLE_PARTIES, "$COL_PARTY_ID = ?", arrayOf(partyId.toString()))
        db.delete(TABLE_KHATA_TRX, "$COL_KT_PARTY_ID = ?", arrayOf(partyId.toString()))
    }

    fun getPartyTransactions(partyId: Long): List<KhataTransaction> {
        val list = mutableListOf<KhataTransaction>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_KHATA_TRX WHERE $COL_KT_PARTY_ID = ? ORDER BY $COL_KT_CREATED_AT DESC",
            arrayOf(partyId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    KhataTransaction(
                        id = it.getSafeLong(COL_KT_ID),
                        partyId = it.getSafeLong(COL_KT_PARTY_ID),
                        partyName = it.getSafeString(COL_KT_PARTY_NAME),
                        type = it.getSafeString(COL_KT_TYPE, "DEBIT"),
                        amount = it.getSafeDouble(COL_KT_AMOUNT),
                        date = it.getSafeString(COL_KT_DATE),
                        notes = it.getSafeString(COL_KT_NOTES),
                        createdAt = it.getSafeLong(COL_KT_CREATED_AT)
                    )
                )
            }
        }
        return list
    }

    fun getAllKhataTransactions(): List<KhataTransaction> {
        val list = mutableListOf<KhataTransaction>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_KHATA_TRX ORDER BY $COL_KT_ID ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    KhataTransaction(
                        id = it.getSafeLong(COL_KT_ID),
                        partyId = it.getSafeLong(COL_KT_PARTY_ID),
                        partyName = it.getSafeString(COL_KT_PARTY_NAME),
                        type = it.getSafeString(COL_KT_TYPE, "DEBIT"),
                        amount = it.getSafeDouble(COL_KT_AMOUNT),
                        date = it.getSafeString(COL_KT_DATE),
                        notes = it.getSafeString(COL_KT_NOTES),
                        createdAt = it.getSafeLong(COL_KT_CREATED_AT)
                    )
                )
            }
        }
        return list
    }

    private fun recalculatePartyBalance(partyId: Long) {
        val db = writableDatabase
        var balance = 0.0
        val cursor = db.rawQuery(
            "SELECT $COL_KT_TYPE, $COL_KT_AMOUNT FROM $TABLE_KHATA_TRX WHERE $COL_KT_PARTY_ID = ?",
            arrayOf(partyId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                val type = if (!it.isNull(0)) it.getString(0) ?: "DEBIT" else "DEBIT"
                val amount = if (!it.isNull(1)) it.getDouble(1) else 0.0
                when (type) {
                    "DEBIT" -> balance += amount
                    "CREDIT" -> balance -= amount
                    "CLEAR" -> balance = 0.0
                }
            }
        }
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val cv = ContentValues().apply {
            put(COL_PARTY_BALANCE, balance)
            put(COL_PARTY_UPDATED_AT, now)
        }
        db.update(TABLE_PARTIES, cv, "$COL_PARTY_ID = ?", arrayOf(partyId.toString()))
    }

    // ==========================================
    // 2. Monthly House Expenditures (Fixed / Todo)
    // ==========================================
    fun getMonthlyExpenditures(monthKey: String): List<MonthlyExpenditure> {
        val list = mutableListOf<MonthlyExpenditure>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_MONTHLY_EXP WHERE $COL_ME_MONTH_KEY = ? ORDER BY $COL_ME_IS_PAID ASC, $COL_ME_ID ASC",
            arrayOf(monthKey)
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    MonthlyExpenditure(
                        id = it.getSafeLong(COL_ME_ID),
                        monthKey = it.getSafeString(COL_ME_MONTH_KEY),
                        title = it.getSafeString(COL_ME_TITLE),
                        amount = it.getSafeDouble(COL_ME_AMOUNT),
                        isPaid = it.getSafeInt(COL_ME_IS_PAID) == 1,
                        paidDate = it.getSafeString(COL_ME_PAID_DATE),
                        notes = it.getSafeString(COL_ME_NOTES)
                    )
                )
            }
        }
        return list
    }

    fun getAllMonthlyExpenditures(): List<MonthlyExpenditure> {
        val list = mutableListOf<MonthlyExpenditure>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MONTHLY_EXP ORDER BY $COL_ME_MONTH_KEY DESC, $COL_ME_ID ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    MonthlyExpenditure(
                        id = it.getSafeLong(COL_ME_ID),
                        monthKey = it.getSafeString(COL_ME_MONTH_KEY),
                        title = it.getSafeString(COL_ME_TITLE),
                        amount = it.getSafeDouble(COL_ME_AMOUNT),
                        isPaid = it.getSafeInt(COL_ME_IS_PAID) == 1,
                        paidDate = it.getSafeString(COL_ME_PAID_DATE),
                        notes = it.getSafeString(COL_ME_NOTES)
                    )
                )
            }
        }
        return list
    }

    fun addMonthlyExpenditure(monthKey: String, title: String, amount: Double, notes: String): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ME_MONTH_KEY, monthKey)
            put(COL_ME_TITLE, title)
            put(COL_ME_AMOUNT, amount)
            put(COL_ME_IS_PAID, 0)
            put(COL_ME_PAID_DATE, "")
            put(COL_ME_NOTES, notes)
        }
        return db.insert(TABLE_MONTHLY_EXP, null, cv)
    }

    fun updateMonthlyExpenditure(id: Long, title: String, amount: Double, notes: String) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ME_TITLE, title)
            put(COL_ME_AMOUNT, amount)
            put(COL_ME_NOTES, notes)
        }
        db.update(TABLE_MONTHLY_EXP, cv, "$COL_ME_ID = ?", arrayOf(id.toString()))
    }

    fun toggleMonthlyExpenditurePaid(id: Long, isPaid: Boolean) {
        val db = writableDatabase
        val now = if (isPaid) SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) else ""
        val cv = ContentValues().apply {
            put(COL_ME_IS_PAID, if (isPaid) 1 else 0)
            put(COL_ME_PAID_DATE, now)
        }
        db.update(TABLE_MONTHLY_EXP, cv, "$COL_ME_ID = ?", arrayOf(id.toString()))
    }

    fun deleteMonthlyExpenditure(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_MONTHLY_EXP, "$COL_ME_ID = ?", arrayOf(id.toString()))
    }

    fun copyMonthlyExpendituresFromPreviousMonth(fromMonthKey: String, toMonthKey: String): Int {
        val prevItems = getMonthlyExpenditures(fromMonthKey)
        if (prevItems.isEmpty()) return 0

        val db = writableDatabase
        var copiedCount = 0
        for (item in prevItems) {
            val cv = ContentValues().apply {
                put(COL_ME_MONTH_KEY, toMonthKey)
                put(COL_ME_TITLE, item.title)
                put(COL_ME_AMOUNT, item.amount)
                put(COL_ME_IS_PAID, 0) // Freshly marked as UNPAID for new month!
                put(COL_ME_PAID_DATE, "")
                put(COL_ME_NOTES, item.notes)
            }
            db.insert(TABLE_MONTHLY_EXP, null, cv)
            copiedCount++
        }
        return copiedCount
    }

    // ==========================================
    // 3. Daily Expenses Log
    // ==========================================
    fun getDailyExpenses(monthKey: String): List<DailyExpense> {
        val list = mutableListOf<DailyExpense>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_DAILY_EXP WHERE $COL_DE_MONTH_KEY = ? ORDER BY $COL_DE_DATE DESC, $COL_DE_CREATED_AT DESC",
            arrayOf(monthKey)
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    DailyExpense(
                        id = it.getSafeLong(COL_DE_ID),
                        date = it.getSafeString(COL_DE_DATE),
                        monthKey = it.getSafeString(COL_DE_MONTH_KEY),
                        title = it.getSafeString(COL_DE_TITLE),
                        amount = it.getSafeDouble(COL_DE_AMOUNT),
                        paymentMethod = it.getSafeString(COL_DE_PAY_METHOD, "Cash"),
                        notes = it.getSafeString(COL_DE_NOTES),
                        createdAt = it.getSafeLong(COL_DE_CREATED_AT)
                    )
                )
            }
        }
        return list
    }

    fun getAllDailyExpenses(): List<DailyExpense> {
        val list = mutableListOf<DailyExpense>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_DAILY_EXP ORDER BY $COL_DE_DATE DESC, $COL_DE_ID DESC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    DailyExpense(
                        id = it.getSafeLong(COL_DE_ID),
                        date = it.getSafeString(COL_DE_DATE),
                        monthKey = it.getSafeString(COL_DE_MONTH_KEY),
                        title = it.getSafeString(COL_DE_TITLE),
                        amount = it.getSafeDouble(COL_DE_AMOUNT),
                        paymentMethod = it.getSafeString(COL_DE_PAY_METHOD, "Cash"),
                        notes = it.getSafeString(COL_DE_NOTES),
                        createdAt = it.getSafeLong(COL_DE_CREATED_AT)
                    )
                )
            }
        }
        return list
    }

    fun addDailyExpense(title: String, amount: Double, paymentMethod: String, notes: String): Long {
        val db = writableDatabase
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val monthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        val cv = ContentValues().apply {
            put(COL_DE_DATE, today)
            put(COL_DE_MONTH_KEY, monthKey)
            put(COL_DE_TITLE, title)
            put(COL_DE_AMOUNT, amount)
            put(COL_DE_PAY_METHOD, paymentMethod)
            put(COL_DE_NOTES, notes)
            put(COL_DE_CREATED_AT, System.currentTimeMillis())
        }
        return db.insert(TABLE_DAILY_EXP, null, cv)
    }

    fun deleteDailyExpense(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_DAILY_EXP, "$COL_DE_ID = ?", arrayOf(id.toString()))
    }

    // ==========================================
    // 4. Monthly Ration / Grocery Checklist
    // ==========================================
    fun getRationItems(monthKey: String): List<RationItem> {
        val list = mutableListOf<RationItem>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_RATION WHERE $COL_RAT_MONTH_KEY = ? ORDER BY $COL_RAT_PURCHASED ASC, $COL_RAT_ID ASC",
            arrayOf(monthKey)
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    RationItem(
                        id = it.getSafeLong(COL_RAT_ID),
                        monthKey = it.getSafeString(COL_RAT_MONTH_KEY),
                        name = it.getSafeString(COL_RAT_NAME),
                        company = it.getSafeString(COL_RAT_COMPANY),
                        quantity = it.getSafeDouble(COL_RAT_QTY),
                        unit = it.getSafeString(COL_RAT_UNIT, "piece"),
                        estimatedPrice = it.getSafeDouble(COL_RAT_EST_PRICE),
                        actualPrice = it.getSafeDouble(COL_RAT_ACT_PRICE),
                        isPurchased = it.getSafeInt(COL_RAT_PURCHASED) == 1,
                        notes = it.getSafeString(COL_RAT_NOTES)
                    )
                )
            }
        }
        return list
    }

    fun getAllRationItems(): List<RationItem> {
        val list = mutableListOf<RationItem>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_RATION ORDER BY $COL_RAT_MONTH_KEY DESC, $COL_RAT_ID ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    RationItem(
                        id = it.getSafeLong(COL_RAT_ID),
                        monthKey = it.getSafeString(COL_RAT_MONTH_KEY),
                        name = it.getSafeString(COL_RAT_NAME),
                        company = it.getSafeString(COL_RAT_COMPANY),
                        quantity = it.getSafeDouble(COL_RAT_QTY),
                        unit = it.getSafeString(COL_RAT_UNIT, "piece"),
                        estimatedPrice = it.getSafeDouble(COL_RAT_EST_PRICE),
                        actualPrice = it.getSafeDouble(COL_RAT_ACT_PRICE),
                        isPurchased = it.getSafeInt(COL_RAT_PURCHASED) == 1,
                        notes = it.getSafeString(COL_RAT_NOTES)
                    )
                )
            }
        }
        return list
    }

    fun addRationItem(monthKey: String, name: String, company: String, qty: Double, unit: String, estPrice: Double, notes: String): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_RAT_MONTH_KEY, monthKey)
            put(COL_RAT_NAME, name)
            put(COL_RAT_COMPANY, company)
            put(COL_RAT_QTY, qty)
            put(COL_RAT_UNIT, unit)
            put(COL_RAT_EST_PRICE, estPrice)
            put(COL_RAT_ACT_PRICE, 0.0)
            put(COL_RAT_PURCHASED, 0)
            put(COL_RAT_NOTES, notes)
        }
        return db.insert(TABLE_RATION, null, cv)
    }

    fun updateRationPurchased(id: Long, isPurchased: Boolean, actualPrice: Double) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_RAT_PURCHASED, if (isPurchased) 1 else 0)
            if (actualPrice > 0) put(COL_RAT_ACT_PRICE, actualPrice)
        }
        db.update(TABLE_RATION, cv, "$COL_RAT_ID = ?", arrayOf(id.toString()))
    }

    fun deleteRationItem(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_RATION, "$COL_RAT_ID = ?", arrayOf(id.toString()))
    }

    fun copyRationFromPreviousMonth(fromMonthKey: String, toMonthKey: String): Int {
        val prev = getRationItems(fromMonthKey)
        if (prev.isEmpty()) return 0
        val db = writableDatabase
        var count = 0
        for (item in prev) {
            val cv = ContentValues().apply {
                put(COL_RAT_MONTH_KEY, toMonthKey)
                put(COL_RAT_NAME, item.name)
                put(COL_RAT_COMPANY, item.company)
                put(COL_RAT_QTY, item.quantity)
                put(COL_RAT_UNIT, item.unit)
                put(COL_RAT_EST_PRICE, item.estimatedPrice)
                put(COL_RAT_ACT_PRICE, 0.0)
                put(COL_RAT_PURCHASED, 0)
                put(COL_RAT_NOTES, item.notes)
            }
            db.insert(TABLE_RATION, null, cv)
            count++
        }
        return count
    }

    // ==========================================
    // History / Archive & Reports
    // ==========================================
    fun getAllRecordedMonths(): List<String> {
        val set = mutableSetOf<String>()
        val db = readableDatabase
        fun queryMonths(query: String) {
            val cursor = db.rawQuery(query, null)
            cursor.use {
                while (it.moveToNext()) {
                    val m = if (!it.isNull(0)) it.getString(0) ?: "" else ""
                    if (m.isNotBlank()) set.add(m)
                }
            }
        }
        queryMonths("SELECT DISTINCT $COL_ME_MONTH_KEY FROM $TABLE_MONTHLY_EXP")
        queryMonths("SELECT DISTINCT $COL_DE_MONTH_KEY FROM $TABLE_DAILY_EXP")
        queryMonths("SELECT DISTINCT $COL_RAT_MONTH_KEY FROM $TABLE_RATION")

        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        set.add(currentMonth)
        return set.sortedDescending()
    }

    fun getDashboardSummary(monthKey: String): DashboardSummary {
        val db = readableDatabase
        var billsTotal = 0.0
        var billsPaid = 0.0
        var billsUnpaid = 0.0
        var pendingCount = 0

        val meCursor = db.rawQuery(
            "SELECT $COL_ME_AMOUNT, $COL_ME_IS_PAID FROM $TABLE_MONTHLY_EXP WHERE $COL_ME_MONTH_KEY = ?",
            arrayOf(monthKey)
        )
        meCursor.use {
            while (it.moveToNext()) {
                val amt = if (!it.isNull(0)) it.getDouble(0) else 0.0
                val isPaid = if (!it.isNull(1)) it.getInt(1) == 1 else false
                billsTotal += amt
                if (isPaid) billsPaid += amt else {
                    billsUnpaid += amt
                    pendingCount++
                }
            }
        }

        var dailyTotal = 0.0
        val deCursor = db.rawQuery(
            "SELECT SUM($COL_DE_AMOUNT) FROM $TABLE_DAILY_EXP WHERE $COL_DE_MONTH_KEY = ?",
            arrayOf(monthKey)
        )
        deCursor.use {
            if (it.moveToFirst() && !it.isNull(0)) dailyTotal = it.getDouble(0)
        }

        var rationTotal = 0.0
        var rationBought = 0
        var rationCount = 0
        val ratCursor = db.rawQuery(
            "SELECT $COL_RAT_ACT_PRICE, $COL_RAT_EST_PRICE, $COL_RAT_PURCHASED FROM $TABLE_RATION WHERE $COL_RAT_MONTH_KEY = ?",
            arrayOf(monthKey)
        )
        ratCursor.use {
            while (it.moveToNext()) {
                rationCount++
                val actPrice = if (!it.isNull(0)) it.getDouble(0) else 0.0
                val estPrice = if (!it.isNull(1)) it.getDouble(1) else 0.0
                val isPurchased = if (!it.isNull(2)) it.getInt(2) == 1 else false
                if (isPurchased) {
                    rationBought++
                    rationTotal += if (actPrice > 0) actPrice else estPrice
                }
            }
        }

        var weOwe = 0.0
        var owedToUs = 0.0
        val partyCursor = db.rawQuery("SELECT $COL_PARTY_TYPE, $COL_PARTY_BALANCE FROM $TABLE_PARTIES", null)
        partyCursor.use {
            while (it.moveToNext()) {
                val pType = if (!it.isNull(0)) it.getString(0) ?: "LENDER" else "LENDER"
                val bal = if (!it.isNull(1)) it.getDouble(1) else 0.0
                if (bal > 0) {
                    if (pType == "LENDER") weOwe += bal else owedToUs += bal
                }
            }
        }

        val grandTotal = billsPaid + dailyTotal + rationTotal

        return DashboardSummary(
            monthKey = monthKey,
            monthlyBillsTotal = billsTotal,
            monthlyBillsPaid = billsPaid,
            monthlyBillsUnpaid = billsUnpaid,
            monthlyBillsPendingCount = pendingCount,
            dailyExpensesTotal = dailyTotal,
            rationTotal = rationTotal,
            rationBoughtCount = rationBought,
            rationTotalCount = rationCount,
            khataWeOwe = weOwe,
            khataOwedToUs = owedToUs,
            grandTotalExpense = grandTotal
        )
    }

    // ==========================================
    // Restore Backup Data (Completely Null-Safe)
    // ==========================================
    fun restoreBackupData(
        parties: List<PartyRecord>?,
        transactions: List<KhataTransaction>?,
        monthlyExp: List<MonthlyExpenditure>?,
        dailyExp: List<DailyExpense>?,
        ration: List<RationItem>?
    ) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_PARTIES, null, null)
            db.delete(TABLE_KHATA_TRX, null, null)
            db.delete(TABLE_MONTHLY_EXP, null, null)
            db.delete(TABLE_DAILY_EXP, null, null)
            db.delete(TABLE_RATION, null, null)

            parties?.forEach { p ->
                val cv = ContentValues().apply {
                    if (p.id > 0) put(COL_PARTY_ID, p.id)
                    put(COL_PARTY_NAME, (p.name as String?).orEmpty().ifBlank { "Party" })
                    put(COL_PARTY_PHONE, (p.phone as String?).orEmpty())
                    put(COL_PARTY_TYPE, (p.type as String?).orEmpty().ifBlank { "LENDER" })
                    put(COL_PARTY_BALANCE, p.currentBalance)
                    put(COL_PARTY_UPDATED_AT, (p.updatedAt as String?).orEmpty())
                    put(COL_PARTY_NOTES, (p.notes as String?).orEmpty())
                }
                db.insert(TABLE_PARTIES, null, cv)
            }

            transactions?.forEach { t ->
                val cv = ContentValues().apply {
                    if (t.id > 0) put(COL_KT_ID, t.id)
                    put(COL_KT_PARTY_ID, t.partyId)
                    put(COL_KT_PARTY_NAME, (t.partyName as String?).orEmpty())
                    put(COL_KT_TYPE, (t.type as String?).orEmpty().ifBlank { "DEBIT" })
                    put(COL_KT_AMOUNT, t.amount)
                    put(COL_KT_DATE, (t.date as String?).orEmpty())
                    put(COL_KT_NOTES, (t.notes as String?).orEmpty())
                    put(COL_KT_CREATED_AT, if (t.createdAt > 0) t.createdAt else System.currentTimeMillis())
                }
                db.insert(TABLE_KHATA_TRX, null, cv)
            }

            monthlyExp?.forEach { m ->
                val cv = ContentValues().apply {
                    if (m.id > 0) put(COL_ME_ID, m.id)
                    put(COL_ME_MONTH_KEY, (m.monthKey as String?).orEmpty())
                    put(COL_ME_TITLE, (m.title as String?).orEmpty().ifBlank { "Expense" })
                    put(COL_ME_AMOUNT, m.amount)
                    put(COL_ME_IS_PAID, if (m.isPaid) 1 else 0)
                    put(COL_ME_PAID_DATE, (m.paidDate as String?).orEmpty())
                    put(COL_ME_NOTES, (m.notes as String?).orEmpty())
                }
                db.insert(TABLE_MONTHLY_EXP, null, cv)
            }

            dailyExp?.forEach { d ->
                val cv = ContentValues().apply {
                    if (d.id > 0) put(COL_DE_ID, d.id)
                    put(COL_DE_DATE, (d.date as String?).orEmpty())
                    put(COL_DE_MONTH_KEY, (d.monthKey as String?).orEmpty())
                    put(COL_DE_TITLE, (d.title as String?).orEmpty().ifBlank { "Expense" })
                    put(COL_DE_AMOUNT, d.amount)
                    put(COL_DE_PAY_METHOD, (d.paymentMethod as String?).orEmpty().ifBlank { "Cash" })
                    put(COL_DE_NOTES, (d.notes as String?).orEmpty())
                    put(COL_DE_CREATED_AT, if (d.createdAt > 0) d.createdAt else System.currentTimeMillis())
                }
                db.insert(TABLE_DAILY_EXP, null, cv)
            }

            ration?.forEach { r ->
                val cv = ContentValues().apply {
                    if (r.id > 0) put(COL_RAT_ID, r.id)
                    put(COL_RAT_MONTH_KEY, (r.monthKey as String?).orEmpty())
                    put(COL_RAT_NAME, (r.name as String?).orEmpty().ifBlank { "Item" })
                    put(COL_RAT_COMPANY, (r.company as String?).orEmpty())
                    put(COL_RAT_QTY, r.quantity)
                    put(COL_RAT_UNIT, (r.unit as String?).orEmpty().ifBlank { "piece" })
                    put(COL_RAT_EST_PRICE, r.estimatedPrice)
                    put(COL_RAT_ACT_PRICE, r.actualPrice)
                    put(COL_RAT_PURCHASED, if (r.isPurchased) 1 else 0)
                    put(COL_RAT_NOTES, (r.notes as String?).orEmpty())
                }
                db.insert(TABLE_RATION, null, cv)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
