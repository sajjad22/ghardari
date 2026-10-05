package com.ghardari.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghardari.app.data.model.*
import com.ghardari.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    language: AppLanguage,
    recordedMonths: List<String>,
    selectedMonth: String,
    summary: DashboardSummary,
    monthlyExpenditures: List<MonthlyExpenditure>,
    dailyExpenses: List<DailyExpense>,
    rationItems: List<RationItem>,
    onSelectMonth: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = I18n.t("card_history", language), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
        ) {
            // Month Selector Chips
            item {
                Text(text = I18n.t("select_month", language), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recordedMonths.forEach { m ->
                        val isSelected = m == selectedMonth
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EmeraldPrimary else SlateLight,
                            contentColor = if (isSelected) Color.White else SlateDark,
                            modifier = Modifier.clickable { onSelectMonth(m) }
                        ) {
                            Text(
                                text = m,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Summary Card for Selected History Month
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "خلاصو: $selectedMonth", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "ڪل خرچ (Grand Total)", fontSize = 13.sp, color = SlateMuted)
                            Text(text = "Rs. ${summary.grandTotalExpense.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "ماهوار بل", fontSize = 13.sp, color = SlateMuted)
                            Text(text = "Rs. ${summary.monthlyBillsTotal.toInt()} (${summary.monthlyBillsPaid.toInt()} ادا)", fontSize = 13.sp, color = Color(0xFF1E40AF))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "روزانو خرچ", fontSize = 13.sp, color = SlateMuted)
                            Text(text = "Rs. ${summary.dailyExpensesTotal.toInt()}", fontSize = 13.sp, color = AmberAccent)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "راشن خرچ", fontSize = 13.sp, color = SlateMuted)
                            Text(text = "Rs. ${summary.rationTotal.toInt()}", fontSize = 13.sp, color = PurpleAccent)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "ماهوار بلن جو رڪارڊ ($selectedMonth)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Historical Monthly Bills
            if (monthlyExpenditures.isEmpty()) {
                item {
                    Text(text = "ڪوبه ماهوار بل موجود ناهي", fontSize = 12.sp, color = SlateMuted)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                items(monthlyExpenditures, key = { it.id }) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = item.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                                Text(text = if (item.isPaid) "ادا ٿيل" else "اڻ ادا", fontSize = 11.sp, color = if (item.isPaid) InflowGreen else OutflowRed)
                            }
                            Text(text = "Rs. ${item.amount.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Historical Ration Items
            item {
                Text(text = "راشن لسٽ ($selectedMonth)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (rationItems.isEmpty()) {
                item {
                    Text(text = "ڪابه راشن شيءِ موجود ناهي", fontSize = 12.sp, color = SlateMuted)
                }
            } else {
                items(rationItems, key = { it.id }) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                val brand = if (item.company.isNotBlank()) " (${item.company})" else ""
                                Text(text = "${item.name}$brand", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                                Text(text = "${item.quantity} ${item.unit}", fontSize = 11.sp, color = SlateMuted)
                            }
                            if (item.actualPrice > 0 || item.estimatedPrice > 0) {
                                val pr = if (item.actualPrice > 0) item.actualPrice else item.estimatedPrice
                                Text(text = "Rs. ${pr.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PurpleAccent)
                            }
                        }
                    }
                }
            }
        }
    }
}
