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
        const val DATABASE_NAME = "ghardari_v2.db"
        const val DATABASE_VERSION = 1

        // Table Parties (Udhar Khata)
        const val TABLE_PARTIES = "parties"
        const val COL_PARTY_ID = "id"
        const val COL_PARTY_NAME = "name"
        const val COL_PARTY_PHONE = "phone"
        const val COL_PARTY_TYPE = "type" // "LENDER" (we owe) or "BORROWER" (they owe)
        const val COL_PARTY_BALANCE = "current_balance"
        const val COL_PARTY_UPDATED_AT = "updated_at"
        const val COL_PARTY_NOTES = "notes"

        // Table Khata Transactions
        const val TABLE_KHATA_TRX = "khata_transactions"
        const val COL_KT_ID = "id"
        const val COL_KT_PARTY_ID = "party_id"
        const val COL_KT_PARTY_NAME = "party_name"
        const val COL_KT_TYPE = "type" // "DEBIT" (we gave), "CREDIT" (we took), "CLEAR"
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
        const val COL_RAT_QTY = "quantity"
        const val COL_RAT_UNIT = "unit"
        const val COL_RAT_EST_PRICE = "est_price"
        const val COL_RAT_ACT_PRICE = "act_price"
        const val COL_RAT_PURCHASED = "is_purchased"
        const val COL_RAT_NOTES = "notes"

        // Table Custom Dashboard Cards
        const val TABLE_CUSTOM_CARDS = "custom_dashboard_cards"
        const val COL_CC_ID = "id"
        const val COL_CC_TITLE = "title"
        const val COL_CC_TARGET_TYPE = "target_type"
        const val COL_CC_TARGET_ID = "target_id"
        const val COL_CC_ICON = "icon"
        const val COL_CC_COLOR = "color"
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
                $COL_RAT_QTY REAL NOT NULL,
                $COL_RAT_UNIT TEXT NOT NULL,
                $COL_RAT_EST_PRICE REAL DEFAULT 0.0,
                $COL_RAT_ACT_PRICE REAL DEFAULT 0.0,
                $COL_RAT_PURCHASED INTEGER DEFAULT 0,
                $COL_RAT_NOTES TEXT DEFAULT ''
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_CUSTOM_CARDS (
                $COL_CC_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CC_TITLE TEXT NOT NULL,
                $COL_CC_TARGET_TYPE TEXT NOT NULL,
                $COL_CC_TARGET_ID INTEGER,
                $COL_CC_ICON TEXT DEFAULT 'Star',
                $COL_CC_COLOR TEXT DEFAULT '#0F766E'
            )
            """.trimIndent()
        )

        // Seed initial monthly expenditures & parties
        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    private fun seedInitialData(db: SQLiteDatabase) {
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        val defaultBills = listOf(
            "بجلي جو بل" to 30000.0,
            "پاڻي بل" to 4000.0,
            "اسڪول رڪشا خرچ" to 3000.0,
            "ٻارن جي اسڪول فيس" to 15000.0,
            "کير واري جو کاتو" to 8000.0,
            "گئس جو بل" to 1500.0
        )
        for (bill in defaultBills) {
            val cv = ContentValues().apply {
                put(COL_ME_MONTH_KEY, currentMonth)
                put(COL_ME_TITLE, bill.first)
                put(COL_ME_AMOUNT, bill.second)
                put(COL_ME_IS_PAID, 0)
                put(COL_ME_PAID_DATE, "")
                put(COL_ME_NOTES, "")
            }
            db.insert(TABLE_MONTHLY_EXP, null, cv)
        }
    }

    // ==========================================
    // 1. Udhar Khata (Parties & Transactions)
    // ==========================================
    fun getAllParties(): List<PartyRecord> {
        val list = mutableListOf<PartyRecord>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PARTIES ORDER BY ABS($COL_PARTY_BALANCE) DESC, $COL_PARTY_NAME ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    PartyRecord(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_PARTY_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(COL_PARTY_NAME)),
                        phone = it.getString(it.getColumnIndexOrThrow(COL_PARTY_PHONE)),
                        type = it.getString(it.getColumnIndexOrThrow(COL_PARTY_TYPE)),
                        currentBalance = it.getDouble(it.getColumnIndexOrThrow(COL_PARTY_BALANCE)),
                        updatedAt = it.getString(it.getColumnIndexOrThrow(COL_PARTY_UPDATED_AT)),
                        notes = it.getString(it.getColumnIndexOrThrow(COL_PARTY_NOTES))
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
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val trxType = if (type == "LENDER") "CREDIT" else "DEBIT"
            addKhataTransaction(pId, name, trxType, initialBalance, "ابتدائي بيلنس (Initial balance)")
        }
        return pId
    }

    fun addKhataTransaction(partyId: Long, partyName: String, type: String, amount: Double, notes: String): Long {
        val db = writableDatabase
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val cv = ContentValues().apply {
            put(COL_KT_PARTY_ID, partyId)
            put(COL_KT_PARTY_NAME, partyName)
            put(COL_KT_TYPE, type)
            put(COL_KT_AMOUNT, amount)
            put(COL_KT_DATE, dateStr)
            put(COL_KT_NOTES, notes)
            put(COL_KT_CREATED_AT, System.currentTimeMillis())
        }
        val id = db.insert(TABLE_KHATA_TRX, null, cv)
        recalculatePartyBalance(partyId)
        return id
    }

    fun clearPartyAccount(partyId: Long, partyName: String) {
        val db = writableDatabase
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val cv = ContentValues().apply {
            put(COL_KT_PARTY_ID, partyId)
            put(COL_KT_PARTY_NAME, partyName)
            put(COL_KT_TYPE, "CLEAR")
            put(COL_KT_AMOUNT, 0.0)
            put(COL_KT_DATE, dateStr)
            put(COL_KT_NOTES, "حساب صاف ٿي ويو (Account Cleared)")
            put(COL_KT_CREATED_AT, System.currentTimeMillis())
        }
        db.insert(TABLE_KHATA_TRX, null, cv)

        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val partyCv = ContentValues().apply {
            put(COL_PARTY_BALANCE, 0.0)
            put(COL_PARTY_UPDATED_AT, now)
        }
        db.update(TABLE_PARTIES, partyCv, "$COL_PARTY_ID = ?", arrayOf(partyId.toString()))
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
                        id = it.getLong(it.getColumnIndexOrThrow(COL_KT_ID)),
                        partyId = it.getLong(it.getColumnIndexOrThrow(COL_KT_PARTY_ID)),
                        partyName = it.getString(it.getColumnIndexOrThrow(COL_KT_PARTY_NAME)),
                        type = it.getString(it.getColumnIndexOrThrow(COL_KT_TYPE)),
                        amount = it.getDouble(it.getColumnIndexOrThrow(COL_KT_AMOUNT)),
                        date = it.getString(it.getColumnIndexOrThrow(COL_KT_DATE)),
                        notes = it.getString(it.getColumnIndexOrThrow(COL_KT_NOTES)),
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COL_KT_CREATED_AT))
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
                val type = it.getString(0)
                val amount = it.getDouble(1)
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
                        id = it.getLong(it.getColumnIndexOrThrow(COL_ME_ID)),
                        monthKey = it.getString(it.getColumnIndexOrThrow(COL_ME_MONTH_KEY)),
                        title = it.getString(it.getColumnIndexOrThrow(COL_ME_TITLE)),
                        amount = it.getDouble(it.getColumnIndexOrThrow(COL_ME_AMOUNT)),
                        isPaid = it.getInt(it.getColumnIndexOrThrow(COL_ME_IS_PAID)) == 1,
                        paidDate = it.getString(it.getColumnIndexOrThrow(COL_ME_PAID_DATE)),
                        notes = it.getString(it.getColumnIndexOrThrow(COL_ME_NOTES))
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

    // Bring / Copy previous month's expenditures into target month!
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
                put(COL_ME_IS_PAID, 0) // reset to unpaid for the new month!
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
                        id = it.getLong(it.getColumnIndexOrThrow(COL_DE_ID)),
                        date = it.getString(it.getColumnIndexOrThrow(COL_DE_DATE)),
                        monthKey = it.getString(it.getColumnIndexOrThrow(COL_DE_MONTH_KEY)),
                        title = it.getString(it.getColumnIndexOrThrow(COL_DE_TITLE)),
                        amount = it.getDouble(it.getColumnIndexOrThrow(COL_DE_AMOUNT)),
                        paymentMethod = it.getString(it.getColumnIndexOrThrow(COL_DE_PAY_METHOD)),
                        notes = it.getString(it.getColumnIndexOrThrow(COL_DE_NOTES)),
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COL_DE_CREATED_AT))
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
        val cursor = db.rawQuery("SELECT * FROM $TABLE_RATION WHERE $COL_RAT_MONTH_KEY = ? ORDER BY $COL_RAT_PURCHASED ASC, $COL_RAT_ID ASC", arrayOf(monthKey))
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    RationItem(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_RAT_ID)),
                        monthKey = it.getString(it.getColumnIndexOrThrow(COL_RAT_MONTH_KEY)),
                        name = it.getString(it.getColumnIndexOrThrow(COL_RAT_NAME)),
                        quantity = it.getDouble(it.getColumnIndexOrThrow(COL_RAT_QTY)),
                        unit = it.getString(it.getColumnIndexOrThrow(COL_RAT_UNIT)),
                        estimatedPrice = it.getDouble(it.getColumnIndexOrThrow(COL_RAT_EST_PRICE)),
                        actualPrice = it.getDouble(it.getColumnIndexOrThrow(COL_RAT_ACT_PRICE)),
                        isPurchased = it.getInt(it.getColumnIndexOrThrow(COL_RAT_PURCHASED)) == 1,
                        notes = it.getString(it.getColumnIndexOrThrow(COL_RAT_NOTES))
                    )
                )
            }
        }
        if (list.isEmpty()) {
            seedDefaultRation(monthKey)
            return getRationItems(monthKey)
        }
        return list
    }

    fun addRationItem(monthKey: String, name: String, qty: Double, unit: String, estPrice: Double, notes: String): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_RAT_MONTH_KEY, monthKey)
            put(COL_RAT_NAME, name)
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

    private fun seedDefaultRation(monthKey: String) {
        val defaults = listOf(
            Triple("کنڊ", 3.0, "kg"),
            Triple("اٽو", 10.0, "kg"),
            Triple("چانور", 5.0, "kg"),
            Triple("تيل / گيهه", 5.0, "kg"),
            Triple("شيمپو", 1.0, "bottle"),
            Triple("چانهه جي پتي", 500.0, "gm"),
            Triple("صابڻ", 6.0, "piece")
        )
        val db = writableDatabase
        for (item in defaults) {
            val cv = ContentValues().apply {
                put(COL_RAT_MONTH_KEY, monthKey)
                put(COL_RAT_NAME, item.first)
                put(COL_RAT_QTY, item.second)
                put(COL_RAT_UNIT, item.third)
                put(COL_RAT_EST_PRICE, 0.0)
                put(COL_RAT_ACT_PRICE, 0.0)
                put(COL_RAT_PURCHASED, 0)
                put(COL_RAT_NOTES, "")
            }
            db.insert(TABLE_RATION, null, cv)
        }
    }

    // ==========================================
    // 5. Custom Dashboard Cards
    // ==========================================
    fun getCustomCards(): List<CustomCard> {
        val list = mutableListOf<CustomCard>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CUSTOM_CARDS ORDER BY $COL_CC_ID ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                val targetId = if (it.isNull(it.getColumnIndexOrThrow(COL_CC_TARGET_ID))) null else it.getLong(it.getColumnIndexOrThrow(COL_CC_TARGET_ID))
                list.add(
                    CustomCard(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_CC_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COL_CC_TITLE)),
                        targetType = it.getString(it.getColumnIndexOrThrow(COL_CC_TARGET_TYPE)),
                        targetId = targetId,
                        icon = it.getString(it.getColumnIndexOrThrow(COL_CC_ICON)),
                        color = it.getString(it.getColumnIndexOrThrow(COL_CC_COLOR))
                    )
                )
            }
        }
        return list
    }

    fun addCustomCard(title: String, targetType: String, targetId: Long?, icon: String, color: String): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_CC_TITLE, title)
            put(COL_CC_TARGET_TYPE, targetType)
            if (targetId != null) put(COL_CC_TARGET_ID, targetId)
            put(COL_CC_ICON, icon)
            put(COL_CC_COLOR, color)
        }
        return db.insert(TABLE_CUSTOM_CARDS, null, cv)
    }

    fun deleteCustomCard(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_CUSTOM_CARDS, "$COL_CC_ID = ?", arrayOf(id.toString()))
    }

    // ==========================================
    // Dashboard Summary Calculations
    // ==========================================
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
                val amt = it.getDouble(0)
                val isPaid = it.getInt(1) == 1
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
                val actPrice = it.getDouble(0)
                val estPrice = it.getDouble(1)
                val isPurchased = it.getInt(2) == 1
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
                val pType = it.getString(0)
                val bal = it.getDouble(1)
                if (bal > 0) {
                    if (pType == "LENDER") weOwe += bal else owedToUs += bal
                }
            }
        }

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
            khataOwedToUs = owedToUs
        )
    }
}
