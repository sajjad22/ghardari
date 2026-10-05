package com.ghardari.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ghardari.app.data.model.AppLanguage
import com.ghardari.app.data.model.CustomCard
import com.ghardari.app.data.model.I18n
import com.ghardari.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    language: AppLanguage,
    customCards: List<CustomCard>,
    onBackClick: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onAddCustomCard: (title: String, targetType: String, targetId: Long?, icon: String, color: String) -> Unit,
    onDeleteCustomCard: (id: Long) -> Unit,
    onExportBackupClick: () -> Unit
) {
    var showAddCardDialog by remember { mutableStateOf(false) }

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
            contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp)
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
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Custom Cards Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مکيه صفحي جا نوان ڪارڊ (Custom Cards)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )

                    Button(
                        onClick = { showAddCardDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(I18n.t("add_custom_card", language), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Custom Cards List
            if (customCards.isEmpty()) {
                item {
                    Text(text = "اڃا ڪو به نئون ڪارڊ شامل نه ڪيو ويو آهي.", fontSize = 12.sp, color = SlateMuted)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                items(customCards, key = { it.id }) { card ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = card.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                            IconButton(onClick = { onDeleteCustomCard(card.id) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray)
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
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

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "پنهنجي سموري ڊيٽا جو JSON بيڪ اپ وٺو ۽ واٽس ايپ يا گوگل ڊرائيو تي شيئر ڪريو.",
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
        }
    }

    // Add Custom Card Dialog (NO alte / placeholder text!)
    if (showAddCardDialog) {
        var titleText by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showAddCardDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = I18n.t("add_custom_card", language), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        label = { Text(I18n.t("card_title", language)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = { showAddCardDialog = false }, shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f)) {
                            Text(I18n.t("cancel", language))
                        }
                        Button(
                            onClick = {
                                if (titleText.isNotBlank()) {
                                    onAddCustomCard(titleText.trim(), "KHATA", null, "Star", "#0F766E")
                                    showAddCardDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f),
                            enabled = titleText.isNotBlank()
                        ) {
                            Text(I18n.t("save", language), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
