package com.ghardari.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import com.ghardari.app.data.model.*
import com.ghardari.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddDialog(
    language: AppLanguage,
    categories: List<CategoryRecord>,
    parties: List<PartyRecord>,
    onDismiss: () -> Unit,
    onSave: (partyId: Long?, partyName: String, categoryId: Long?, categoryKey: String, type: String, amount: Double, paymentMethod: String, notes: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("OUTFLOW") } // OUTFLOW or INFLOW or KHATA_GIVE
    var selectedCategory by remember { mutableStateOf<CategoryRecord?>(categories.firstOrNull()) }
    var selectedParty by remember { mutableStateOf<PartyRecord?>(null) }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var notesText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Text(
                    text = I18n.t("quick_add", language),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Inflow vs Outflow toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SlateLight, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val isOutflow = selectedType == "OUTFLOW"
                    val isInflow = selectedType == "INFLOW"

                    Button(
                        onClick = { selectedType = "OUTFLOW"; selectedParty = null },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOutflow) OutflowRed else Color.Transparent,
                            contentColor = if (isOutflow) Color.White else SlateDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text(
                            text = I18n.t("outflow", language),
                            fontWeight = if (isOutflow) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Button(
                        onClick = { selectedType = "INFLOW"; selectedParty = null },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInflow) InflowGreen else Color.Transparent,
                            contentColor = if (isInflow) Color.White else SlateDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Text(
                            text = I18n.t("inflow", language),
                            fontWeight = if (isInflow) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Large Amount Input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) amountText = it },
                    label = { Text(I18n.t("amount", language), fontSize = 16.sp) },
                    placeholder = { Text("5000", fontSize = 24.sp, color = Color.LightGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold, color = EmeraldDark),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Category Chips
                Text(
                    text = I18n.t("category", language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SlateMuted
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory?.id == cat.id && selectedParty == null
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) EmeraldPrimary else SlateLight,
                            contentColor = if (isSelected) Color.White else SlateDark,
                            modifier = Modifier.clickable {
                                selectedCategory = cat
                                selectedParty = null
                            }
                        ) {
                            Text(
                                text = cat.localizedName(language),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                // If parties exist, show party chips too!
                if (parties.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = I18n.t("person", language),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        parties.forEach { party ->
                            val isSelected = selectedParty?.id == party.id
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) PurpleAccent else SlateLight,
                                contentColor = if (isSelected) Color.White else SlateDark,
                                modifier = Modifier.clickable {
                                    selectedParty = party
                                    selectedType = "KHATA_GIVE"
                                }
                            ) {
                                Text(
                                    text = party.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Method Chips (Cash, Easypaisa, Bank)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Cash", "Easypaisa", "Bank").forEach { method ->
                        val isSelected = paymentMethod == method
                        val label = when (method) {
                            "Cash" -> if (language == AppLanguage.SINDHI) "ڪيش" else if (language == AppLanguage.URDU) "کیش" else "Cash"
                            "Easypaisa" -> "Easypaisa"
                            else -> if (language == AppLanguage.SINDHI) "بئنڪ" else if (language == AppLanguage.URDU) "بینک" else "Bank"
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) EmeraldLight else SlateLight,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { paymentMethod = method }
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) EmeraldDark else SlateDark,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Optional Notes
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    placeholder = { Text(I18n.t("notes", language), fontSize = 14.sp) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(I18n.t("cancel", language))
                    }

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                val pId = selectedParty?.id
                                val pName = selectedParty?.name ?: ""
                                val cId = selectedCategory?.id
                                val cKey = selectedCategory?.key ?: "other"
                                val finalType = if (pId != null) {
                                    if (selectedType == "INFLOW") "KHATA_RECEIVE" else "KHATA_GIVE"
                                } else {
                                    selectedType
                                }
                                onSave(pId, pName, cId, cKey, finalType, amt, paymentMethod, notesText)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.5f),
                        enabled = amountText.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0
                    ) {
                        Text(
                            text = I18n.t("save", language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
