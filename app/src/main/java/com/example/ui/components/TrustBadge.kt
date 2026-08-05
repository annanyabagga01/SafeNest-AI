package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RiskLevel
import com.example.ui.theme.BadgeVerifiedBg
import com.example.ui.theme.BadgeVerifiedText
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WarningAmber

@Composable
fun TrustBadge(
    score: Int,
    label: String = "Trust Score",
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(BadgeVerifiedBg, shape = RoundedCornerShape(12.dp))
            .border(1.dp, BadgeVerifiedText.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = "Verified",
            tint = BadgeVerifiedText,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label $score%",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = BadgeVerifiedText
        )
    }
}

@Composable
fun RiskBadge(
    level: RiskLevel,
    score: Int,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (level) {
        RiskLevel.LOW -> Triple(Color(0xFFDCFCE7), SafeGreen, Icons.Default.CheckCircle)
        RiskLevel.MEDIUM -> Triple(Color(0xFFFEF3C7), WarningAmber, Icons.Default.Shield)
        RiskLevel.HIGH -> Triple(Color(0xFFFEE2E2), DangerRed, Icons.Default.Shield)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = level.name,
            tint = textColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${level.name} RISK ($score%)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
