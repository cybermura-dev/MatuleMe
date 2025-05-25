package ru.takeshiko.matuleme.presentation.components.paymentmethods

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.UserPaymentDto
import ru.takeshiko.matuleme.presentation.components.fields.CustomOutlinedTextField
import ru.takeshiko.matuleme.presentation.theme.AppTypography
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun UpdatePaymentMethodContent(
    payment: UserPaymentDto,
    isLoading: Boolean,
    onSavePayment: (UserPaymentDto) -> Unit,
    onNavigateToBack: () -> Unit
) {
    val appColors = rememberAppColors()
    val typography = AppTypography
    val density = LocalDensity.current

    var animateItems by remember { mutableStateOf(false) }
    var animateFields by remember { mutableStateOf(false) }

    var rawCardNumber by remember { mutableStateOf(payment.cardNumber) }
    var cardHolderName by remember { mutableStateOf(payment.cardHolderName) }
    var rawExpirationDate by remember { mutableStateOf(payment.expirationDate) }
    var isDefault by remember { mutableStateOf(payment.isDefault == true) }

    val cardNumberError = rawCardNumber.filter { it.isDigit() }.length != 16
    val cardHolderNameError = cardHolderName.isBlank()
    val expirationDateError = rawExpirationDate.filter { it.isDigit() }.length != 4 ||
            !isValidExpirationDate(rawExpirationDate)

    LaunchedEffect(isLoading) {
        animateItems = !isLoading
        animateFields = !isLoading
    }

    fun formatExpirationDateForSave(text: String): String {
        val digitsOnly = text.filter { it.isDigit() }
        return buildString {
            digitsOnly.forEachIndexed { index, c ->
                if (index == 2) append('/')
                append(c)
            }
        }.take(5)
    }


    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            AddPaymentMethodTopBar(onNavigateToBack)
        },
        bottomBar = {
            AnimatedVisibility(
                visible = animateItems,
                enter = fadeIn(tween(700)) + slideInVertically(
                    initialOffsetY = { with(density) { 100.dp.roundToPx() } },
                    animationSpec = tween(700)
                ),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = appColors.primaryColor.copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = appColors.surfaceColor.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                val formattedExpirationDate = formatExpirationDateForSave(rawExpirationDate)

                                onSavePayment(
                                    payment.copy(
                                        cardNumber = rawCardNumber.replace(" ", ""),
                                        cardHolderName = cardHolderName,
                                        expirationDate = formattedExpirationDate,
                                        isDefault = isDefault
                                    )
                                )
                            },
                            enabled = !cardNumberError && !cardHolderNameError && !expirationDateError && !isLoading,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = appColors.secondaryColor
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 0.dp
                            ),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.save),
                                style = typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        Column(
            Modifier
                .fillMaxWidth()
                .padding(padding)
                .verticalScroll(scrollState)
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = appColors.primaryColor)
                }
            }

            AnimatedVisibility(
                visible = animateFields,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(150))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        CustomOutlinedTextField(
                            value = rawCardNumber,
                            onValueChange = { rawCardNumber = it.filter { c -> c.isDigit() }.take(16) },
                            textFieldSize = 64.dp,
                            visualTransformation = CreditCardVisualTransformation(),
                            label = stringResource(R.string.card_number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = cardNumberError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            enabled = !isLoading
                        )

                        if (cardNumberError) {
                            Text(
                                text = if (rawCardNumber.isBlank())
                                    stringResource(R.string.error_card_number_required)
                                else
                                    stringResource(R.string.error_card_number_invalid),
                                color = MaterialTheme.colorScheme.error,
                                style = typography.bodySmall.copy(fontSize = 12.sp),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        CustomOutlinedTextField(
                            value = cardHolderName,
                            onValueChange = { cardHolderName = it },
                            textFieldSize = 64.dp,
                            label = stringResource(R.string.cardholder_name),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = cardHolderNameError,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            enabled = !isLoading
                        )

                        if (cardHolderNameError) {
                            Text(
                                text = stringResource(R.string.error_cardholder_name_required),
                                color = MaterialTheme.colorScheme.error,
                                style = typography.bodySmall.copy(fontSize = 12.sp),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        CustomOutlinedTextField(
                            value = rawExpirationDate,
                            onValueChange = { rawExpirationDate = it.filter { c -> c.isDigit() }.take(4) },
                            textFieldSize = 64.dp,
                            visualTransformation = ExpirationDateVisualTransformation(),
                            label = stringResource(R.string.expiration_date),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = expirationDateError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            placeholder = "MM/YY",
                            enabled = !isLoading
                        )

                        if (expirationDateError) {
                            Text(
                                text = if (rawExpirationDate.isBlank())
                                    stringResource(R.string.error_expiration_date_required)
                                else
                                    stringResource(R.string.error_expiration_date_invalid),
                                color = MaterialTheme.colorScheme.error,
                                style = typography.bodySmall.copy(fontSize = 12.sp),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Checkbox(
                            checked = isDefault,
                            onCheckedChange = { isDefault = it },
                            enabled = !isLoading
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.make_default_payment),
                            style = typography.bodyLarge,
                            color = appColors.textPrimary
                        )
                    }
                }
            }
        }
    }
}

class CreditCardVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(16)
        val formatted = digits.chunked(4).joinToString(" ")
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val spaces = (offset / 4).coerceAtMost(3)
                return (offset + spaces).coerceAtMost(formatted.length)
            }
            override fun transformedToOriginal(offset: Int): Int {
                val groups = offset / 5
                return (offset - groups).coerceAtMost(digits.length)
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

class ExpirationDateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }.take(4)
        val formatted = buildString {
            digits.forEachIndexed { index, c ->
                if (index == 2) append('/')
                append(c)
            }
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset <= 2) offset else (offset + 1).coerceAtMost(formatted.length)
            }
            override fun transformedToOriginal(offset: Int): Int {
                return if (offset <= 2) offset else (offset - 1).coerceAtMost(digits.length)
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

private fun isValidExpirationDate(rawExpirationDateDigits: String): Boolean {
    val formattedDate = buildString {
        rawExpirationDateDigits.forEachIndexed { index, c ->
            if (index == 2) append('/')
            append(c)
        }
    }

    if (!formattedDate.matches(Regex("\\d{2}/\\d{2}"))) return false

    try {
        val (month, year) = formattedDate.split("/")
        val monthInt = month.toInt()
        val yearInt = year.toInt()

        if (monthInt !in 1..12) return false

        val currentYear = java.time.Year.now().value % 100
        val currentMonth = java.time.MonthDay.now().monthValue

        return when {
            yearInt > currentYear -> true
            yearInt == currentYear && monthInt >= currentMonth -> true
            else -> false
        }
    } catch (_: Exception) {
        return false
    }
}