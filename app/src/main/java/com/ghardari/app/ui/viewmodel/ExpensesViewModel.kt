package com.ghardari.app.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ghardari.app.data.db.DatabaseHelper
import com.ghardari.app.data.model.*
import com.ghardari.app.data.repository.BackupManager
import com.ghardari.app.data.repository.ExpensesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpensesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpensesRepository
    private val backupManager: BackupManager
    private val prefs = application.getSharedPreferences("ghardari_prefs", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(AppLanguage.SINDHI)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _currentMonthKey = MutableStateFlow(
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    )
    val currentMonthKey: StateFlow<String> = _currentMonthKey.asStateFlow()

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _summary = MutableStateFlow(MonthSummary(_currentMonthKey.value))
    val summary: StateFlow<MonthSummary> = _summary.asStateFlow()

    private val _transactions = MutableStateFlow<List<TransactionRecord>>(emptyList())
    val transactions: StateFlow<List<TransactionRecord>> = _transactions.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryRecord>>(emptyList())
    val categories: StateFlow<List<CategoryRecord>> = _categories.asStateFlow()

    private val _parties = MutableStateFlow<List<PartyRecord>>(emptyList())
    val parties: StateFlow<List<PartyRecord>> = _parties.asStateFlow()

    private val _rationItems = MutableStateFlow<List<RationItem>>(emptyList())
    val rationItems: StateFlow<List<RationItem>> = _rationItems.asStateFlow()

    init {
        val dbHelper = DatabaseHelper(application)
        repository = ExpensesRepository(dbHelper)
        backupManager = BackupManager(application, repository)

        val savedLangCode = prefs.getString("language", AppLanguage.SINDHI.code)
        _language.value = AppLanguage.values().find { it.code == savedLangCode } ?: AppLanguage.SINDHI

        refreshAll()
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        prefs.edit().putString("language", lang.code).apply()
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun setMonthKey(monthKey: String) {
        _currentMonthKey.value = monthKey
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            val mKey = _currentMonthKey.value
            _categories.value = repository.getCategories()
            _parties.value = repository.getParties()
            _transactions.value = repository.getTransactionsByMonth(mKey)
            _rationItems.value = repository.getRationItems(mKey)
            _summary.value = repository.getMonthSummary(mKey)
        }
    }

    // 1-Click Fast Transaction Add
    fun addTransaction(
        partyId: Long?,
        partyName: String,
        categoryId: Long?,
        categoryKey: String,
        type: String,
        amount: Double,
        paymentMethod: String,
        notes: String
    ) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        repository.addTransaction(
            partyId = partyId,
            partyName = partyName,
            categoryId = categoryId,
            categoryKey = categoryKey,
            type = type,
            amount = amount,
            date = today,
            monthKey = _currentMonthKey.value,
            paymentMethod = paymentMethod,
            status = "CLEARED",
            notes = notes
        )
        refreshAll()
    }

    fun deleteTransaction(id: Long) {
        repository.deleteTransaction(id)
        refreshAll()
    }

    // Party / Khata
    fun addParty(name: String, phone: String, type: String, initialBalance: Double, notes: String) {
        repository.addParty(name, phone, type, initialBalance, notes)
        refreshAll()
    }

    fun clearPartyAccount(partyId: Long, partyName: String) {
        repository.clearPartyAccount(partyId, partyName)
        refreshAll()
    }

    fun sharePartyViaWhatsApp(party: PartyRecord) {
        val partyTrx = repository.getTransactionsByParty(party.id)
        val text = backupManager.generateWhatsAppStatement(party, partyTrx, _language.value)
        val context = getApplication<Application>()
        try {
            val sendIntent = Intent(Intent.ACTION_VIEW).apply {
                val url = if (party.phone.isNotBlank()) {
                    val phoneClean = party.phone.replace("+", "").replace("-", "").trim()
                    "https://api.whatsapp.com/send?phone=$phoneClean&text=${URLEncoder.encode(text, "UTF-8")}"
                } else {
                    "https://api.whatsapp.com/send?text=${URLEncoder.encode(text, "UTF-8")}"
                }
                data = Uri.parse(url)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(shareIntent, "Share Ledger").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        }
    }

    // Custom Category
    fun addCustomCategory(nameSd: String, nameUr: String, nameEn: String, icon: String, color: String) {
        repository.addCustomCategory(nameSd, nameUr, nameEn, icon, color)
        refreshAll()
    }

    fun deleteCategory(id: Long) {
        repository.deleteCategory(id)
        refreshAll()
    }

    // Ration Operations
    fun addRationItem(nameSd: String, nameUr: String, nameEn: String, qty: Double, unit: String, estPrice: Double, notes: String) {
        repository.addRationItem(_currentMonthKey.value, nameSd, nameUr, nameEn, qty, unit, estPrice, 0.0, false, notes)
        refreshAll()
    }

    fun toggleRationPurchased(id: Long, isPurchased: Boolean, actualPrice: Double) {
        repository.updateRationPurchased(id, isPurchased, actualPrice)
        refreshAll()
    }

    fun deleteRationItem(id: Long) {
        repository.deleteRationItem(id)
        refreshAll()
    }

    fun addRationTotalToMonthlyExpenses() {
        val items = _rationItems.value
        val totalSpent = items.filter { it.isPurchased }.sumOf {
            if (it.actualPrice > 0) it.actualPrice else it.estimatedPrice
        }
        if (totalSpent <= 0) {
            Toast.makeText(getApplication(), "ڪو به راشن ورتل ناهي (No purchased items)", Toast.LENGTH_SHORT).show()
            return
        }

        val noteText = when (_language.value) {
            AppLanguage.SINDHI -> "ماهوار راشن ۽ سودا سلف خريداري جو ڪل بل"
            AppLanguage.URDU -> "ماہانہ راشن اور سودا سلف خریداری کا کل بل"
            AppLanguage.ENGLISH -> "Monthly Grocery & Ration Total Bill"
        }

        addTransaction(
            partyId = null,
            partyName = "",
            categoryId = null,
            categoryKey = "grocery",
            type = "OUTFLOW",
            amount = totalSpent,
            paymentMethod = "Cash",
            notes = noteText
        )

        Toast.makeText(getApplication(), I18n.t("ration_added_success", _language.value), Toast.LENGTH_LONG).show()
    }

    // Backup
    fun exportBackup() {
        try {
            val file = backupManager.exportToJson(_currentMonthKey.value)
            backupManager.shareBackup(file)
        } catch (e: Exception) {
            Toast.makeText(getApplication(), "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
