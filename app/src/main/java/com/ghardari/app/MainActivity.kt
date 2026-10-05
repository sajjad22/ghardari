package com.ghardari.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghardari.app.data.model.AppLanguage
import com.ghardari.app.data.model.I18n
import com.ghardari.app.ui.dialogs.*
import com.ghardari.app.ui.screens.*
import com.ghardari.app.ui.theme.*
import com.ghardari.app.ui.viewmodel.ExpensesViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private val viewModel: ExpensesViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val language by viewModel.language.collectAsState()
            val currentTab by viewModel.currentTab.collectAsState()
            val currentMonthKey by viewModel.currentMonthKey.collectAsState()
            val summary by viewModel.summary.collectAsState()
            val transactions by viewModel.transactions.collectAsState()
            val categories by viewModel.categories.collectAsState()
            val parties by viewModel.parties.collectAsState()
            val rationItems by viewModel.rationItems.collectAsState()

            var showQuickAddDialog by remember { mutableStateOf(false) }
            var showAddPersonDialog by remember { mutableStateOf(false) }
            var showAddCategoryDialog by remember { mutableStateOf(false) }
            var showAddRationDialog by remember { mutableStateOf(false) }

            // Dynamic RTL layout direction based on language
            val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                GhardariTheme {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Column {
                                        Text(
                                            text = I18n.t("app_title", language),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = I18n.t("app_subtitle", language),
                                            fontSize = 11.sp,
                                            color = EmeraldLight
                                        )
                                    }
                                },
                                actions = {
                                    // Month navigation
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        IconButton(
                                            onClick = { shiftMonth(-1) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = Color.White)
                                        }

                                        Text(
                                            text = currentMonthKey,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )

                                        IconButton(
                                            onClick = { shiftMonth(1) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Quick Language Toggle
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color.White.copy(alpha = 0.25f),
                                        modifier = Modifier.clickable {
                                            val nextLang = when (language) {
                                                AppLanguage.SINDHI -> AppLanguage.URDU
                                                AppLanguage.URDU -> AppLanguage.ENGLISH
                                                AppLanguage.ENGLISH -> AppLanguage.SINDHI
                                            }
                                            viewModel.setLanguage(nextLang)
                                        }
                                    ) {
                                        Text(
                                            text = language.displayName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = EmeraldDark
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = Color.White,
                                tonalElevation = 8.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == 0,
                                    onClick = { viewModel.setTab(0) },
                                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
                                    label = { Text(I18n.t("tab_expenses", language), fontSize = 11.sp, fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = EmeraldPrimary,
                                        selectedTextColor = EmeraldPrimary,
                                        indicatorColor = EmeraldLight
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentTab == 1,
                                    onClick = { viewModel.setTab(1) },
                                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                                    label = { Text(I18n.t("tab_khata", language), fontSize = 11.sp, fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = EmeraldPrimary,
                                        selectedTextColor = EmeraldPrimary,
                                        indicatorColor = EmeraldLight
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentTab == 2,
                                    onClick = { viewModel.setTab(2) },
                                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                                    label = { Text(I18n.t("tab_ration", language), fontSize = 11.sp, fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = EmeraldPrimary,
                                        selectedTextColor = EmeraldPrimary,
                                        indicatorColor = EmeraldLight
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentTab == 3,
                                    onClick = { viewModel.setTab(3) },
                                    icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
                                    label = { Text(I18n.t("tab_reports", language), fontSize = 11.sp, fontWeight = if (currentTab == 3) FontWeight.Bold else FontWeight.Normal) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = EmeraldPrimary,
                                        selectedTextColor = EmeraldPrimary,
                                        indicatorColor = EmeraldLight
                                    )
                                )
                            }
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = { showQuickAddDialog = true },
                                containerColor = EmeraldPrimary,
                                contentColor = Color.White,
                                shape = CircleShape,
                                modifier = Modifier.size(62.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Quick Add", modifier = Modifier.size(30.dp))
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(EmeraldBackground)
                        ) {
                            when (currentTab) {
                                0 -> MonthlyExpensesScreen(
                                    language = language,
                                    summary = summary,
                                    transactions = transactions,
                                    categories = categories,
                                    onAddCustomCategoryClick = { showAddCategoryDialog = true },
                                    onDeleteTransaction = { viewModel.deleteTransaction(it) }
                                )
                                1 -> KhataScreen(
                                    language = language,
                                    parties = parties,
                                    summary = summary,
                                    onAddPersonClick = { showAddPersonDialog = true },
                                    onClearAccountClick = { viewModel.clearPartyAccount(it.id, it.name) },
                                    onShareWhatsAppClick = { viewModel.sharePartyViaWhatsApp(it) }
                                )
                                2 -> RationScreen(
                                    language = language,
                                    rationItems = rationItems,
                                    summary = summary,
                                    onAddItemClick = { showAddRationDialog = true },
                                    onTogglePurchased = { id, done, price -> viewModel.toggleRationPurchased(id, done, price) },
                                    onDeleteItem = { viewModel.deleteRationItem(it) },
                                    onAddToMonthlyExpenses = { viewModel.addRationTotalToMonthlyExpenses() }
                                )
                                3 -> ReportsScreen(
                                    language = language,
                                    summary = summary,
                                    onLanguageChange = { viewModel.setLanguage(it) },
                                    onExportBackupClick = { viewModel.exportBackup() }
                                )
                            }
                        }
                    }

                    // Dialogs
                    if (showQuickAddDialog) {
                        QuickAddDialog(
                            language = language,
                            categories = categories,
                            parties = parties,
                            onDismiss = { showQuickAddDialog = false },
                            onSave = { pId, pName, cId, cKey, type, amount, payMethod, notes ->
                                viewModel.addTransaction(pId, pName, cId, cKey, type, amount, payMethod, notes)
                            }
                        )
                    }

                    if (showAddPersonDialog) {
                        AddPersonDialog(
                            language = language,
                            onDismiss = { showAddPersonDialog = false },
                            onSave = { name, phone, type, bal, notes ->
                                viewModel.addParty(name, phone, type, bal, notes)
                            }
                        )
                    }

                    if (showAddCategoryDialog) {
                        AddCategoryDialog(
                            language = language,
                            onDismiss = { showAddCategoryDialog = false },
                            onSave = { nameSd, nameUr, nameEn, icon, color ->
                                viewModel.addCustomCategory(nameSd, nameUr, nameEn, icon, color)
                            }
                        )
                    }

                    if (showAddRationDialog) {
                        AddRationItemDialog(
                            language = language,
                            onDismiss = { showAddRationDialog = false },
                            onSave = { name, qty, unit, estPrice, notes ->
                                viewModel.addRationItem(name, name, name, qty, unit, estPrice, notes)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun shiftMonth(delta: Int) {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        try {
            val date = sdf.parse(viewModel.currentMonthKey.value) ?: Date()
            val cal = Calendar.getInstance().apply {
                time = date
                add(Calendar.MONTH, delta)
            }
            viewModel.setMonthKey(sdf.format(cal.time))
        } catch (_: Exception) {}
    }
}
