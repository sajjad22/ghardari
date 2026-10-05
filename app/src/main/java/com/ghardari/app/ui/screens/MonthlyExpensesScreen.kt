package com.ghardari.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghardari.app.data.model.*
import com.ghardari.app.ui.theme.*

@Composable
fun MonthlyExpensesScreen(
    language: AppLanguage,
    summary: MonthSummary,
    transactions: List<TransactionRecord>,
    categories: List<CategoryRecord>,
    onAddCustomCategoryClick: () -> Unit,
    onDeleteTransaction: (Long) -> Unit
) {
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val filteredTransactions = remember(transactions, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) {
            transactions
        } else {
            transactions.filter { it.categoryKey == selectedCategoryFilter }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Summary Cards
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark),
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
                                text = I18n.t("net_balance", language),
                                fontSize = 13.sp,
                                color = EmeraldLight
                            )
                            Text(
                                text = "Rs. ${summary.netBalance.toInt()}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = summary.monthKey,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Inflow
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = I18n.t("received_money", language),
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "+ ${summary.totalInflow.toInt()}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF86EFAC) // Soft Light Green
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Outflow
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = I18n.t("total_spent", language),
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "- ${summary.totalOutflow.toInt()}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFCA5A5) // Soft Light Red
                                )
                            }
                        }
                    }
                }
            }
        }

        // Category Filter & Custom Categories
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = I18n.t("category", language),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )

                TextButton(onClick = onAddCustomCategoryClick) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(I18n.t("add_category", language), fontSize = 12.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All" filter chip
                val isAllSelected = selectedCategoryFilter == null
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isAllSelected) EmeraldDark else SlateLight,
                    contentColor = if (isAllSelected) Color.White else SlateDark,
                    modifier = Modifier.clickable { selectedCategoryFilter = null }
                ) {
                    Text(
                        text = if (language == AppLanguage.SINDHI) "سڀ (All)" else if (language == AppLanguage.URDU) "سب (All)" else "All",
                        fontSize = 12.sp,
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }

                categories.forEach { cat ->
                    val isSelected = selectedCategoryFilter == cat.key
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) EmeraldDark else SlateLight,
                        contentColor = if (isSelected) Color.White else SlateDark,
                        modifier = Modifier.clickable {
                            selectedCategoryFilter = if (isSelected) null else cat.key
                        }
                    ) {
                        Text(
                            text = cat.localizedName(language),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Transactions Header
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = I18n.t("tab_expenses", language),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Transaction list items
        if (filteredTransactions.isEmpty()) {
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
            items(filteredTransactions, key = { it.id }) { trx ->
                val isInflow = trx.type == "INFLOW"
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
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
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isInflow) InflowGreen.copy(alpha = 0.15f) else OutflowRed.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isInflow) "+" else "-",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isInflow) InflowGreen else OutflowRed
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                val catObj = categories.find { it.key == trx.categoryKey }
                                val title = if (trx.partyName.isNotBlank()) trx.partyName else (catObj?.localizedName(language) ?: trx.categoryKey)
                                Text(
                                    text = title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SlateDark
                                )

                                val subtext = listOfNotNull(
                                    trx.notes.takeIf { it.isNotBlank() },
                                    trx.paymentMethod,
                                    trx.date
                                ).joinToString(" • ")

                                Text(
                                    text = subtext,
                                    fontSize = 11.sp,
                                    color = SlateMuted
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Rs. ${trx.amount.toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isInflow) InflowGreen else OutflowRed
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = { onDeleteTransaction(trx.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
