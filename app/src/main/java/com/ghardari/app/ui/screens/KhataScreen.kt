package com.ghardari.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghardari.app.data.model.AppLanguage
import com.ghardari.app.data.model.I18n
import com.ghardari.app.data.model.PartyRecord
import com.ghardari.app.ui.dialogs.AddPersonDialog
import com.ghardari.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhataScreen(
    language: AppLanguage,
    parties: List<PartyRecord>,
    onBackClick: () -> Unit,
    onPartyClick: (PartyRecord) -> Unit,
    onAddParty: (name: String, phone: String, type: String, initialBalance: Double, notes: String) -> Unit
) {
    var showAddPersonDialog by remember { mutableStateOf(false) }

    val totalWeOwe = parties.filter { it.type == "LENDER" && it.currentBalance > 0 }.sumOf { it.currentBalance }
    val totalOwedToUs = parties.filter { it.type == "BORROWER" && it.currentBalance > 0 }.sumOf { it.currentBalance }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = I18n.t("card_khata", language),
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
                onClick = { showAddPersonDialog = true },
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Person")
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
            // Totals Banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = I18n.t("we_owe", language), fontSize = 11.sp, color = OutflowRed)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rs. ${totalWeOwe.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = OutflowRed
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = I18n.t("owed_to_us", language), fontSize = 11.sp, color = InflowGreen)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rs. ${totalOwedToUs.toInt()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = InflowGreen
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (parties.isEmpty()) {
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
                items(parties, key = { it.id }) { party ->
                    val isCleared = party.currentBalance == 0.0
                    val isLender = party.type == "LENDER"

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable { onPartyClick(party) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCleared) SlateLight
                                            else if (isLender) OutflowRed.copy(alpha = 0.12f)
                                            else InflowGreen.copy(alpha = 0.12f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = party.name.take(1).uppercase(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCleared) SlateMuted else if (isLender) OutflowRed else InflowGreen
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = party.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateDark
                                    )
                                    val status = if (isCleared) I18n.t("cleared", language) else if (isLender) I18n.t("we_owe", language) else I18n.t("owed_to_us", language)
                                    Text(
                                        text = status,
                                        fontSize = 11.sp,
                                        color = if (isCleared) SlateMuted else if (isLender) OutflowRed else InflowGreen
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isCleared) I18n.t("cleared", language) else "Rs. ${party.currentBalance.toInt()}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCleared) SlateMuted else if (isLender) OutflowRed else InflowGreen
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SlateMuted)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddPersonDialog) {
        AddPersonDialog(
            language = language,
            onDismiss = { showAddPersonDialog = false },
            onSave = { name, phone, type, bal, notes ->
                onAddParty(name, phone, type, bal, notes)
                showAddPersonDialog = false
            }
        )
    }
}
