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
import com.ghardari.app.data.repository.PrintReportHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

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

    // Screen navigation: "HOME", "KHATA", "KHATA_DETAIL", "MONTHLY", "DAILY", "RATION", "REPORTS", "HISTORY", "SETTINGS"
    private val _currentScreen = MutableStateFlow("HOME")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _selectedParty = MutableStateFlow<PartyRecord?>(null)
    val selectedParty: StateFlow<PartyRecord?> = _selectedParty.asStateFlow()

    private val _summary = MutableStateFlow(DashboardSummary(_currentMonthKey.value))
    val summary: StateFlow<DashboardSummary> = _summary.asStateFlow()

    private val _parties = MutableStateFlow<List<PartyRecord>>(emptyList())
    val parties: StateFlow<List<PartyRecord>> = _parties.asStateFlow()

    private val _partyTransactions = MutableStateFlow<List<KhataTransaction>>(emptyList())
    val partyTransactions: StateFlow<List<KhataTransaction>> = _partyTransactions.asStateFlow()

    private val _monthlyExpenditures = MutableStateFlow<List<MonthlyExpenditure>>(emptyList())
    val monthlyExpenditures: StateFlow<List<MonthlyExpenditure>> = _monthlyExpenditures.asStateFlow()

    private val _dailyExpenses = MutableStateFlow<List<DailyExpense>>(emptyList())
    val dailyExpenses: StateFlow<List<DailyExpense>> = _dailyExpenses.asStateFlow()

    private val _rationItems = MutableStateFlow<List<RationItem>>(emptyList())
    val rationItems: StateFlow<List<RationItem>> = _rationItems.asStateFlow()

    // History / Archive States
    private val _recordedMonths = MutableStateFlow<List<String>>(emptyList())
    val recordedMonths: StateFlow<List<String>> = _recordedMonths.asStateFlow()

    private val _historySelectedMonth = MutableStateFlow(_currentMonthKey.value)
    val historySelectedMonth: StateFlow<String> = _historySelectedMonth.asStateFlow()

    private val _historyMonthlyExpenditures = MutableStateFlow<List<MonthlyExpenditure>>(emptyList())
    val historyMonthlyExpenditures: StateFlow<List<MonthlyExpenditure>> = _historyMonthlyExpenditures.asStateFlow()

    private val _historyDailyExpenses = MutableStateFlow<List<DailyExpense>>(emptyList())
    val historyDailyExpenses: StateFlow<List<DailyExpense>> = _historyDailyExpenses.asStateFlow()

    private val _historyRationItems = MutableStateFlow<List<RationItem>>(emptyList())
    val historyRationItems: StateFlow<List<RationItem>> = _historyRationItems.asStateFlow()

    private val _historySummary = MutableStateFlow(DashboardSummary(_currentMonthKey.value))
    val historySummary: StateFlow<DashboardSummary> = _historySummary.asStateFlow()

    init {
        val dbHelper = DatabaseHelper(application)
        repository = ExpensesRepository(dbHelper)
        backupManager = BackupManager(application, repository)

        val savedLang = prefs.getString("language", AppLanguage.SINDHI.code)
        _language.value = AppLanguage.values().find { it.code == savedLang } ?: AppLanguage.SINDHI

        refreshAll()
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        prefs.edit().putString("language", lang.code).apply()
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
        if (screen != "KHATA_DETAIL") {
            _selectedParty.value = null
        }
        if (screen == "HISTORY") {
            loadHistoryMonths()
        }
    }

    fun setMonthKey(monthKey: String) {
        _currentMonthKey.value = monthKey
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            val mKey = _currentMonthKey.value
            _parties.value = repository.getParties()
            _monthlyExpenditures.value = repository.getMonthlyExpenditures(mKey)
            _dailyExpenses.value = repository.getDailyExpenses(mKey)
            _rationItems.value = repository.getRationItems(mKey)
            _summary.value = repository.getDashboardSummary(mKey)
            _recordedMonths.value = repository.getAllRecordedMonths()

            val currentP = _selectedParty.value
            if (currentP != null) {
                _partyTransactions.value = repository.getPartyTransactions(currentP.id)
                _selectedParty.value = repository.getParties().find { it.id == currentP.id }
            }
        }
    }

    // 1. Udhar Khata
    fun selectParty(party: PartyRecord) {
        _selectedParty.value = party
        _partyTransactions.value = repository.getPartyTransactions(party.id)
        _currentScreen.value = "KHATA_DETAIL"
    }

    fun addParty(name: String, phone: String, type: String, initialBalance: Double, notes: String) {
        repository.addParty(name, phone, type, initialBalance, notes)
        refreshAll()
    }

    fun addKhataTransaction(partyId: Long, partyName: String, type: String, amount: Double, notes: String) {
        repository.addKhataTransaction(partyId, partyName, type, amount, notes)
        refreshAll()
    }

    fun clearPartyAccount(partyId: Long, partyName: String) {
        repository.clearPartyAccount(partyId, partyName)
        refreshAll()
    }

    fun deleteParty(partyId: Long) {
        repository.deleteParty(partyId)
        if (_selectedParty.value?.id == partyId) {
            _selectedParty.value = null
            _currentScreen.value = "KHATA"
        }
        refreshAll()
    }

    fun sharePartyViaWhatsApp(party: PartyRecord) {
        val list = repository.getPartyTransactions(party.id)
        val text = backupManager.generateWhatsAppStatement(party, list, _language.value)
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
        } catch (_: Exception) {
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

    // 2. Monthly House Expenditures (Fixed / Todo)
    fun addMonthlyExpenditure(title: String, amount: Double, notes: String) {
        repository.addMonthlyExpenditure(_currentMonthKey.value, title, amount, notes)
        refreshAll()
    }

    fun updateMonthlyExpenditure(id: Long, title: String, amount: Double, notes: String) {
        repository.updateMonthlyExpenditure(id, title, amount, notes)
        refreshAll()
    }

    fun toggleMonthlyExpenditurePaid(id: Long, isPaid: Boolean) {
        repository.toggleMonthlyExpenditurePaid(id, isPaid)
        refreshAll()
    }

    fun deleteMonthlyExpenditure(id: Long) {
        repository.deleteMonthlyExpenditure(id)
        refreshAll()
    }

    // Copy previous month's expenditures into this month (marked as UNPAID fresh)
    fun copyPreviousMonthExpenditures() {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        try {
            val date = sdf.parse(_currentMonthKey.value) ?: Date()
            val cal = Calendar.getInstance().apply {
                time = date
                add(Calendar.MONTH, -1)
            }
            val prevMonthKey = sdf.format(cal.time)
            val copied = repository.copyMonthlyExpendituresFromPreviousMonth(prevMonthKey, _currentMonthKey.value)
            repository.copyRationFromPreviousMonth(prevMonthKey, _currentMonthKey.value)
            refreshAll()
            if (copied > 0) {
                Toast.makeText(getApplication(), I18n.t("copy_success", _language.value), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(getApplication(), "گذريل مهيني ($prevMonthKey) ۾ ڪي به خرچ نه مليا", Toast.LENGTH_SHORT).show()
            }
        } catch (_: Exception) {}
    }

    // 3. Daily Expenses Log
    fun addDailyExpense(title: String, amount: Double, paymentMethod: String, notes: String) {
        repository.addDailyExpense(title, amount, paymentMethod, notes)
        refreshAll()
    }

    fun deleteDailyExpense(id: Long) {
        repository.deleteDailyExpense(id)
        refreshAll()
    }

    // 4. Monthly Ration / Grocery Checklist
    fun addRationItem(name: String, company: String, qty: Double, unit: String, estPrice: Double, notes: String) {
        repository.addRationItem(_currentMonthKey.value, name, company, qty, unit, estPrice, notes)
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

    // Print & Share Shopkeeper List
    fun printShopkeeperList(context: Context) {
        PrintReportHelper.printShopkeeperDocument(context, _rationItems.value, _currentMonthKey.value, _language.value)
    }

    fun shareShopkeeperList(context: Context) {
        val text = PrintReportHelper.generateShopkeeperText(_rationItems.value, _currentMonthKey.value, _language.value)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(sendIntent, "Share Grocery List").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    // History / Archive Operations
    fun loadHistoryMonths() {
        viewModelScope.launch {
            val months = repository.getAllRecordedMonths()
            _recordedMonths.value = months
            if (months.isNotEmpty()) {
                selectHistoryMonth(months.first())
            }
        }
    }

    fun selectHistoryMonth(monthKey: String) {
        _historySelectedMonth.value = monthKey
        viewModelScope.launch {
            _historyMonthlyExpenditures.value = repository.getMonthlyExpenditures(monthKey)
            _historyDailyExpenses.value = repository.getDailyExpenses(monthKey)
            _historyRationItems.value = repository.getRationItems(monthKey)
            _historySummary.value = repository.getDashboardSummary(monthKey)
        }
    }

    // Backup & Restore
    fun exportBackup() {
        try {
            val file = backupManager.exportToJson(_currentMonthKey.value)
            backupManager.shareBackup(file)
        } catch (e: Exception) {
            Toast.makeText(getApplication(), "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun restoreBackup(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = backupManager.restoreFromUri(uri)
                withContext(Dispatchers.Main) {
                    if (success) {
                        refreshAll()
                        Toast.makeText(getApplication(), I18n.t("restore_success", _language.value), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(getApplication(), I18n.t("restore_failed", _language.value), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Restore error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
