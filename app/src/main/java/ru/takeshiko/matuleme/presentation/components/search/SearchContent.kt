package ru.takeshiko.matuleme.presentation.components.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.models.SearchQueryDto
import ru.takeshiko.matuleme.presentation.components.PrimaryBackground
import ru.takeshiko.matuleme.presentation.components.createShimmerBrush
import ru.takeshiko.matuleme.presentation.components.fields.CustomOutlinedTextField
import ru.takeshiko.matuleme.presentation.theme.rememberAppColors

@Composable
fun SearchContent(
    searchQuery: String,
    isLoading: Boolean,
    searchHistory: List<SearchQueryDto>,
    processingItems: Set<String> = emptySet(),
    onSearchQueryChange: (String) -> Unit,
    onClearHistoryItem: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onNavigateToBack: () -> Unit,
) {
    val appColors = rememberAppColors()

    var animateItems by remember { mutableStateOf(false) }

    LaunchedEffect(searchHistory, isLoading) {
        animateItems = !isLoading
    }

    PrimaryBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    CustomOutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        textFieldSize = 56.dp,
                        placeholder = stringResource(R.string.search),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_history),
                                contentDescription = null,
                                tint = appColors.textSecondary
                            )
                        },
                        trailingIcon = if (searchQuery.isNotEmpty()) {
                            {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_clear),
                                        contentDescription = null,
                                        tint = appColors.textSecondary
                                    )
                                }
                            }
                        } else null,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onSearchSubmit() }),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            color = appColors.textPrimary,
                            fontSize = 16.sp
                        )
                    )
                }

                Text(
                    text = stringResource(R.string.cancel),
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clickable(onClick = onNavigateToBack),
                    fontSize = 16.sp
                )
            }

            if (isLoading) {
                val brush = createShimmerBrush()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(8) {
                        ShimmerSearchHistoryItem(brush = brush)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.05f)),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    itemsIndexed(
                        items = searchHistory,
                        key = { _, historyItem -> "${historyItem.query}-${historyItem.id}" }
                    ) { index, historyItem ->
                        AnimatedVisibility(
                            visible = animateItems,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    delayMillis = 50 * index,
                                    easing = FastOutSlowInEasing
                                )
                            ) + slideInVertically(
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = 50 * index,
                                    easing = FastOutSlowInEasing
                                ),
                                initialOffsetY = { it / 5 }
                            ),
                            exit = fadeOut()
                        ) {
                            SearchHistoryItem(
                                historyItem = historyItem,
                                onItemClick = {
                                    onSearchQueryChange(historyItem.query)
                                    onSearchSubmit()
                                },
                                onClearItem = { onClearHistoryItem(historyItem.query) },
                                isProcessing = processingItems.contains(historyItem.query)
                            )
                        }
                    }
                }
            }
        }
    }
}