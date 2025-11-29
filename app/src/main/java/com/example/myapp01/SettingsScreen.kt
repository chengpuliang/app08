package com.example.myapp01

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    var isCH by remember { mutableStateOf(GlobalSettings.getLang() == Lang.CH) }
    Column(
        modifier = Modifier
            .padding(18.dp, 0.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        VSpacer(12.dp)
        IconButton(
            onClick = { viewModel.pop() },
            modifier = Modifier
                .padding(0.dp, 24.dp, 0.dp, 0.dp)
                .align(Alignment.End),

            ) {
            Icon(Icons.Default.Done, contentDescription = "Done")
        }
        Text(
            text = getLangText("設定", "Settings"),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        VSpacer(12.dp)
        Row {
            Button(
                onClick = {
                    GlobalSettings.setLang(Lang.CH)
                    isCH = true
                    viewModel.saveGlobalSettings()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isCH) MaterialTheme.colorScheme.primary else Color.Gray),
                modifier = Modifier.width(80.dp)
            ) {
                Text("中文")
            }
            HSpacer(10.dp)
            Button(
                onClick = {
                    GlobalSettings.setLang(Lang.EN)
                    isCH = false
                    viewModel.saveGlobalSettings()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (!isCH) MaterialTheme.colorScheme.primary else Color.Gray),
                modifier = Modifier.width(80.dp)
            ) {
                Text("EN")
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun SettingsPreview() {
    SettingsScreen(MainViewModel())
}