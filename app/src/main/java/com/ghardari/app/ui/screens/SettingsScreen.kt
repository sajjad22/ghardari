package com.ghardari.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghardari.app.data.model.AppLanguage
import com.ghardari.app.data.model.I18n
import com.ghardari.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    language: AppLanguage,
    onBackClick: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onExportBackupClick: () -> Unit,
    onRestoreBackupClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = I18n.t("card_settings", language), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White) },
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
            contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language Selection Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "ٻوليءَ جي چونڊ (Language)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SlateLight, RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AppLanguage.values().forEach { lang ->
                                val isSelected = language == lang
                                Button(
                                    onClick = { onLanguageChange(lang) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) EmeraldPrimary else Color.Transparent,
                                        contentColor = if (isSelected) Color.White else SlateDark
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text(text = lang.displayName, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }
                }
            }

            // Backup & Export Section
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = PurpleAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = I18n.t("export_backup", language), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "پنهنجي سموري ڊيٽا جو محفوظ JSON فائيل ٺاهيو ۽ واٽس ايپ، اي ميل يا گوگل ڊرائيو تي محفوظ ڪريو.",
                            fontSize = 12.sp,
                            color = SlateMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onExportBackupClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(I18n.t("export_backup", language), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Restore from Backup Section
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = I18n.t("restore_backup", language), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "پنهنجي اڳ محفوظ ڪيل JSON بيڪ اپ فائيل مان سمورو کاتو، خرچ ۽ راشن لسٽ بحال ڪريو.",
                            fontSize = 12.sp,
                            color = SlateMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = onRestoreBackupClick,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp), tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(I18n.t("restore_backup", language), fontWeight = FontWeight.Bold, color = EmeraldDark)
                        }
                    }
                }
            }
        }
    }
}
