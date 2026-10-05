package com.ghardari.app.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.ghardari.app.data.model.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupManager(private val context: Context, private val repository: ExpensesRepository) {

    data class BackupData(
        val appVersion: String = "1.0.0",
        val exportedAt: String,
        val categories: List<CategoryRecord>,
        val parties: List<PartyRecord>,
        val currentMonthKey: String,
        val transactions: List<TransactionRecord>,
        val rationItems: List<RationItem>
    )

    fun exportToJson(currentMonthKey: String): File {
        val now = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date())
        val data = BackupData(
            exportedAt = now,
            categories = repository.getCategories(),
            parties = repository.getParties(),
            currentMonthKey = currentMonthKey,
            transactions = repository.getTransactionsByMonth(currentMonthKey),
            rationItems = repository.getRationItems(currentMonthKey)
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

    fun generateWhatsAppStatement(party: PartyRecord, transactions: List<TransactionRecord>, lang: AppLanguage): String {
        val sb = StringBuilder()
        val isSd = lang == AppLanguage.SINDHI
        val isUr = lang == AppLanguage.URDU

        if (isSd) {
            sb.append("📋 *گهرڌاري - کاتي جي تفصيل*\n")
            sb.append("👤 پارٽي/ماڻهو: *${party.name}*\n")
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
                val sign = if (it.type == "KHATA_GIVE") "(-) ڏنا" else if (it.type == "KHATA_RECEIVE") "(+) ورتا" else "صاف"
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
                val sign = if (it.type == "KHATA_GIVE") "(-) دیے" else if (it.type == "KHATA_RECEIVE") "(+) لیے" else "بےباق"
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
