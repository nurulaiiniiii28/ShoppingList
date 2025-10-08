package com.example.shoppinglist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.nurull), // Ganti dengan nama file gambar Anda
            contentDescription = "Foto Profil",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                ProfileInfoRow("Nama", "Nurul Aini")
                ProfileInfoRow("NIM", "2311521013")
                ProfileInfoRow("Hobi", "Mendengarkan musik, Menonton Film")
                ProfileInfoRow("Tempat, Tanggal Lahir", "Bukittinggi, 28 Januari 2005")
                ProfileInfoRow("Peminatan", "Web Programming, UI/UX Design")
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(120.dp)
        )
        Text(text = ":")
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value)
    }
}