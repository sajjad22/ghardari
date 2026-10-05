package com.ghardari.app.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ghardari.app.data.model.AppLanguage
import com.ghardari.app.data.model.I18n
import com.ghardari.app.ui.theme.EmeraldDark
import com.ghardari.app.ui.theme.EmeraldPrimary
import com.ghardari.app.ui.theme.SlateDark
import com.ghardari.app.ui.theme.SlateLight

@Composable
fun AddRationItemDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (name: String, qty: Double, unit: String, estPrice: Double, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var qtyText by remember { mutableStateOf("1") }
    var selectedUnit by remember { mutableStateOf("kg") }
    var estPriceText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val units = listOf(
        "kg" to "ڪلو (kg)",
        "gm" to "گرام (gm)",
        "liter" to "ليٽر (L)",
        "bottle" to "بوتل (bottle)",
        "packet" to "پيڪٽ (pkt)",
        "piece" to "عدد (pc)",
        "dozen" to "درجن (dz)"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = I18n.t("add_item", language),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(I18n.t("item_name", language)) },
                    placeholder = { Text("مثال: کنڊ، شيمپو، اٽو") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
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
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = estPriceText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) estPriceText = it },
                        label = { Text(I18n.t("est_price", language)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = I18n.t("unit", language),
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    units.forEach { (unitKey, unitLabel) ->
                        val isSelected = selectedUnit == unitKey
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EmeraldPrimary else SlateLight,
                            contentColor = if (isSelected) Color.White else SlateDark,
                            modifier = Modifier.clickable { selectedUnit = unitKey }
                        ) {
                            Text(
                                text = unitLabel,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(I18n.t("notes", language)) },
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
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(I18n.t("cancel", language))
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(
                                    name.trim(),
                                    qtyText.toDoubleOrNull() ?: 1.0,
                                    selectedUnit,
                                    estPriceText.toDoubleOrNull() ?: 0.0,
                                    notes.trim()
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.2f),
                        enabled = name.isNotBlank()
                    ) {
                        Text(I18n.t("save", language), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
