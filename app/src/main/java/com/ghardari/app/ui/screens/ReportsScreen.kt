package com.ghardari.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.ghardari.app.data.model.AppLanguage
import com.ghardari.app.data.model.DashboardSummary
import com.ghardari.app.data.model.I18n
import com.ghardari.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    language: AppLanguage,
    summary: DashboardSummary,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = I18n.t("card_reports", language) + " (${summary.monthKey})",
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Grand Total Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Text(
                            text = "هن مهيني جو ڪل خرچ (Grand Total Spent)",
                            fontSize = 13.sp,
                            color = EmeraldLight
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Rs. ${summary.grandTotalExpense.toInt()}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "(ادا ٿيل بل + روزانو خرچ + ورتل راشن)",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Monthly Bills Report Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "1. " + I18n.t("card_monthly", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ReportRow(label = "ڪل متوقع بل", value = "Rs. ${summary.monthlyBillsTotal.toInt()}", color = SlateDark)
                        ReportRow(label = "ادا ٿيل بل (Paid)", value = "Rs. ${summary.monthlyBillsPaid.toInt()}", color = InflowGreen)
                        ReportRow(label = "باقي رهيل (Unpaid)", value = "Rs. ${summary.monthlyBillsUnpaid.toInt()}", color = OutflowRed)
                        ReportRow(label = "باقي بلن جو تعداد", value = "${summary.monthlyBillsPendingCount}", color = SlateMuted)
                    }
                }
            }

            // Daily Expenses Report Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "2. " + I18n.t("card_daily", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberAccent
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ReportRow(label = "ڪل روزانو خرچ", value = "Rs. ${summary.dailyExpensesTotal.toInt()}", color = AmberAccent)
                    }
                }
            }

            // Ration Report Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "3. " + I18n.t("card_ration", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurpleAccent
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ReportRow(label = "ورتل راشن جو خرچ", value = "Rs. ${summary.rationTotal.toInt()}", color = PurpleAccent)
                        ReportRow(label = "خريداري جي ترقي", value = "${summary.rationBoughtCount} / ${summary.rationTotalCount} شيون ورتل", color = SlateMuted)
                    }
                }
            }

            // Udhar Khata Report Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "4. " + I18n.t("card_khata", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ReportRow(label = I18n.t("we_owe", language) + " (Payables)", value = "Rs. ${summary.khataWeOwe.toInt()}", color = OutflowRed)
                        ReportRow(label = I18n.t("owed_to_us", language) + " (Receivables)", value = "Rs. ${summary.khataOwedToUs.toInt()}", color = InflowGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = SlateMuted)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
