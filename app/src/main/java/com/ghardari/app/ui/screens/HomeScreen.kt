package com.ghardari.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghardari.app.data.model.*
import com.ghardari.app.ui.theme.*

@Composable
fun HomeScreen(
    language: AppLanguage,
    summary: DashboardSummary,
    onCardClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Overview Banner
        item(span = { GridItemSpan(2) }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = I18n.t("app_title", language),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = I18n.t("app_subtitle", language),
                                fontSize = 12.sp,
                                color = EmeraldLight
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = summary.monthKey,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "ڪل خرچ (Grand Total)",
                                    fontSize = 11.sp,
                                    color = EmeraldLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rs. ${summary.grandTotalExpense.toInt()}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = I18n.t("card_monthly", language),
                                    fontSize = 11.sp,
                                    color = EmeraldLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rs. ${summary.monthlyBillsTotal.toInt()}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (summary.monthlyBillsPendingCount > 0) {
                                    Text(
                                        text = "${summary.monthlyBillsPendingCount} " + I18n.t("unpaid", language),
                                        fontSize = 10.sp,
                                        color = Color(0xFFFCA5A5),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Card 1: Udhar Khata (اڌار کاتو)
        item {
            MainDashboardCard(
                title = I18n.t("card_khata", language),
                subtitle = I18n.t("card_khata_sub", language),
                badgeText = if (summary.khataWeOwe > 0) "ڏيڻا: ${summary.khataWeOwe.toInt()}" else if (summary.khataOwedToUs > 0) "وٺڻا: ${summary.khataOwedToUs.toInt()}" else "صاف",
                badgeColor = if (summary.khataWeOwe > 0) OutflowRed else InflowGreen,
                icon = Icons.Default.AccountBalanceWallet,
                cardColor = Color(0xFFF0FDF4),
                iconTint = InflowGreen,
                onClick = { onCardClick("KHATA") }
            )
        }

        // Card 2: Monthly House Expenditures (مهيني جا خرچ)
        item {
            MainDashboardCard(
                title = I18n.t("card_monthly", language),
                subtitle = I18n.t("card_monthly_sub", language),
                badgeText = if (summary.monthlyBillsPendingCount > 0) "${summary.monthlyBillsPendingCount} " + I18n.t("unpaid", language) else "سڀ ادا",
                badgeColor = if (summary.monthlyBillsPendingCount > 0) OutflowRed else InflowGreen,
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                cardColor = Color(0xFFEFF6FF),
                iconTint = Color(0xFF2563EB),
                onClick = { onCardClick("MONTHLY") }
            )
        }

        // Card 3: Daily Expenses (روزانو خرچ)
        item {
            MainDashboardCard(
                title = I18n.t("card_daily", language),
                subtitle = I18n.t("card_daily_sub", language),
                badgeText = "Rs. ${summary.dailyExpensesTotal.toInt()}",
                badgeColor = AmberAccent,
                icon = Icons.Default.Today,
                cardColor = Color(0xFFFFFBEB),
                iconTint = AmberAccent,
                onClick = { onCardClick("DAILY") }
            )
        }

        // Card 4: Monthly Ration (راشن لسٽ)
        item {
            MainDashboardCard(
                title = I18n.t("card_ration", language),
                subtitle = I18n.t("card_ration_sub", language),
                badgeText = "${summary.rationBoughtCount}/${summary.rationTotalCount} " + I18n.t("bought", language),
                badgeColor = PurpleAccent,
                icon = Icons.Default.ShoppingCart,
                cardColor = Color(0xFFFAF5FF),
                iconTint = PurpleAccent,
                onClick = { onCardClick("RATION") }
            )
        }

        // Card 5: Financial Reports (مالي رپورٽون)
        item {
            MainDashboardCard(
                title = I18n.t("card_reports", language),
                subtitle = I18n.t("card_reports_sub", language),
                badgeText = "خلاصو",
                badgeColor = EmeraldDark,
                icon = Icons.Default.Assessment,
                cardColor = Color(0xFFF0FDFA),
                iconTint = EmeraldPrimary,
                onClick = { onCardClick("REPORTS") }
            )
        }

        // Card 6: History & Archive (تاريخ ۽ آرڪائيو)
        item {
            MainDashboardCard(
                title = I18n.t("card_history", language),
                subtitle = I18n.t("card_history_sub", language),
                badgeText = "آرڪائيو",
                badgeColor = SlateDark,
                icon = Icons.Default.History,
                cardColor = Color(0xFFF8FAFC),
                iconTint = SlateDark,
                onClick = { onCardClick("HISTORY") }
            )
        }

        // Card 7: Settings & Backup (سيٽنگ ۽ بيڪ اپ)
        item(span = { GridItemSpan(2) }) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCardClick("SETTINGS") }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SlateLight,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Settings, contentDescription = null, tint = SlateDark)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = I18n.t("card_settings", language),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark
                            )
                            Text(
                                text = I18n.t("card_settings_sub", language),
                                fontSize = 11.sp,
                                color = SlateMuted
                            )
                        }
                    }

                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SlateMuted)
                }
            }
        }
    }
}

@Composable
fun MainDashboardCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    icon: ImageVector,
    cardColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = iconTint.copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = SlateMuted,
                    maxLines = 2
                )
            }
        }
    }
}
