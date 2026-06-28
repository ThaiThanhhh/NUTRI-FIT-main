package com.example.nutrifit.ui.screens.onboarding

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nutrifit.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onStart: () -> Unit, viewModel: OnboardingViewModel = viewModel()) {
    val pages by viewModel.pages.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val pagerState = rememberPagerState(initialPage = currentPage) { pages.size }
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setCurrentPage(pagerState.currentPage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = currentPage != 0
        ) { pageIndex ->
            val data = pages[pageIndex]

            if (pageIndex > 0) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                        .offset(y = (-85).dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Image(
                            painter = painterResource(id = data.imageRes),
                            contentDescription = data.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(260.dp)
                                .width(360.dp)
                                .clip(RoundedCornerShape(25.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = (-125).dp)
                    ) {
                        items(3) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .height(6.dp)
                                    .width(17.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (currentPage == index + 1) colorScheme.primary
                                        else colorScheme.outlineVariant
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(60.dp))

                    Text(
                        text = data.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        lineHeight = 28.sp,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        fontSize = 22.sp
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = data.description,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        color = Color.Black.copy(alpha = 0.85f),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = data.imageRes),
                        contentDescription = data.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(230.dp)
                            .width(230.dp)
                            .offset(y = -50.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color(0xFF1AC9AC), fontWeight = FontWeight.Bold)) { append("NUTRI") }
                            withStyle(style = SpanStyle(color = colorScheme.onBackground, fontWeight = FontWeight.Bold)) { append(" - ") }
                            withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) { append("FIT") }
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 46.sp,
                        modifier = Modifier.offset(y = (-60).dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = data.description,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.offset(y = 0.dp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(80.dp))

                    Text(
                        text = "Bấm vào để tiếp tục",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.outline,
                        fontSize = 17.sp,
                        modifier = Modifier.clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(currentPage + 1)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(0.dp))

        val isLast = currentPage == pages.lastIndex

        if (currentPage > 0) {
            Button(
                onClick = {
                    if (isLast) onStart()
                    else scope.launch {
                        pagerState.animateScrollToPage(currentPage + 1)
                    }
                },
                colors = if (isLast) {
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3475B7),
                        contentColor = Color.White
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                },
                shape = RoundedCornerShape(8.dp),
                border = if (isLast) {
                    BorderStroke(1.dp, colorScheme.outlineVariant)
                } else {
                    BorderStroke(1.dp, colorScheme.onBackground)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 0.dp)
                    .offset(y = (-24).dp)
            ) {
                Text(
                    text = if (isLast) "Bắt đầu" else "Tiếp tục",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tôi đã có tài khoản",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onStart() }
            )
        }
    }
}
