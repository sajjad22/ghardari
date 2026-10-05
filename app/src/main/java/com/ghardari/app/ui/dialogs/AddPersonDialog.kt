package com.ghardari.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun AddPersonDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, type: String, initialBalance: Double, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("LENDER") } // LENDER (we owe them) or BORROWER (they owe us)
    var balanceText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = I18n.t("add_person", language),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(I18n.t("person_name", language)) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("فون نمبر (Phone)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Type selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SlateLight, RoundedCornerShape(10.dp))
                        .padding(4.dp)
                ) {
                    val isLender = type == "LENDER"
                    Button(
                        onClick = { type = "LENDER" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLender) EmeraldPrimary else Color.Transparent,
                            contentColor = if (isLender) Color.White else SlateDark
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Text(I18n.t("we_owe", language), fontSize = 12.sp)
                    }

                    Button(
                        onClick = { type = "BORROWER" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isLender) EmeraldPrimary else Color.Transparent,
                            contentColor = if (!isLender) Color.White else SlateDark
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 6.dp)
                    ) {
                        Text(I18n.t("owed_to_us", language), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) balanceText = it },
                    label = { Text(I18n.t("amount", language)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

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
                                onSave(name.trim(), phone.trim(), type, balanceText.toDoubleOrNull() ?: 0.0, notes.trim())
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
