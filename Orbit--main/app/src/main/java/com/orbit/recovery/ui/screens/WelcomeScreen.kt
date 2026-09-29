package com.orbit.recovery.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.orbit.recovery.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.orbit.recovery.ui.components.*
import com.orbit.recovery.ui.theme.*

import androidx.compose.foundation.layout.systemBarsPadding

@Composable
fun WelcomeScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToQuiz: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        color = OrbitBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            
            // 1. Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.orbit_logo),
                    contentDescription = "Orbit",
                    modifier = Modifier.height(28.dp).wrapContentWidth(),
                    contentScale = ContentScale.Fit
                )
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, OrbitBorder, RoundedCornerShape(50))
                        .clickable { onNavigateToHome() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Preview Home",
                        style = Typography.labelSmall,
                        color = OrbitTextPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 2. Hero Headline and Subtitle
            Text(
                text = "You're not alone in this.",
                style = Typography.headlineLarge,
                color = OrbitTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "A calm space that reminds you: relapse doesn't mean failure. Support is part of the plan.",
                style = Typography.bodyLarge,
                color = OrbitTextSecondary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 3. Community Momentum Card
            CommunityMomentumCard()
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // 4. WHAT ORBIT DOES FOR YOU section
            Text(
                text = "WHAT ORBIT DOES FOR YOU",
                style = Typography.labelSmall,
                color = OrbitTextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            FeatureHighlightCard(
                icon = Icons.Default.Group,
                title = "You're not alone",
                subtitle = "A calm space that reminds you: relapse doesn't mean failure."
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            FeatureHighlightCard(
                icon = Icons.Default.Shield,
                title = "Take back control, instantly",
                subtitle = "One tap opens a fast reset to ride the urge wave."
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            FeatureHighlightCard(
                icon = Icons.Default.LocalFireDepartment,
                title = "See progress that feels real",
                subtitle = "Track your streak, collect orbs, and notice small wins."
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            FeatureHighlightCard(
                icon = Icons.Default.AutoAwesome,
                title = "Your coach, always by your side",
                subtitle = "A non-judgmental guide to choose your next right action."
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // 5. Start your plan button
            OrbitPrimaryButton(
                text = "Start your plan →",
                onClick = { onNavigateToQuiz() }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 6. Premium Teaser Card
            PremiumTeaserCard()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 7. Disclaimer Text
            Text(
                text = "This prototype is for support and self-improvement, not medical advice.",
                style = Typography.bodyMedium,
                color = OrbitTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FeatureHighlightCard(icon: ImageVector, title: String, subtitle: String) {
    Surface(
        color = OrbitSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(OrbitSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = OrbitPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = Typography.titleMedium, color = OrbitTextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, style = Typography.bodyMedium, color = OrbitTextSecondary)
            }
        }
    }
}

@Composable
fun CommunityMomentumCard() {
    Surface(
        color = OrbitSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "COMMUNITY / THIS WEEK'S MOMENTUM",
                style = Typography.labelSmall,
                color = OrbitTextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Folded (reset)", style = Typography.bodyMedium, color = OrbitTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "38%", style = Typography.headlineMedium, color = OrbitTextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Still going strong", style = Typography.bodyMedium, color = OrbitTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "62%", style = Typography.headlineMedium, color = OrbitPrimary)
                }
            }
        }
    }
}

@Composable
fun PremiumTeaserCard() {
    Surface(
        color = OrbitSurfaceVariant,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "PREMIUM TEASER", style = Typography.labelSmall, color = OrbitTextMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Loved by thousands", style = Typography.titleLarge, color = OrbitTextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Unlock deeper insights, guided resets, and personalized routines.", style = Typography.bodyMedium, color = OrbitTextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(4) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = OrbitAmber, modifier = Modifier.size(16.dp))
                }
                Icon(imageVector = Icons.Outlined.Star, contentDescription = null, tint = OrbitTextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "4.8 Rated highly", style = Typography.labelSmall, color = OrbitTextSecondary)
            }
        }
    }
}
