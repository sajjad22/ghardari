package com.ghardari.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ghardari.app.data.model.AppLanguage
import com.ghardari.app.data.model.I18n
import com.ghardari.app.data.model.MonthlyExpenditure
import com.ghardari.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyBillsScreen(
    language: AppLanguage,
    monthKey: String,
    expenditures: List<MonthlyExpenditure>,
    onBackClick: () -> Unit,
    onTogglePaid: (id: Long, isPaid: Boolean) -> Unit,
    onAddExpenditure: (title: String, amount: Double, notes: String) -> Unit,
    onUpdateExpenditure: (id: Long, title: String, amount: Double, notes: String) -> Unit,
    onDeleteExpenditure: (id: Long) -> Unit,
    onCopyPreviousMonthClick: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<MonthlyExpenditure?>(null) }
    var showConfirmCopyDialog by remember { mutableStateOf(false) }

    val totalAmount = expenditures.sumOf { it.amount }
    val paidAmount = expenditures.filter { it.isPaid }.sumOf { it.amount }
    val unpaidAmount = totalAmount - paidAmount

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = I18n.t("card_monthly", language) + " ($monthKey)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showConfirmCopyDialog = true }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Last Month", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Bill")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            // Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "ڪل ماهوار خرچ (Total)", fontSize = 12.sp, color = SlateMuted)
                                Text(
                                    text = "Rs. ${totalAmount.toInt()}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E40AF)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = InflowGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "ادا: ${paidAmount.toInt()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = InflowGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = OutflowRed.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "باقي: ${unpaidAmount.toInt()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OutflowRed,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Copy Previous Month Action
                        OutlinedButton(
                            onClick = { showConfirmCopyDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2563EB))
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = I18n.t("copy_previous_month", language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Todo Items List
            if (expenditures.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = I18n.t("no_data", language),
                            color = SlateMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(expenditures, key = { it.id }) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isPaid) Color(0xFFF0FDF4) else Color.White
                        ),
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = item.isPaid,
                                    onCheckedChange = { onTogglePaid(item.id, it) },
                                    colors = CheckboxDefaults.colors(checkedColor = InflowGreen)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Column {
                                    Text(
                                        text = item.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.isPaid) SlateMuted else SlateDark,
                                        textDecoration = if (item.isPaid) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                    val statusNote = if (item.isPaid) {
                                        I18n.t("paid", language) + (if (item.paidDate.isNotBlank()) " (${item.paidDate})" else "")
                                    } else {
                                        I18n.t("unpaid", language)
                                    }
                                    Text(
                                        text = statusNote + (if (item.notes.isNotBlank()) " • ${item.notes}" else ""),
                                        fontSize = 11.sp,
                                        color = if (item.isPaid) InflowGreen else OutflowRed,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Rs. ${item.amount.toInt()}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.isPaid) InflowGreen else SlateDark
                                )

                                IconButton(
                                    onClick = { editingItem = item },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteExpenditure(item.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.LightGray,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Add Monthly Expenditure
    if (showAddDialog) {
        var titleText by remember { mutableStateOf("") }
        var amountText by remember { mutableStateOf("") }
        var notesText by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = I18n.t("add_monthly_item", language),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        label = { Text(I18n.t("expense_title", language)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) amountText = it },
                        label = { Text(I18n.t("amount", language)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
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
                            onClick = { showAddDialog = false },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(I18n.t("cancel", language))
                        }

                        Button(
                            onClick = {
                                val amt = amountText.toDoubleOrNull() ?: 0.0
                                if (titleText.isNotBlank() && amt > 0) {
                                    onAddExpenditure(titleText.trim(), amt, notesText.trim())
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f),
                            enabled = titleText.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0
                        ) {
                            Text(I18n.t("save", language), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Dialog: Edit Monthly Expenditure (e.g. modify amount of copied bill)
    if (editingItem != null) {
        val target = editingItem!!
        var titleText by remember { mutableStateOf(target.title) }
        var amountText by remember { mutableStateOf(if (target.amount % 1.0 == 0.0) target.amount.toInt().toString() else target.amount.toString()) }
        var notesText by remember { mutableStateOf(target.notes) }

        Dialog(onDismissRequest = { editingItem = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = I18n.t("edit_monthly_item", language),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E40AF)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        label = { Text(I18n.t("expense_title", language)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) amountText = it },
                        label = { Text(I18n.t("amount", language)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
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
                            onClick = { editingItem = null },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(I18n.t("cancel", language))
                        }

                        Button(
                            onClick = {
                                val amt = amountText.toDoubleOrNull() ?: 0.0
                                if (titleText.isNotBlank() && amt > 0) {
                                    onUpdateExpenditure(target.id, titleText.trim(), amt, notesText.trim())
                                    editingItem = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f),
                            enabled = titleText.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0
                        ) {
                            Text(I18n.t("save", language), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Dialog: Confirm Copy from Previous Month
    if (showConfirmCopyDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmCopyDialog = false },
            title = { Text(I18n.t("copy_previous_month", language)) },
            text = { Text(I18n.t("copy_confirm_msg", language)) },
            confirmButton = {
                Button(
                    onClick = {
                        onCopyPreviousMonthClick()
                        showConfirmCopyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text(I18n.t("save", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmCopyDialog = false }) {
                    Text(I18n.t("cancel", language))
                }
            }
        )
    }
}
