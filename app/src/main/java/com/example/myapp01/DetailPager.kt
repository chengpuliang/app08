package com.example.myapp01

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailPager(viewModel: MainViewModel, city: List<City>, initPage: Int) {
    val pagerState = rememberPagerState(pageCount = {
        city.count()
    }, initialPage = initPage)
    val weatherList = remember(city) {
        city.map { c ->
            viewModel.getWeatherData(c.fileName.dropLast(4))
        }
    }
    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            DetailScreen(weatherList[page])
        }
        Row(
            modifier = Modifier
                .height(50.dp)
                .background(Color.White)
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                city.forEachIndexed { index, it ->
                    if (it.fileName == "current.xml") {
                        Icon(
                            painter = painterResource(R.drawable.baseline_my_location_24),
                            "",
                            tint = if (index == pagerState.currentPage) Color.Black else Color.LightGray,
                            modifier = Modifier
                                .padding(3.dp)
                                .size(15.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(if (index == pagerState.currentPage) Color.Black else Color.LightGray)
                                .size(10.dp)
                        )
                    }
                }
            }
            Icon(
                painter = painterResource(R.drawable.baseline_list_24),
                "",
                modifier = Modifier.clickable {
                    viewModel.pop()
                }
            )

        }
    }
}

@Preview
@Composable
fun DetailPagerPreview() {
    val context = LocalContext.current
    val city = parseCityXml(context.resources.getXml(R.xml.city_list))
    DetailPager(MainViewModel().apply { initialize(LocalContext.current) }, city, 0)
}