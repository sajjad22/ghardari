package com.ghardari.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.ghardari.app.data.model.*
import com.ghardari.app.ui.theme.*

@Composable
fun RationScreen(
    language: AppLanguage,
    rationItems: List<RationItem>,
    summary: MonthSummary,
    onAddItemClick: () -> Unit,
    onTogglePurchased: (id: Long, isPurchased: Boolean, actualPrice: Double) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onAddToMonthlyExpenses: () -> Unit
) {
    var priceDialogItem by remember { mutableStateOf<RationItem?>(null) }
    var enteredPriceText by remember { mutableStateOf("") }

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
                        text = "${target.localizedName(language)} - ${I18n.t("act_price", language)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = enteredPriceText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) enteredPriceText = it },
                        label = { Text("خرچ ٿيل رقم (Rs.)") },
                        placeholder = { Text("مثال: 450") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
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
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Grocery Budget Summary Card
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F3FF)), // Light Purple
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = I18n.t("ration_title", language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = PurpleAccent
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rs. ${summary.rationTotal.toInt()}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleAccent
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PurpleAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${summary.rationBoughtCount} / ${summary.rationTotalCount} " + I18n.t("bought", language),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleAccent,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    if (summary.rationTotal > 0) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onAddToMonthlyExpenses,
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = I18n.t("add_to_monthly_expenses", language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Header with Add Item button
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سودا سلف لسٽ (Grocery List)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )

                Button(
                    onClick = onAddItemClick,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(I18n.t("add_item", language), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Checklist Items
        items(rationItems, key = { it.id }) { item ->
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
                            colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = item.localizedName(language),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDone) SlateMuted else SlateDark,
                                textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                            )

                            val unitDisplay = when (item.unit) {
                                "kg" -> "ڪلو (kg)"
                                "gm" -> "گرام (gm)"
                                "liter" -> "ليٽر (L)"
                                "bottle" -> "بوتل (bottle)"
                                "packet" -> "پيڪٽ (pkt)"
                                "piece" -> "عدد (pc)"
                                else -> item.unit
                            }

                            val qtyDisplay = if (item.quantity % 1.0 == 0.0) "${item.quantity.toInt()} $unitDisplay" else "${item.quantity} $unitDisplay"

                            Text(
                                text = qtyDisplay + if (item.notes.isNotBlank()) " • ${item.notes}" else "",
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
                                color = if (isDone) EmeraldDark else SlateMuted
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
