package ir.numdoz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.numdoz.profile.AVATARS
import ir.numdoz.profile.FaceAvatar
import ir.numdoz.profile.avatarById
import ir.numdoz.ui.components.AnimatedLogo
import ir.numdoz.ui.theme.LocalAccent
import ir.numdoz.ui.theme.NumDozTheme
import ir.numdoz.ui.theme.TextMuted
import ir.numdoz.ui.theme.TextPrimary
import ir.numdoz.ui.theme.TextSecondary

@Composable
fun ProfileSetupScreen(
    isFa: Boolean,
    onSave: (username: String, avatarId: Int) -> Unit
) {
    val accent = LocalAccent.current
    var username by remember { mutableStateOf("") }
    var avatarId by remember { mutableIntStateOf(0) }
    val valid = username.trim().length in 2..15
    val selected = avatarById(avatarId)

    Column(
        Modifier
            .fillMaxSize()
            .widthIn(max = 460.dp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        AnimatedLogo(size = 96.dp)
        Spacer(Modifier.height(20.dp))

        Text(
            text = if (isFa) "به عددچین خوش اومدی" else "Welcome to NumDoz",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Light,
            color = TextPrimary,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (isFa) "یک نام و آواتار برای خودت انتخاب کن"
                   else "Choose a name and an avatar",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        Spacer(Modifier.height(24.dp))

        // Selected avatar preview
        Box(
            Modifier
                .size(100.dp)
                .drawBehind {
                    val r = size.minDimension * 0.9f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(selected.color.copy(alpha = 0.35f), Color.Transparent),
                            center = center, radius = r
                        ),
                        radius = r, center = center
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                selected.color.copy(alpha = 0.35f),
                                selected.color.copy(alpha = 0.12f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.size(60.dp)) { FaceAvatar(spec = selected, modifier = Modifier.fillMaxSize()) }
            }
        }
        Spacer(Modifier.height(24.dp))

        // Username input
        Text(
            text = if (isFa) "نام کاربری" else "Username",
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { if (it.length <= 15) username = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(if (isFa) "مثلاً: آرش" else "e.g. Alex", color = TextMuted) },
            textStyle = MaterialTheme.typography.titleMedium.copy(color = TextPrimary),
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accent.copy(alpha = 0.7f),
                unfocusedBorderColor = accent.copy(alpha = 0.25f),
                cursorColor = accent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                if (valid) onSave(username.trim(), avatarId)
            })
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (isFa) "۲ تا ۱۵ کاراکتر" else "2 to 15 characters",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))

        // Avatar grid
        Text(
            text = if (isFa) "انتخاب آواتار" else "Pick your avatar",
            style = MaterialTheme.typography.labelLarge,
            color = TextMuted,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (row in 0 until 3) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (col in 0 until 4) {
                        val idx = row * 4 + col
                        if (idx < AVATARS.size) {
                            val av = AVATARS[idx]
                            val isSelected = av.id == avatarId
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected)
                                            Brush.horizontalGradient(
                                                listOf(av.color.copy(alpha = 0.30f), av.color.copy(alpha = 0.12f))
                                            )
                                        else
                                            Brush.horizontalGradient(listOf(Color(0xFF1E1E2E), Color(0xFF15151F)))
                                    )
                                    .clickable { avatarId = av.id },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(Modifier.size(40.dp)) { FaceAvatar(spec = av, modifier = Modifier.fillMaxSize()) }
                                if (isSelected) {
                                    Box(
                                        Modifier
                                            .size(16.dp)
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .clip(CircleShape)
                                            .background(av.color),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))

        // Submit
        Box(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (valid) Brush.horizontalGradient(listOf(accent.copy(alpha = 0.92f), accent.copy(alpha = 0.62f)))
                    else Brush.horizontalGradient(listOf(Color(0xFF23232F), Color(0xFF1A1A25)))
                )
                .clickable(enabled = valid) { onSave(username.trim(), avatarId) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isFa) "شروع سفر" else "Start the journey",
                color = if (valid) Color.White else TextMuted,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(40.dp))
    }
}
