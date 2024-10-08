package jp.speakbuddy.edisonandroidexercise.presentation.fact

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import jp.speakbuddy.edisonandroidexercise.R
import jp.speakbuddy.edisonandroidexercise.presentation.commons.ErrorContent
import jp.speakbuddy.edisonandroidexercise.presentation.commons.TheResult
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.ui.unit.sp
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.draw.scale
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactScreen(
    viewModel: FactViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    // Move showTranslation state to this level
    var showTranslation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                actions = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        LanguageSelector(
                            onLanguageSelected = { source, target ->
                                viewModel.setLanguages(source, target)
                                viewModel.setSelectedLanguage(target)
                            },
                            uiState = (uiState as? TheResult.Success)?.data ?: FactUiState(),
                            viewModel = viewModel,
                            selectedLanguage = selectedLanguage
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("savedFacts") }) {
                        Icon(Icons.Default.List, contentDescription = "Saved Facts")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is TheResult.Loading -> LoadingContent()
                is TheResult.Success -> StoryContent(
                    state.data,
                    viewModel,
                    showTranslation,
                    onToggleTranslation = { showTranslation = !showTranslation }
                )

                is TheResult.Error -> ErrorContent(state.message)
            }
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun StoryContent(
    uiState: FactUiState,
    viewModel: FactViewModel,
    showTranslation: Boolean,
    onToggleTranslation: () -> Unit
) {
    var showQuiz by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StoryCard(uiState, true, uiState.targetLanguage) // Always show translation
        }
        item {
            Button(
                onClick = { showQuiz = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.take_quiz))
            }
        }
        if (showQuiz) {
            item {
                QuizSection(
                    uiState.translationText,
                    viewModel,
                    onQuizComplete = { showQuiz = false }
                )
            }
        }
        item {
            Button(
                onClick = { viewModel.updateFact() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.next_story))
            }
        }
    }
}

@Composable
fun StoryCard(uiState: FactUiState, showTranslation: Boolean, selectedLanguage: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.fact_of_the_day),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = uiState.fact,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            )
            if (uiState.showLength) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.length_characters, uiState.fact.length),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            if (uiState.showMultipleCats) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.multiple_cats),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Remove the if (showTranslation) check and always show the translation
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.translation),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.translation_language, selectedLanguage),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.translationText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 22.sp
            )

            if (uiState.showMultipleCats) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.multiple_cats),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageSelector(
    onLanguageSelected: (String, String) -> Unit,
    uiState: FactUiState,
    viewModel: FactViewModel,
    selectedLanguage: String
) {
    val languages = viewModel.getFlagCodes().keys.toList()
    var currentLanguage by remember(selectedLanguage) { mutableStateOf(selectedLanguage) }

    LaunchedEffect(Unit) {
        if (currentLanguage.isEmpty() && languages.isNotEmpty()) {
            currentLanguage = languages.first()
            onLanguageSelected(uiState.sourceLanguage, currentLanguage)
        }
    }

    SimpleLanguageDropdown(
        selectedLanguage = currentLanguage,
        onLanguageSelected = { lang ->
            currentLanguage = lang
            onLanguageSelected(uiState.sourceLanguage, lang)
        },
        languages = languages,
        label = "To",
        viewModel = viewModel
    )
}

@Composable
fun SimpleLanguageDropdown(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    languages: List<String>,
    label: String,
    viewModel: FactViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val flagCodes = viewModel.getFlagCodes()

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { expanded = true }
                .padding(8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 4.dp)
            )
            AsyncImage(
                model = "https://flagsapi.com/${flagCodes[selectedLanguage]}/flat/64.png",
                contentDescription = selectedLanguage,
                modifier = Modifier.size(24.dp)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand",
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(120.dp)
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = "https://flagsapi.com/${flagCodes[language]}/flat/64.png",
                                contentDescription = language,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(language)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun QuizSection(fact: String, viewModel: FactViewModel, onQuizComplete: () -> Unit) {
    val quizState by viewModel.quizState.collectAsState()
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    LaunchedEffect(fact) {
        viewModel.getQuiz(fact)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.quick_quiz),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            when (val state = quizState) {
                is TheResult.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is TheResult.Success -> {
                    val quiz = state.data
                    Text(
                        text = quiz.question,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    QuizOptions(
                        options = quiz.options,
                        selectedOption = selectedOption ?: -1,
                        onOptionSelected = { selectedOption = it }
                    )
                    
                    AnimatedVisibility(
                        visible = !showResult,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Button(
                            onClick = {
                                selectedOption?.let { option ->
                                    isCorrect = quiz.options[option] == quiz.answer
                                    showResult = true
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            enabled = selectedOption != null
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.submit),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.submit))
                        }
                    }

                    AnimatedVisibility(
                        visible = showResult,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        ResultAnimation(isCorrect = isCorrect) {
                            showResult = false
                            selectedOption = null  // Reset selected option
                            onQuizComplete()
                        }
                    }
                }
                is TheResult.Error -> {
                    Text(
                        text = stringResource(R.string.failed_to_load_quiz, state.message),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizOptions(
    options: List<String>,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit
) {
    options.forEachIndexed { index, option ->
        QuizOption(
            text = option,
            selected = selectedOption == index,
            onSelect = { onOptionSelected(index) }
        )
        if (index < options.lastIndex) {
            Spacer(modifier = Modifier.height(4.dp)) // Reduced from 8.dp to 4.dp
        }
    }
}

@Composable
fun QuizOption(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) 
                MaterialTheme.colorScheme.secondaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp), // Reduced vertical padding from 16.dp to 8.dp
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) 
                    MaterialTheme.colorScheme.onSecondaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ResultAnimation(isCorrect: Boolean, onAnimationComplete: () -> Unit) {
    var animationPlayed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.3f, stiffness = 300f)
    )
    
    val context = LocalContext.current
    val soundResId = if (isCorrect) R.raw.raw_correct else R.raw.raw_wrong

    DisposableEffect(isCorrect) {
        val mediaPlayer = MediaPlayer.create(context, soundResId)
        mediaPlayer.start()

        onDispose {
            mediaPlayer.release()
        }
    }

    LaunchedEffect(key1 = true) {
        animationPlayed = true
        delay(2000) // Wait for 2 seconds before closing
        onAnimationComplete()
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
            contentDescription = if (isCorrect) "Correct" else "Incorrect",
            tint = if (isCorrect) Color.Green else Color.Red,
            modifier = Modifier
                .size(100.dp)
                .scale(scale)
        )
    }
}