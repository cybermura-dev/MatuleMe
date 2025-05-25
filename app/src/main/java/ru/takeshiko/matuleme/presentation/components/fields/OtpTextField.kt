package ru.takeshiko.matuleme.presentation.components.fields

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OtpTextField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onComplete: (String) -> Unit = {}
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val focusManager = LocalFocusManager.current
    val length = 6

    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until length) {
            val char = otpValue.getOrNull(i)?.toString() ?: ""
            CompositionLocalProvider(
                LocalTextStyle provides LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
            ) {
                CustomOutlinedTextField(
                    value = char,
                    onValueChange = { value ->
                        val filteredValue = value.filter { it.isDigit() }.take(1)
                        val currentOtpList = otpValue.toMutableList()

                        if (filteredValue.isNotEmpty()) {
                            while (currentOtpList.size <= i) {
                                currentOtpList.add(' ')
                            }
                            currentOtpList[i] = filteredValue.first()

                            val newOtp = currentOtpList.joinToString("")

                            onOtpChange(newOtp)

                            if (i < length - 1) {
                                focusRequesters[i + 1].requestFocus()
                            } else {
                                focusManager.clearFocus()
                            }

                            if (newOtp.trim().length == length) {
                                onComplete(newOtp.trim())
                            }

                        } else if (value.isEmpty() && i < currentOtpList.size && currentOtpList[i] != ' ') {
                            currentOtpList[i] = ' '

                            val newOtp = currentOtpList.joinToString("")

                            onOtpChange(newOtp)

                            if (i > 0) {
                                focusRequesters[i - 1].requestFocus()
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .focusRequester(focusRequesters[i]),
                    textFieldSize = 80.dp,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        }
    }
}
