package com.ghardari.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.ghardari.app.ui.screens.*
import com.ghardari.app.ui.theme.EmeraldBackground
import com.ghardari.app.ui.theme.GhardariTheme
import com.ghardari.app.ui.viewmodel.ExpensesViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ExpensesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val language by viewModel.language.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val currentMonthKey by viewModel.currentMonthKey.collectAsState()
            val summary by viewModel.summary.collectAsState()
            val parties by viewModel.parties.collectAsState()
            val selectedParty by viewModel.selectedParty.collectAsState()
            val partyTransactions by viewModel.partyTransactions.collectAsState()
            val monthlyExpenditures by viewModel.monthlyExpenditures.collectAsState()
            val dailyExpenses by viewModel.dailyExpenses.collectAsState()
            val rationItems by viewModel.rationItems.collectAsState()
            val customCards by viewModel.customCards.collectAsState()

            // Dynamic RTL layout direction based on language
            val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

            // Android Hardware/Gesture Back button handling
            BackHandler(enabled = currentScreen != "HOME") {
                if (currentScreen == "KHATA_DETAIL") {
                    viewModel.navigateTo("KHATA")
                } else {
                    viewModel.navigateTo("HOME")
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                GhardariTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(EmeraldBackground)
                    ) {
                        when (currentScreen) {
                            "HOME" -> HomeScreen(
                                language = language,
                                summary = summary,
                                customCards = customCards,
                                onCardClick = { screenTag -> viewModel.navigateTo(screenTag) }
                            )

                            "KHATA" -> KhataScreen(
                                language = language,
                                parties = parties,
                                onBackClick = { viewModel.navigateTo("HOME") },
                                onPartyClick = { party -> viewModel.selectParty(party) },
                                onAddParty = { name, phone, type, bal, notes ->
                                    viewModel.addParty(name, phone, type, bal, notes)
                                }
                            )

                            "KHATA_DETAIL" -> {
                                if (selectedParty != null) {
                                    KhataDetailScreen(
                                        language = language,
                                        party = selectedParty!!,
                                        transactions = partyTransactions,
                                        onBackClick = { viewModel.navigateTo("KHATA") },
                                        onAddTransaction = { type, amount, notes ->
                                            viewModel.addKhataTransaction(selectedParty!!.id, selectedParty!!.name, type, amount, notes)
                                        },
                                        onClearAccount = {
                                            viewModel.clearPartyAccount(selectedParty!!.id, selectedParty!!.name)
                                        },
                                        onDeleteParty = {
                                            viewModel.deleteParty(selectedParty!!.id)
                                        },
                                        onShareWhatsApp = {
                                            viewModel.sharePartyViaWhatsApp(selectedParty!!)
                                        }
                                    )
                                } else {
                                    viewModel.navigateTo("KHATA")
                                }
                            }

                            "MONTHLY" -> MonthlyBillsScreen(
                                language = language,
                                monthKey = currentMonthKey,
                                expenditures = monthlyExpenditures,
                                onBackClick = { viewModel.navigateTo("HOME") },
                                onTogglePaid = { id, isPaid ->
                                    viewModel.toggleMonthlyExpenditurePaid(id, isPaid)
                                },
                                onAddExpenditure = { title, amount, notes ->
                                    viewModel.addMonthlyExpenditure(title, amount, notes)
                                },
                                onDeleteExpenditure = { id ->
                                    viewModel.deleteMonthlyExpenditure(id)
                                },
                                onCopyPreviousMonthClick = {
                                    viewModel.copyPreviousMonthExpenditures()
                                }
                            )

                            "DAILY" -> DailyExpensesScreen(
                                language = language,
                                monthKey = currentMonthKey,
                                expenses = dailyExpenses,
                                onBackClick = { viewModel.navigateTo("HOME") },
                                onAddDailyExpense = { title, amount, paymentMethod, notes ->
                                    viewModel.addDailyExpense(title, amount, paymentMethod, notes)
                                },
                                onDeleteExpense = { id ->
                                    viewModel.deleteDailyExpense(id)
                                }
                            )

                            "RATION" -> RationScreen(
                                language = language,
                                monthKey = currentMonthKey,
                                items = rationItems,
                                onBackClick = { viewModel.navigateTo("HOME") },
                                onAddItem = { name, qty, unit, estPrice, notes ->
                                    viewModel.addRationItem(name, qty, unit, estPrice, notes)
                                },
                                onTogglePurchased = { id, isPurchased, actualPrice ->
                                    viewModel.toggleRationPurchased(id, isPurchased, actualPrice)
                                },
                                onDeleteItem = { id ->
                                    viewModel.deleteRationItem(id)
                                }
                            )

                            "SETTINGS" -> SettingsScreen(
                                language = language,
                                customCards = customCards,
                                onBackClick = { viewModel.navigateTo("HOME") },
                                onLanguageChange = { lang -> viewModel.setLanguage(lang) },
                                onAddCustomCard = { title, targetType, targetId, icon, color ->
                                    viewModel.addCustomCard(title, targetType, targetId, icon, color)
                                },
                                onDeleteCustomCard = { id -> viewModel.deleteCustomCard(id) },
                                onExportBackupClick = { viewModel.exportBackup() }
                            )
                        }
                    }
                }
            }
        }
    }
}
