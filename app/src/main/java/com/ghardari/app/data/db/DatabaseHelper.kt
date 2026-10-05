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
        const val DATABASE_NAME = "ghardari_household.db"
        const val DATABASE_VERSION = 1

        // Table Categories
        const val TABLE_CATEGORIES = "categories"
        const val COL_CAT_ID = "id"
        const val COL_CAT_KEY = "category_key"
        const val COL_CAT_NAME_SD = "name_sd"
        const val COL_CAT_NAME_UR = "name_ur"
        const val COL_CAT_NAME_EN = "name_en"
        const val COL_CAT_ICON = "icon"
        const val COL_CAT_COLOR = "color"
        const val COL_CAT_IS_CUSTOM = "is_custom"

        // Table Parties (Khata)
        const val TABLE_PARTIES = "parties"
        const val COL_PARTY_ID = "id"
        const val COL_PARTY_NAME = "name"
        const val COL_PARTY_PHONE = "phone"
        const val COL_PARTY_TYPE = "type"
        const val COL_PARTY_BALANCE = "current_balance"
        const val COL_PARTY_UPDATED_AT = "updated_at"
        const val COL_PARTY_NOTES = "notes"

        // Table Transactions
        const val TABLE_TRANSACTIONS = "transactions"
        const val COL_TRX_ID = "id"
        const val COL_TRX_PARTY_ID = "party_id"
        const val COL_TRX_PARTY_NAME = "party_name"
        const val COL_TRX_CATEGORY_ID = "category_id"
        const val COL_TRX_CATEGORY_KEY = "category_key"
        const val COL_TRX_TYPE = "type"
        const val COL_TRX_AMOUNT = "amount"
        const val COL_TRX_DATE = "date"
        const val COL_TRX_MONTH_KEY = "month_key"
        const val COL_TRX_PAY_METHOD = "payment_method"
        const val COL_TRX_STATUS = "status"
        const val COL_TRX_NOTES = "notes"
        const val COL_TRX_CREATED_AT = "created_at"

        // Table Ration Items
        const val TABLE_RATION = "ration_items"
        const val COL_RAT_ID = "id"
        const val COL_RAT_MONTH_KEY = "month_key"
        const val COL_RAT_NAME_SD = "name_sd"
        const val COL_RAT_NAME_UR = "name_ur"
        const val COL_RAT_NAME_EN = "name_en"
        const val COL_RAT_QTY = "quantity"
        const val COL_RAT_UNIT = "unit"
        const val COL_RAT_EST_PRICE = "est_price"
        const val COL_RAT_ACT_PRICE = "act_price"
        const val COL_RAT_PURCHASED = "is_purchased"
        const val COL_RAT_NOTES = "notes"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Categories
        db.execSQL(
            """
            CREATE TABLE $TABLE_CATEGORIES (
                $COL_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CAT_KEY TEXT UNIQUE NOT NULL,
                $COL_CAT_NAME_SD TEXT NOT NULL,
                $COL_CAT_NAME_UR TEXT NOT NULL,
                $COL_CAT_NAME_EN TEXT NOT NULL,
                $COL_CAT_ICON TEXT NOT NULL,
                $COL_CAT_COLOR TEXT NOT NULL,
                $COL_CAT_IS_CUSTOM INTEGER DEFAULT 0
            )
            """.trimIndent()
        )

        // Create Parties (Khata)
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

        // Create Transactions
        db.execSQL(
            """
            CREATE TABLE $TABLE_TRANSACTIONS (
                $COL_TRX_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TRX_PARTY_ID INTEGER,
                $COL_TRX_PARTY_NAME TEXT DEFAULT '',
                $COL_TRX_CATEGORY_ID INTEGER,
                $COL_TRX_CATEGORY_KEY TEXT DEFAULT '',
                $COL_TRX_TYPE TEXT NOT NULL,
                $COL_TRX_AMOUNT REAL NOT NULL,
                $COL_TRX_DATE TEXT NOT NULL,
                $COL_TRX_MONTH_KEY TEXT NOT NULL,
                $COL_TRX_PAY_METHOD TEXT DEFAULT 'Cash',
                $COL_TRX_STATUS TEXT DEFAULT 'CLEARED',
                $COL_TRX_NOTES TEXT DEFAULT '',
                $COL_TRX_CREATED_AT INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // Create Ration Items
        db.execSQL(
            """
            CREATE TABLE $TABLE_RATION (
                $COL_RAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_RAT_MONTH_KEY TEXT NOT NULL,
                $COL_RAT_NAME_SD TEXT NOT NULL,
                $COL_RAT_NAME_UR TEXT NOT NULL,
                $COL_RAT_NAME_EN TEXT NOT NULL,
                $COL_RAT_QTY REAL NOT NULL,
                $COL_RAT_UNIT TEXT NOT NULL,
                $COL_RAT_EST_PRICE REAL DEFAULT 0.0,
                $COL_RAT_ACT_PRICE REAL DEFAULT 0.0,
                $COL_RAT_PURCHASED INTEGER DEFAULT 0,
                $COL_RAT_NOTES TEXT DEFAULT ''
            )
            """.trimIndent()
        )

        seedDefaultCategories(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Version upgrades handled here
    }

    private data class CatSeed(
        val key: String,
        val nameSd: String,
        val nameUr: String,
        val nameEn: String,
        val icon: String,
        val color: String
    )

    private fun seedDefaultCategories(db: SQLiteDatabase) {
        val defaults = listOf(
            CatSeed("school_fee", "ٻارن جي اسڪول فيس", "بچوں کی اسکول فیس", "School Fee", "School", "#2563EB"),
            CatSeed("rickshaw", "اسڪول رڪشا ۽ گاڏي", "اسکول رکشہ", "School Rickshaw", "DirectionsBus", "#D97706"),
            CatSeed("milkman", "کير وارو", "دودھ والا", "Milkman", "LocalDrink", "#059669"),
            CatSeed("bills", "بجلي ۽ گئس جا بل", "بجلی اور گیس بل", "Utility Bills", "FlashOn", "#DC2626"),
            CatSeed("grocery", "گراسري ۽ راشن", "راشن اور سودا سلف", "Grocery", "ShoppingCart", "#7C3AED"),
            CatSeed("clothing", "ڪپڙا ۽ سينگار", "کپڑے اور سنگھار", "Clothing", "Checkroom", "#DB2777"),
            CatSeed("tailor", "درزي ۽ سڀائي", "درزی اور سلائی", "Tailoring", "ContentCut", "#475569"),
            CatSeed("bc", "بي سي / ڪميٽي", "بی سی کمیٹی", "Committee BC", "AccountBalance", "#0D9488"),
            CatSeed("medical", "علاج ۽ دوائون", "علاج معالجہ و ادویات", "Medical", "LocalHospital", "#E11D48"),
            CatSeed("other", "ٻيا متفرق خرچ", "دیگر اخراجات", "Other", "MoreHoriz", "#64748B")
        )

        for (item in defaults) {
            val cv = ContentValues().apply {
                put(COL_CAT_KEY, item.key)
                put(COL_CAT_NAME_SD, item.nameSd)
                put(COL_CAT_NAME_UR, item.nameUr)
                put(COL_CAT_NAME_EN, item.nameEn)
                put(COL_CAT_ICON, item.icon)
                put(COL_CAT_COLOR, item.color)
                put(COL_CAT_IS_CUSTOM, 0)
            }
            db.insert(TABLE_CATEGORIES, null, cv)
        }
    }

    // ==========================================
    // Category Operations
    // ==========================================
    fun getAllCategories(): List<CategoryRecord> {
        val list = mutableListOf<CategoryRecord>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORIES ORDER BY $COL_CAT_IS_CUSTOM ASC, $COL_CAT_ID ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    CategoryRecord(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_CAT_ID)),
                        key = it.getString(it.getColumnIndexOrThrow(COL_CAT_KEY)),
                        nameSd = it.getString(it.getColumnIndexOrThrow(COL_CAT_NAME_SD)),
                        nameUr = it.getString(it.getColumnIndexOrThrow(COL_CAT_NAME_UR)),
                        nameEn = it.getString(it.getColumnIndexOrThrow(COL_CAT_NAME_EN)),
                        icon = it.getString(it.getColumnIndexOrThrow(COL_CAT_ICON)),
                        color = it.getString(it.getColumnIndexOrThrow(COL_CAT_COLOR)),
                        isCustom = it.getInt(it.getColumnIndexOrThrow(COL_CAT_IS_CUSTOM)) == 1
                    )
                )
            }
        }
        return list
    }

    fun addCustomCategory(nameSd: String, nameUr: String, nameEn: String, icon: String, color: String): Long {
        val db = writableDatabase
        val key = "custom_" + System.currentTimeMillis()
        val cv = ContentValues().apply {
            put(COL_CAT_KEY, key)
            put(COL_CAT_NAME_SD, nameSd.ifBlank { nameEn })
            put(COL_CAT_NAME_UR, nameUr.ifBlank { nameSd })
            put(COL_CAT_NAME_EN, nameEn.ifBlank { nameSd })
            put(COL_CAT_ICON, icon)
            put(COL_CAT_COLOR, color)
            put(COL_CAT_IS_CUSTOM, 1)
        }
        return db.insert(TABLE_CATEGORIES, null, cv)
    }

    fun deleteCategory(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_CATEGORIES, "$COL_CAT_ID = ? AND $COL_CAT_IS_CUSTOM = 1", arrayOf(id.toString()))
    }

    // ==========================================
    // Party (Khata) Operations
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
        val partyId = db.insert(TABLE_PARTIES, null, cv)

        if (initialBalance > 0) {
            val monthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val trxType = if (type == "LENDER") "KHATA_GIVE" else "KHATA_RECEIVE"
            addTransaction(
                partyId = partyId,
                partyName = name,
                categoryId = null,
                categoryKey = "khata",
                type = trxType,
                amount = initialBalance,
                date = dateStr,
                monthKey = monthKey,
                paymentMethod = "Cash",
                status = "PENDING",
                notes = if (notes.isNotBlank()) notes else "Initial Khata balance"
            )
        }
        return partyId
    }

    fun updatePartyBalance(partyId: Long, newBalance: Double) {
        val db = writableDatabase
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val cv = ContentValues().apply {
            put(COL_PARTY_BALANCE, newBalance)
            put(COL_PARTY_UPDATED_AT, now)
        }
        db.update(TABLE_PARTIES, cv, "$COL_PARTY_ID = ?", arrayOf(partyId.toString()))
    }

    fun clearPartyAccount(partyId: Long, partyName: String) {
        val db = writableDatabase
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val cv = ContentValues().apply {
            put(COL_PARTY_BALANCE, 0.0)
            put(COL_PARTY_UPDATED_AT, now)
        }
        db.update(TABLE_PARTIES, cv, "$COL_PARTY_ID = ?", arrayOf(partyId.toString()))

        // Add clear transaction
        val monthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        addTransaction(
            partyId = partyId,
            partyName = partyName,
            categoryId = null,
            categoryKey = "khata",
            type = "KHATA_CLEAR",
            amount = 0.0,
            date = dateStr,
            monthKey = monthKey,
            paymentMethod = "Cash",
            status = "CLEARED",
            notes = "حساب صاف ٿي ويو (Account Cleared)"
        )
    }

    // ==========================================
    // Transaction Operations
    // ==========================================
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
        val db = writableDatabase
        val cv = ContentValues().apply {
            if (partyId != null) put(COL_TRX_PARTY_ID, partyId)
            put(COL_TRX_PARTY_NAME, partyName)
            if (categoryId != null) put(COL_TRX_CATEGORY_ID, categoryId)
            put(COL_TRX_CATEGORY_KEY, categoryKey)
            put(COL_TRX_TYPE, type)
            put(COL_TRX_AMOUNT, amount)
            put(COL_TRX_DATE, date)
            put(COL_TRX_MONTH_KEY, monthKey)
            put(COL_TRX_PAY_METHOD, paymentMethod)
            put(COL_TRX_STATUS, status)
            put(COL_TRX_NOTES, notes)
            put(COL_TRX_CREATED_AT, System.currentTimeMillis())
        }
        val id = db.insert(TABLE_TRANSACTIONS, null, cv)

        // Automatically update party balance if linked to a party
        if (partyId != null && partyId > 0) {
            recalculatePartyBalance(partyId)
        }

        return id
    }

    fun getTransactionsByMonth(monthKey: String): List<TransactionRecord> {
        val list = mutableListOf<TransactionRecord>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_TRANSACTIONS WHERE $COL_TRX_MONTH_KEY = ? ORDER BY $COL_TRX_DATE DESC, $COL_TRX_CREATED_AT DESC",
            arrayOf(monthKey)
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(extractTransactionFromCursor(it))
            }
        }
        return list
    }

    fun getTransactionsByParty(partyId: Long): List<TransactionRecord> {
        val list = mutableListOf<TransactionRecord>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_TRANSACTIONS WHERE $COL_TRX_PARTY_ID = ? ORDER BY $COL_TRX_DATE DESC, $COL_TRX_CREATED_AT DESC",
            arrayOf(partyId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(extractTransactionFromCursor(it))
            }
        }
        return list
    }

    fun deleteTransaction(id: Long) {
        val db = writableDatabase
        var partyId: Long? = null
        val cursor = db.rawQuery("SELECT $COL_TRX_PARTY_ID FROM $TABLE_TRANSACTIONS WHERE $COL_TRX_ID = ?", arrayOf(id.toString()))
        cursor.use {
            if (it.moveToFirst() && !it.isNull(0)) {
                partyId = it.getLong(0)
            }
        }
        db.delete(TABLE_TRANSACTIONS, "$COL_TRX_ID = ?", arrayOf(id.toString()))
        if (partyId != null && partyId!! > 0) {
            recalculatePartyBalance(partyId!!)
        }
    }

    private fun recalculatePartyBalance(partyId: Long) {
        val db = writableDatabase
        var balance = 0.0
        val cursor = db.rawQuery(
            "SELECT $COL_TRX_TYPE, $COL_TRX_AMOUNT FROM $TABLE_TRANSACTIONS WHERE $COL_TRX_PARTY_ID = ?",
            arrayOf(partyId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                val type = it.getString(0)
                val amount = it.getDouble(1)
                when (type) {
                    "KHATA_GIVE" -> balance += amount
                    "KHATA_RECEIVE" -> balance -= amount
                    "KHATA_CLEAR" -> balance = 0.0
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

    private fun extractTransactionFromCursor(it: Cursor): TransactionRecord {
        val partyId = if (it.isNull(it.getColumnIndexOrThrow(COL_TRX_PARTY_ID))) null else it.getLong(it.getColumnIndexOrThrow(COL_TRX_PARTY_ID))
        val categoryId = if (it.isNull(it.getColumnIndexOrThrow(COL_TRX_CATEGORY_ID))) null else it.getLong(it.getColumnIndexOrThrow(COL_TRX_CATEGORY_ID))
        return TransactionRecord(
            id = it.getLong(it.getColumnIndexOrThrow(COL_TRX_ID)),
            partyId = partyId,
            partyName = it.getString(it.getColumnIndexOrThrow(COL_TRX_PARTY_NAME)),
            categoryId = categoryId,
            categoryKey = it.getString(it.getColumnIndexOrThrow(COL_TRX_CATEGORY_KEY)),
            type = it.getString(it.getColumnIndexOrThrow(COL_TRX_TYPE)),
            amount = it.getDouble(it.getColumnIndexOrThrow(COL_TRX_AMOUNT)),
            date = it.getString(it.getColumnIndexOrThrow(COL_TRX_DATE)),
            monthKey = it.getString(it.getColumnIndexOrThrow(COL_TRX_MONTH_KEY)),
            paymentMethod = it.getString(it.getColumnIndexOrThrow(COL_TRX_PAY_METHOD)),
            status = it.getString(it.getColumnIndexOrThrow(COL_TRX_STATUS)),
            notes = it.getString(it.getColumnIndexOrThrow(COL_TRX_NOTES)),
            createdAt = it.getLong(it.getColumnIndexOrThrow(COL_TRX_CREATED_AT))
        )
    }

    // ==========================================
    // Monthly Summary Calculation
    // ==========================================
    fun getMonthSummary(monthKey: String): MonthSummary {
        val db = readableDatabase
        var inflow = 0.0
        var outflow = 0.0

        val cursor = db.rawQuery(
            "SELECT $COL_TRX_TYPE, SUM($COL_TRX_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COL_TRX_MONTH_KEY = ? GROUP BY $COL_TRX_TYPE",
            arrayOf(monthKey)
        )
        cursor.use {
            while (it.moveToNext()) {
                val type = it.getString(0)
                val sum = it.getDouble(1)
                when (type) {
                    "INFLOW" -> inflow += sum
                    "OUTFLOW" -> outflow += sum
                }
            }
        }

        // Khata balances
        var owedToUs = 0.0
        var weOwe = 0.0
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

        // Ration summary for the month
        var rationTotal = 0.0
        var boughtCount = 0
        var totalCount = 0
        val ratCursor = db.rawQuery("SELECT $COL_RAT_ACT_PRICE, $COL_RAT_EST_PRICE, $COL_RAT_PURCHASED FROM $TABLE_RATION WHERE $COL_RAT_MONTH_KEY = ?", arrayOf(monthKey))
        ratCursor.use {
            while (it.moveToNext()) {
                totalCount++
                val actPrice = it.getDouble(0)
                val estPrice = it.getDouble(1)
                val isPurchased = it.getInt(2) == 1
                if (isPurchased) {
                    boughtCount++
                    rationTotal += if (actPrice > 0) actPrice else estPrice
                }
            }
        }

        return MonthSummary(
            monthKey = monthKey,
            totalInflow = inflow,
            totalOutflow = outflow,
            netBalance = inflow - outflow,
            khataOwedToUs = owedToUs,
            khataWeOwe = weOwe,
            rationTotal = rationTotal,
            rationBoughtCount = boughtCount,
            rationTotalCount = totalCount
        )
    }

    // ==========================================
    // Ration / Grocery Operations
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
                        nameSd = it.getString(it.getColumnIndexOrThrow(COL_RAT_NAME_SD)),
                        nameUr = it.getString(it.getColumnIndexOrThrow(COL_RAT_NAME_UR)),
                        nameEn = it.getString(it.getColumnIndexOrThrow(COL_RAT_NAME_EN)),
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

        // If list is empty for this month, seed default grocery essentials for household
        if (list.isEmpty()) {
            seedDefaultRationItems(monthKey)
            return getRationItems(monthKey)
        }

        return list
    }

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
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_RAT_MONTH_KEY, monthKey)
            put(COL_RAT_NAME_SD, nameSd.ifBlank { nameEn })
            put(COL_RAT_NAME_UR, nameUr.ifBlank { nameSd })
            put(COL_RAT_NAME_EN, nameEn.ifBlank { nameSd })
            put(COL_RAT_QTY, quantity)
            put(COL_RAT_UNIT, unit)
            put(COL_RAT_EST_PRICE, estPrice)
            put(COL_RAT_ACT_PRICE, actualPrice)
            put(COL_RAT_PURCHASED, if (isPurchased) 1 else 0)
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

    private data class RationSeed(
        val nameSd: String,
        val nameUr: String,
        val nameEn: String,
        val qty: Double,
        val unit: String
    )

    private fun seedDefaultRationItems(monthKey: String) {
        val defaults = listOf(
            RationSeed("کنڊ", "چینی", "Sugar", 3.0, "kg"),
            RationSeed("اٽو", "آٹا", "Wheat Flour", 10.0, "kg"),
            RationSeed("چانور", "چاول", "Rice", 5.0, "kg"),
            RationSeed("تيل / گيهه", "تیل / گھی", "Cooking Oil / Ghee", 5.0, "kg"),
            RationSeed("شيمپو", "شیمپو", "Shampoo (500 ml)", 1.0, "bottle"),
            RationSeed("چانهه جي پتي", "چائے کی پتی", "Tea Leaves", 500.0, "gm"),
            RationSeed("ڌوئڻ ۽ وهنجڻ جا صابڻ", "صابن", "Soaps", 6.0, "piece"),
            RationSeed("دال چنا ۽ مونگ", "دال چنا / مونگ", "Pulses / Lentils", 2.0, "kg")
        )

        val db = writableDatabase
        for (item in defaults) {
            val cv = ContentValues().apply {
                put(COL_RAT_MONTH_KEY, monthKey)
                put(COL_RAT_NAME_SD, item.nameSd)
                put(COL_RAT_NAME_UR, item.nameUr)
                put(COL_RAT_NAME_EN, item.nameEn)
                put(COL_RAT_QTY, item.qty)
                put(COL_RAT_UNIT, item.unit)
                put(COL_RAT_EST_PRICE, 0.0)
                put(COL_RAT_ACT_PRICE, 0.0)
                put(COL_RAT_PURCHASED, 0)
                put(COL_RAT_NOTES, "")
            }
            db.insert(TABLE_RATION, null, cv)
        }
    }
}
