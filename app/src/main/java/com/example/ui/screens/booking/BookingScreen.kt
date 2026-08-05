package com.example.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.Property
import com.example.ui.components.HackathonDemoBadge
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.NavyDark
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.TealContainer
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SafeNestViewModel

@Composable
fun BookingScreen(
    propertyId: String,
    viewModel: SafeNestViewModel,
    onBack: () -> Unit,
    onBookingSubmitted: () -> Unit
) {
    var property by remember { mutableStateOf<Property?>(null) }

    var moveInDate by remember { mutableStateOf("15 Aug 2026") }
    var durationMonths by remember { mutableStateOf("11") }
    var tenantName by remember { mutableStateOf("Ananya Sharma") }
    var tenantPhone by remember { mutableStateOf("+91 98765 43210") }
    var message by remember { mutableStateOf("Hi, I am interested in moving into CampusNest. I would like to schedule a physical walkthrough tomorrow.") }
    var isSubmitted by remember { mutableStateOf(false) }

    LaunchedEffect(propertyId) {
        property = viewModel.getPropertyById(propertyId)
    }

    Scaffold(
        topBar = { HackathonDemoBadge() },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("booking_back_btn")) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NavyDark)
                }
                Text(
                    text = "Request Escrow Booking",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
            }

            val item = property
            if (item != null) {
                // Property Summary Header
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(TealContainer, shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = TealPrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                            Text(text = "${item.locality}, ${item.city}", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "₹${item.monthlyRent} / month • Deposit: ₹${item.deposit}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                        }
                    }
                }

                // Escrow Protection Info Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(TealContainer, RoundedCornerShape(12.dp))
                        .border(1.dp, TealPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("100% Escrow Protected Booking", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Your booking request is logged safely into local database ledger. Token payments are only transferred after physical key handover and room inspection.",
                                fontSize = 11.sp,
                                color = NavyDark,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isSubmitted) {
                    // Success View
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .border(1.dp, SafeGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Booking Request Logged!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NavyDark)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Your request has been submitted with Escrow Protection active. The landlord will review your request shortly.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onBookingSubmitted,
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("booking_success_done_btn")
                            ) {
                                Text("Return to Dashboard", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Booking Form Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Tenant & Lease Information", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyDark)

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = moveInDate,
                                onValueChange = { moveInDate = it },
                                label = { Text("Target Move-in Date") },
                                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextSecondary) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("booking_movein_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = durationMonths,
                                onValueChange = { durationMonths = it },
                                label = { Text("Lease Duration (Months)") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("booking_duration_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = tenantName,
                                onValueChange = { tenantName = it },
                                label = { Text("Tenant Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("booking_tenant_name_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = tenantPhone,
                                onValueChange = { tenantPhone = it },
                                label = { Text("Tenant Mobile Number") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TextSecondary) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("booking_tenant_phone_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = message,
                                onValueChange = { message = it },
                                label = { Text("Message to Landlord / Warden") },
                                minLines = 2,
                                maxLines = 4,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("booking_message_input")
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    val duration = durationMonths.toIntOrNull() ?: 11
                                    viewModel.submitBookingRequest(
                                        property = item,
                                        moveInDate = moveInDate,
                                        durationMonths = duration,
                                        tenantName = tenantName,
                                        tenantPhone = tenantPhone,
                                        message = message,
                                        onSuccess = { isSubmitted = true }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("booking_submit_request_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Submit Escrow Booking Request", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
