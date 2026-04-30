package com.example.playlistmaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.alltracks.TrackListItem
import com.example.playlistmaker.ui.search.SearchState
import com.example.playlistmaker.ui.search.SearchViewModel
import com.example.playlistmaker.ui.theme.AppAccent
import com.example.playlistmaker.ui.theme.AppPrimary
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

private enum class SearchPlaceholderType {
    NoResults,
    ConnectionError
}

@Composable
fun SearchScreen(
    innerPadding: PaddingValues,
    viewModel: SearchViewModel,
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit = {}
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    val historyList by viewModel.historyList.collectAsState(emptyList())
    var text by rememberSaveable { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(text) {
        viewModel.updateQuery(text)
    }

    val showHistory = isFocused && text.isEmpty() && historyList.isNotEmpty()

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        ScreenHeader(
            title = stringResource(R.string.search_title),
            onBackClick = onBackClick
        )

        SearchField(
            value = text,
            showAttachedContent = showHistory,
            onValueChange = { text = it },
            onFocusChanged = { isFocused = it },
            onClearClick = {
                text = ""
                viewModel.clearSearch()
            }
        )

        when {
            showHistory -> {
                SearchHistoryBlock(
                    historyList = historyList,
                    onClick = { query ->
                        text = query
                        isFocused = false
                    }
                )
            }

            screenState is SearchState.Searching -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppAccent)
                }
            }

            screenState is SearchState.Success && (screenState as SearchState.Success).list.isNotEmpty() -> {
                val tracks = (screenState as SearchState.Success).list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(
                        items = tracks,
                        key = { track -> track.id }
                    ) { track ->
                        TrackListItem(
                            track = track,
                            onClick = { onTrackClick(track) }
                        )
                    }
                }
            }

            screenState is SearchState.Success && text.isNotBlank() -> {
                SearchPlaceholder(
                    type = SearchPlaceholderType.NoResults,
                    title = stringResource(R.string.search_no_songs_found)
                )
            }

            screenState is SearchState.Fail -> {
                SearchPlaceholder(
                    type = SearchPlaceholderType.ConnectionError,
                    title = stringResource(R.string.search_error_title),
                    message = stringResource(R.string.search_server_error),
                    actionText = stringResource(R.string.search_retry),
                    onActionClick = viewModel::retrySearch
                )
            }

            else -> Unit
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    showAttachedContent: Boolean,
    onValueChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onClearClick: () -> Unit
) {
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val fieldColor = if (isDarkTheme) Color.White else Color(0xFFE1E4EA)
    val contentColor = AppPrimary
    val shape = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 8.dp,
        bottomStart = if (showAttachedContent) 0.dp else 8.dp,
        bottomEnd = if (showAttachedContent) 0.dp else 8.dp
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = fieldColor,
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.search_icon_content_description),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { onFocusChanged(it.isFocused) }
                    .padding(vertical = 8.dp),
                singleLine = true,
                textStyle = TextStyle(
                    color = contentColor,
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                ),
                cursorBrush = SolidColor(AppAccent),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                color = if (isDarkTheme) contentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        innerTextField()
                    }
                }
            )
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = onClearClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(R.string.clear_search_content_description),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchHistoryBlock(
    historyList: List<String>,
    onClick: (String) -> Unit
) {
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val blockColor = if (isDarkTheme) Color.White else Color(0xFFE1E4EA)
    val visibleHistory = historyList.take(3)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-8).dp),
        color = blockColor,
        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 164.dp)
        ) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            )
            visibleHistory.forEachIndexed { index, word ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(word) }
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = word,
                        color = AppPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1
                    )
                }
                if (index != visibleHistory.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchPlaceholder(
    type: SearchPlaceholderType,
    title: String,
    message: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.padding(top = 108.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchPlaceholderIllustration(type = type)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            if (message != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(312.dp)
                )
            }
            if (actionText != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.width(168.dp)) {
                    AppActionButton(
                        text = actionText,
                        onClick = onActionClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchPlaceholderIllustration(type: SearchPlaceholderType) {
    val accentColor = when (type) {
        SearchPlaceholderType.NoResults -> Color(0xFFFCD452)
        SearchPlaceholderType.ConnectionError -> AppAccent
    }

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(44.dp))
                .background(accentColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (type) {
                    SearchPlaceholderType.NoResults -> Icons.Filled.SentimentDissatisfied
                    SearchPlaceholderType.ConnectionError -> Icons.Filled.WifiOff
                },
                contentDescription = null,
                tint = if (type == SearchPlaceholderType.NoResults) AppPrimary else Color.White,
                modifier = Modifier.size(52.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchPlaceholderLightPreview() {
    PlaylistMakerTheme {
        SearchPlaceholder(
            type = SearchPlaceholderType.NoResults,
            title = "Ничего не нашлось"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchPlaceholderDarkPreview() {
    PlaylistMakerTheme(darkTheme = true) {
        SearchPlaceholder(
            type = SearchPlaceholderType.ConnectionError,
            title = "Проблемы со связью",
            message = "Загрузка не удалась. Проверьте подключение к интернету"
        )
    }
}
