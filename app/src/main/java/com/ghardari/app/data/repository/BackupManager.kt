package com.ghardari.app.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.ghardari.app.data.model.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupManager(private val context: Context, private val repository: ExpensesRepository) {

    data class BackupData(
        val appVersion: String? = "3.0.0",
        val exportedAt: String? = null,
        val currentMonthKey: String? = null,
        val parties: List<PartyRecord>? = null,
        val khataTransactions: List<KhataTransaction>? = null,
        val monthlyExpenditures: List<MonthlyExpenditure>? = null,
        val dailyExpenses: List<DailyExpense>? = null,
        val rationItems: List<RationItem>? = null
    )

    fun exportToJson(currentMonthKey: String): File {
        val now = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date())
        val data = BackupData(
            appVersion = "3.0.0",
            exportedAt = now,
            currentMonthKey = currentMonthKey,
            parties = repository.getParties(),
            khataTransactions = repository.getAllKhataTransactions(),
            monthlyExpenditures = repository.getAllMonthlyExpenditures(),
            dailyExpenses = repository.getAllDailyExpenses(),
            rationItems = repository.getAllRationItems()
        )

        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(data)

        val backupDir = File(context.cacheDir, "backups")
        if (!backupDir.exists()) backupDir.mkdirs()

        val backupFile = File(backupDir, "Ghardari_Backup_$now.json")
        backupFile.writeText(jsonString)
        return backupFile
    }

    fun shareBackup(backupFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            backupFile
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Ghardari App Backup")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Share Ghardari Backup").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    fun restoreFromUri(uri: Uri): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return false
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val gson = Gson()
            val backupData = gson.fromJson(jsonString, BackupData::class.java) ?: return false

            val safeParties = backupData.parties?.map { p ->
                PartyRecord(
                    id = p.id,
                    name = (p.name as String?).orEmpty().ifBlank { "Party" },
                    phone = (p.phone as String?).orEmpty(),
                    type = (p.type as String?).orEmpty().ifBlank { "LENDER" },
                    currentBalance = p.currentBalance,
                    updatedAt = (p.updatedAt as String?).orEmpty(),
                    notes = (p.notes as String?).orEmpty()
                )
            } ?: emptyList()

            val safeTrx = backupData.khataTransactions?.map { t ->
                KhataTransaction(
                    id = t.id,
                    partyId = t.partyId,
                    partyName = (t.partyName as String?).orEmpty(),
                    type = (t.type as String?).orEmpty().ifBlank { "DEBIT" },
                    amount = t.amount,
                    date = (t.date as String?).orEmpty(),
                    notes = (t.notes as String?).orEmpty(),
                    createdAt = if (t.createdAt > 0) t.createdAt else System.currentTimeMillis()
                )
            } ?: emptyList()

            val safeMonthly = backupData.monthlyExpenditures?.map { m ->
                MonthlyExpenditure(
                    id = m.id,
                    monthKey = (m.monthKey as String?).orEmpty(),
                    title = (m.title as String?).orEmpty().ifBlank { "Expense" },
                    amount = m.amount,
                    isPaid = m.isPaid,
                    paidDate = (m.paidDate as String?).orEmpty(),
                    notes = (m.notes as String?).orEmpty()
                )
            } ?: emptyList()

            val safeDaily = backupData.dailyExpenses?.map { d ->
                DailyExpense(
                    id = d.id,
                    date = (d.date as String?).orEmpty(),
                    monthKey = (d.monthKey as String?).orEmpty(),
                    title = (d.title as String?).orEmpty().ifBlank { "Expense" },
                    amount = d.amount,
                    paymentMethod = (d.paymentMethod as String?).orEmpty().ifBlank { "Cash" },
                    notes = (d.notes as String?).orEmpty(),
                    createdAt = if (d.createdAt > 0) d.createdAt else System.currentTimeMillis()
                )
            } ?: emptyList()

            val safeRation = backupData.rationItems?.map { r ->
                RationItem(
                    id = r.id,
                    monthKey = (r.monthKey as String?).orEmpty(),
                    name = (r.name as String?).orEmpty().ifBlank { "Item" },
                    company = (r.company as String?).orEmpty(),
                    quantity = r.quantity,
                    unit = (r.unit as String?).orEmpty().ifBlank { "piece" },
                    estimatedPrice = r.estimatedPrice,
                    actualPrice = r.actualPrice,
                    isPurchased = r.isPurchased,
                    notes = (r.notes as String?).orEmpty()
                )
            } ?: emptyList()

            repository.restoreBackupData(
                parties = safeParties,
                transactions = safeTrx,
                monthlyExp = safeMonthly,
                dailyExp = safeDaily,
                ration = safeRation
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun generateWhatsAppStatement(party: PartyRecord, transactions: List<KhataTransaction>, lang: AppLanguage): String {
        val sb = StringBuilder()
        val isSd = lang == AppLanguage.SINDHI
        val isUr = lang == AppLanguage.URDU

        if (isSd) {
            sb.append("📋 *گهرداري - کاتي جي تفصيل*\n")
            sb.append("👤 ماڻهو: *${party.name}*\n")
            sb.append("----------------------------\n")
            if (party.currentBalance == 0.0) {
                sb.append("✅ *حساب صاف ٿيل آهي (Cleared)*\n")
            } else if (party.type == "LENDER") {
                sb.append("🔴 اوهان جا ڏيڻا آهن: *روپيا ${party.currentBalance.toInt()}*\n")
            } else {
                sb.append("🟢 اوهان جا وٺڻا آهن: *روپيا ${party.currentBalance.toInt()}*\n")
            }
            sb.append("----------------------------\n")
            sb.append("تازا اندراج:\n")
            transactions.take(5).forEach {
                val sign = if (it.type == "DEBIT") "(-) ڏنا" else if (it.type == "CREDIT") "(+) ورتا" else "صاف"
                sb.append("• ${it.date}: Rs. ${it.amount.toInt()} ($sign) ${it.notes}\n")
            }
        } else if (isUr) {
            sb.append("📋 *گھرداری - کھاتہ تفصیل*\n")
            sb.append("👤 نام: *${party.name}*\n")
            sb.append("----------------------------\n")
            if (party.currentBalance == 0.0) {
                sb.append("✅ *حساب بےباق ہے (Cleared)*\n")
            } else if (party.type == "LENDER") {
                sb.append("🔴 بقایا دینے ہیں: *روپے ${party.currentBalance.toInt()}*\n")
            } else {
                sb.append("🟢 بقایا لینے ہیں: *روپے ${party.currentBalance.toInt()}*\n")
            }
            sb.append("----------------------------\n")
            sb.append("حالیہ لین دین:\n")
            transactions.take(5).forEach {
                val sign = if (it.type == "DEBIT") "(-) دیے" else if (it.type == "CREDIT") "(+) لیے" else "بےباق"
                sb.append("• ${it.date}: Rs. ${it.amount.toInt()} ($sign) ${it.notes}\n")
            }
        } else {
            sb.append("📋 *Ghardari - Ledger Statement*\n")
            sb.append("👤 Party: *${party.name}*\n")
            sb.append("----------------------------\n")
            if (party.currentBalance == 0.0) {
                sb.append("✅ *Account Cleared*\n")
            } else {
                sb.append("Current Balance: *Rs. ${party.currentBalance.toInt()}*\n")
            }
            sb.append("----------------------------\n")
            transactions.take(5).forEach {
                sb.append("• ${it.date}: Rs. ${it.amount.toInt()} (${it.type}) ${it.notes}\n")
            }
        }
        return sb.toString()
    }
}
