package com.ghardari.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.ghardari.app.data.model.RationItem
import com.ghardari.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RationScreen(
    language: AppLanguage,
    monthKey: String,
    items: List<RationItem>,
    onBackClick: () -> Unit,
    onAddItem: (name: String, qty: Double, unit: String, estPrice: Double, notes: String) -> Unit,
    onTogglePurchased: (id: Long, isPurchased: Boolean, actualPrice: Double) -> Unit,
    onDeleteItem: (id: Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var priceDialogItem by remember { mutableStateOf<RationItem?>(null) }
    var enteredPriceText by remember { mutableStateOf("") }

    val totalBoughtCost = items.filter { it.isPurchased }.sumOf { if (it.actualPrice > 0) it.actualPrice else it.estimatedPrice }
    val boughtCount = items.count { it.isPurchased }
    val totalCount = items.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = I18n.t("card_ration", language) + " ($monthKey)",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PurpleAccent,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
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
            // Header Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "ڪل خرچ ٿيل رقم", fontSize = 12.sp, color = SlateMuted)
                            Text(
                                text = "Rs. ${totalBoughtCost.toInt()}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleAccent
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PurpleAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$boughtCount / $totalCount " + I18n.t("bought", language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleAccent,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(items, key = { it.id }) { item ->
                val isDone = item.isPurchased

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDone) Color(0xFFF8FAFC) else Color.White
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
                                checked = isDone,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        enteredPriceText = if (item.actualPrice > 0) item.actualPrice.toInt().toString() else ""
                                        priceDialogItem = item
                                    } else {
                                        onTogglePurchased(item.id, false, 0.0)
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = PurpleAccent)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = item.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone) SlateMuted else SlateDark,
                                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                                )

                                val qtyDisplay = if (item.quantity % 1.0 == 0.0) "${item.quantity.toInt()} ${item.unit}" else "${item.quantity} ${item.unit}"
                                Text(
                                    text = qtyDisplay + (if (item.notes.isNotBlank()) " • ${item.notes}" else ""),
                                    fontSize = 12.sp,
                                    color = SlateMuted
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (item.actualPrice > 0 || item.estimatedPrice > 0) {
                                val priceToShow = if (item.actualPrice > 0) item.actualPrice else item.estimatedPrice
                                Text(
                                    text = "Rs. ${priceToShow.toInt()}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone) PurpleAccent else SlateMuted
                                )
                            }

                            IconButton(
                                onClick = { onDeleteItem(item.id) },
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

    // Add Ration Item Dialog (NO alte / placeholder text!)
    if (showAddDialog) {
        var nameText by remember { mutableStateOf("") }
        var qtyText by remember { mutableStateOf("1") }
        var selectedUnit by remember { mutableStateOf("kg") }
        var estPriceText by remember { mutableStateOf("") }
        var notesText by remember { mutableStateOf("") }

        val units = listOf("kg", "gm", "liter", "bottle", "packet", "piece")

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = I18n.t("add_ration_item", language),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleAccent
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nameText,
                        onValueChange = { nameText = it },
                        label = { Text(I18n.t("item_name", language)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = qtyText,
                            onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) qtyText = it },
                            label = { Text(I18n.t("quantity", language)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = estPriceText,
                            onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) estPriceText = it },
                            label = { Text(I18n.t("est_price", language)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = I18n.t("unit", language), fontSize = 12.sp, color = SlateMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        units.forEach { unit ->
                            val isSelected = selectedUnit == unit
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) PurpleAccent else SlateLight,
                                contentColor = if (isSelected) Color.White else SlateDark,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedUnit = unit }
                            ) {
                                Text(
                                    text = unit,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier
                                        .padding(vertical = 6.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }

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
                                val qty = qtyText.toDoubleOrNull() ?: 1.0
                                val est = estPriceText.toDoubleOrNull() ?: 0.0
                                if (nameText.isNotBlank()) {
                                    onAddItem(nameText.trim(), qty, selectedUnit, est, notesText.trim())
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f),
                            enabled = nameText.isNotBlank()
                        ) {
                            Text(I18n.t("save", language), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Actual Price Dialog when purchased (NO alte / placeholder text!)
    if (priceDialogItem != null) {
        val target = priceDialogItem!!
        Dialog(onDismissRequest = { priceDialogItem = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = target.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleAccent
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = enteredPriceText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) enteredPriceText = it },
                        label = { Text(I18n.t("act_price", language)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            onClick = { priceDialogItem = null },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(I18n.t("cancel", language))
                        }

                        Button(
                            onClick = {
                                val price = enteredPriceText.toDoubleOrNull() ?: 0.0
                                onTogglePurchased(target.id, true, price)
                                priceDialogItem = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text(I18n.t("save", language), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
