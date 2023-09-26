package news

import BottomTabBar
import Variables.ColorsOnPrimary
import Variables.ColorsOnPrimaryVariant
import Variables.ColorsPrimary
import Variables.ColorsPrimaryVariant
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun News(navigator: Navigator) {
    Contents()
    BottomTabBar(navigator)
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Contents() {

    val categories: List<NewsCategory> = (
                listOf(
                    NewsCategory(0, "삼성", "samsung.png"),
                    NewsCategory(1, "현대차", "hyundai.png"),
                    NewsCategory(2, "한화", "hanhwa.png"),
                    NewsCategory(3, "네이버", "naver.png"),
                    NewsCategory(4, "SM엔터", "sm.png"),
                    ))

    var idx by remember {mutableStateOf(0)}

    Column {
        TabRow(
            selectedTabIndex = idx,
            backgroundColor = ColorsPrimary,
            contentColor = ColorsOnPrimary,
            modifier = Modifier
                .height(80.dp)
        ) {
            categories.map { category ->
                Tab(
                    selected = idx == category.idx,
                    onClick = { idx = category.idx }

                ) {
                    Image(
                        painterResource("drawable/logo_" + category.imgSrc),
                        "",
                        modifier = Modifier.height(32.dp).width(40.dp)
                    )
                    Text(category.brand, fontSize = 12.sp)
                }
            }
        }
        var searchStr by remember {mutableStateOf("")}

        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(color = ColorsPrimary)
                .padding(12.dp)
        ) {
            TextField(
                value = searchStr,
                onValueChange = { searchStr = it },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = "", tint = Color(0x66FFFFFF)) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color(0xFF000000),
                    backgroundColor = Color(0x66212121),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    trailingIconColor = ColorsOnPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        val newsListViewModel: NewsViewModel = viewModel(NewsViewModel::class, keys = listOf(idx.toString() + searchStr)) {
            NewsViewModel()
        }

        NewsList(newsListViewModel, searchStr)
    }
}

@Composable
fun NewsList(viewModel: NewsViewModel, searchStr: String) {
    LazyColumn (
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .padding(8.dp)

    ) {
        items(viewModel.newses) {newsItem ->
            if (newsItem.title.contains(searchStr)) {
                Text(newsItem.title, fontSize = 20.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        Text(newsItem.date, color = Color(0xFF9E9E9E))
                        Text(newsItem.time, color = Color(0xFF9E9E9E))
                    }
                    Text("|", color = Color(0xFF9E9E9E))
                    Text(newsItem.publisher, color = Color(0xFF9E9E9E))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0x22000000))
            }
        }
    }
}