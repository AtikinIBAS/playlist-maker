package com.example.playlistmaker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.alltracks.TrackListItem
import com.example.playlistmaker.ui.search.SearchState
import com.example.playlistmaker.ui.search.SearchViewModel
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

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
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(text) {
        viewModel.updateQuery(text)
    }

    LaunchedEffect(screenState) {
        if (screenState is SearchState.Success) {
            focusManager.clearFocus()
        }
    }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        ScreenHeader(
            title = stringResource(R.string.search_title),
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { newText -> text = newText },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState -> isFocused = focusState.isFocused },
                placeholder = {
                    Text(stringResource(R.string.search_placeholder))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.search_icon_content_description)
                    )
                },
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                text = ""
                                focusManager.clearFocus()
                                viewModel.clearSearch()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = stringResource(R.string.clear_search_content_description)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            if (isFocused && text.isEmpty() && historyList.isNotEmpty()) {
                HistoryRequests(
                    historyList = historyList,
                    onClick = { word ->
                        text = word
                    }
                )
            }

            when (val state = screenState) {
                SearchState.Initial -> {
                    if (text.isEmpty()) {
                        EmptyState(
                            text = stringResource(R.string.search_prompt),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                SearchState.Searching -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is SearchState.Success -> {
                    if (state.list.isEmpty()) {
                        PlaceholderState(
                            imageRes = R.drawable.ic_music,
                            message = stringResource(R.string.search_no_songs_found)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.list) { track ->
                                TrackListItem(
                                    track = track,
                                    onClick = { onTrackClick(track) }
                                )
                            }
                        }
                    }
                }

                is SearchState.Fail -> {
                    PlaceholderState(
                        imageRes = R.drawable.ic_music,
                        message = stringResource(R.string.search_server_error),
                        buttonText = stringResource(R.string.search_retry)
                    ) { viewModel.retrySearch() }
                }
            }
        }
    }
}

@Composable
private fun HistoryRequests(
    historyList: List<String>,
    onClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 220.dp)
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(historyList) { word ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(word) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = word)
                }
            }
        }
    }
}

@Composable
private fun PlaceholderState(
    imageRes: Int,
    message: String,
    buttonText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.size(88.dp)
            )
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (buttonText != null && onActionClick != null) {
                AppActionButton(
                    text = buttonText,
                    onClick = onActionClick
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SearchScreenPreview() {
    val sampleTrack = remember {
        Track(
            trackName = "Yesterday",
            artistName = "The Beatles",
            trackTime = "2:38"
        )
    }

    PlaylistMakerTheme {
        Column {
            Text(
                text = "Search",
                style = MaterialTheme.typography.headlineSmall
            )
            TrackListItem(track = sampleTrack)
        }
    }
}
