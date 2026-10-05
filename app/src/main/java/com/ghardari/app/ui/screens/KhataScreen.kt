package com.ghardari.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghardari.app.data.model.AppLanguage
import com.ghardari.app.data.model.I18n
import com.ghardari.app.data.model.MonthSummary
import com.ghardari.app.data.model.PartyRecord
import com.ghardari.app.ui.theme.*

@Composable
fun KhataScreen(
    language: AppLanguage,
    parties: List<PartyRecord>,
    summary: MonthSummary,
    onAddPersonClick: () -> Unit,
    onClearAccountClick: (PartyRecord) -> Unit,
    onShareWhatsAppClick: (PartyRecord) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Khata Totals
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // We owe them (Payable / Tailors / Miss XYZ)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = I18n.t("we_owe", language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = OutflowRed
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rs. ${summary.khataWeOwe.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = OutflowRed
                        )
                    }
                }

                // They owe us (Receivable / Lent)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = I18n.t("owed_to_us", language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = InflowGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rs. ${summary.khataOwedToUs.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = InflowGreen
                        )
                    }
                }
            }
        }

        // Header with Add Button
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = I18n.t("tab_khata", language),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )

                Button(
                    onClick = onAddPersonClick,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(I18n.t("add_person", language), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Parties List
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
                val isLender = party.type == "LENDER" // We owe them

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                    val statusLabel = if (isCleared) {
                                        I18n.t("cleared", language)
                                    } else if (isLender) {
                                        I18n.t("we_owe_them", language)
                                    } else {
                                        I18n.t("they_owe_us", language)
                                    }
                                    Text(
                                        text = statusLabel,
                                        fontSize = 11.sp,
                                        color = if (isCleared) SlateMuted else if (isLender) OutflowRed else InflowGreen,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isCleared) I18n.t("cleared", language) else "Rs. ${party.currentBalance.toInt()}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCleared) SlateMuted else if (isLender) OutflowRed else InflowGreen
                                )
                                if (party.phone.isNotBlank()) {
                                    Text(
                                        text = party.phone,
                                        fontSize = 11.sp,
                                        color = SlateMuted
                                    )
                                }
                            }
                        }

                        // Bottom Actions for each party (Clear Account, WhatsApp Share)
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = SlateLight, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { onShareWhatsAppClick(party) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF25D366))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(I18n.t("share_whatsapp", language), fontSize = 12.sp, color = Color(0xFF25D366), fontWeight = FontWeight.SemiBold)
                            }

                            if (!isCleared) {
                                OutlinedButton(
                                    onClick = { onClearAccountClick(party) },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp), tint = EmeraldPrimary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(I18n.t("clear_account", language), fontSize = 11.sp, color = EmeraldDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
