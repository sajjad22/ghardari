package com.ghardari.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ghardari.app.data.model.AppLanguage
import com.ghardari.app.data.model.I18n
import com.ghardari.app.data.model.KhataTransaction
import com.ghardari.app.data.model.PartyRecord
import com.ghardari.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhataDetailScreen(
    language: AppLanguage,
    party: PartyRecord,
    transactions: List<KhataTransaction>,
    onBackClick: () -> Unit,
    onAddTransaction: (type: String, amount: Double, notes: String) -> Unit,
    onClearAccount: () -> Unit,
    onDeleteParty: () -> Unit,
    onShareWhatsApp: () -> Unit
) {
    var trxTypeDialog by remember { mutableStateOf<String?>(null) } // "DEBIT" or "CREDIT"
    var showDeletePartyConfirm by remember { mutableStateOf(false) }

    val isCleared = party.currentBalance == 0.0
    val isLender = party.type == "LENDER"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = party.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onShareWhatsApp) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                    IconButton(onClick = { showDeletePartyConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Party", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDark)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp)
        ) {
            // Balance Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCleared) SlateLight else if (isLender) Color(0xFFFEF2F2) else Color(0xFFF0FDF4)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val statusText = if (isCleared) {
                            I18n.t("cleared", language)
                        } else if (isLender) {
                            I18n.t("we_owe", language)
                        } else {
                            I18n.t("owed_to_us", language)
                        }

                        Text(
                            text = statusText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isCleared) SlateMuted else if (isLender) OutflowRed else InflowGreen
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isCleared) I18n.t("cleared", language) else "Rs. ${party.currentBalance.toInt()}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCleared) SlateDark else if (isLender) OutflowRed else InflowGreen
                        )

                        if (party.phone.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = party.phone, fontSize = 12.sp, color = SlateMuted)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Fast Debit / Credit Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { trxTypeDialog = "DEBIT" },
                                colors = ButtonDefaults.buttonColors(containerColor = OutflowRed),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(I18n.t("debit", language), fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { trxTypeDialog = "CREDIT" },
                                colors = ButtonDefaults.buttonColors(containerColor = InflowGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(I18n.t("credit", language), fontWeight = FontWeight.Bold)
                            }
                        }

                        if (!isCleared) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = onClearAccount,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(I18n.t("clear_account", language), fontWeight = FontWeight.Bold, color = EmeraldDark)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "کاتي جي تاريخ (Transaction History)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Transactions History
            if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = I18n.t("no_data", language), color = SlateMuted)
                    }
                }
            } else {
                items(transactions, key = { it.id }) { trx ->
                    val isDebit = trx.type == "DEBIT"
                    val isCredit = trx.type == "CREDIT"

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val label = if (isDebit) I18n.t("debit", language) else if (isCredit) I18n.t("credit", language) else I18n.t("cleared", language)
                                Text(
                                    text = label,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDebit) OutflowRed else if (isCredit) InflowGreen else SlateDark
                                )
                                Text(
                                    text = listOfNotNull(trx.notes.takeIf { it.isNotBlank() }, trx.date).joinToString(" • "),
                                    fontSize = 11.sp,
                                    color = SlateMuted
                                )
                            }

                            Text(
                                text = if (trx.type == "CLEAR") "صاف" else "Rs. ${trx.amount.toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDebit) OutflowRed else if (isCredit) InflowGreen else SlateMuted
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog for Debit / Credit (NO alte / placeholder text!)
    if (trxTypeDialog != null) {
        val type = trxTypeDialog!!
        var amtText by remember { mutableStateOf("") }
        var noteText by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { trxTypeDialog = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (type == "DEBIT") I18n.t("debit", language) else I18n.t("credit", language),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (type == "DEBIT") OutflowRed else InflowGreen
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = amtText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) amtText = it },
                        label = { Text(I18n.t("amount", language)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text(I18n.t("notes", language)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { trxTypeDialog = null },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(I18n.t("cancel", language))
                        }

                        Button(
                            onClick = {
                                val amt = amtText.toDoubleOrNull() ?: 0.0
                                if (amt > 0) {
                                    onAddTransaction(type, amt, noteText.trim())
                                    trxTypeDialog = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (type == "DEBIT") OutflowRed else InflowGreen
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f),
                            enabled = (amtText.toDoubleOrNull() ?: 0.0) > 0
                        ) {
                            Text(I18n.t("save", language), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showDeletePartyConfirm) {
        AlertDialog(
            onDismissRequest = { showDeletePartyConfirm = false },
            title = { Text(I18n.t("delete", language)) },
            text = { Text("ڇا توهان هن کاتي کي ختم ڪرڻ چاهيو ٿا؟") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteParty()
                        showDeletePartyConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OutflowRed)
                ) {
                    Text(I18n.t("delete", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePartyConfirm = false }) {
                    Text(I18n.t("cancel", language))
                }
            }
        )
    }
}
