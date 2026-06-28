package com.example.nutrifit.viewmodel

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.nutrifit.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int,
    val titleColor: Color = Color.Black
)

class OnboardingViewModel : ViewModel() {

    private val _pages = MutableStateFlow(listOf(
        OnboardingPage(
            title = "NUTRI - FIT",
            description = "  Ăn uống lành mạnh\n Luyện tập thông minh",
            imageRes = R.drawable.logo,
            titleColor = Color(0xFF1AC9AC)
        ),
        OnboardingPage(
            title = "      Tập luyện hiệu quả\n Video hướng dẫn chi tiết",
            description = "Chọn nhóm cơ bạn muốn tập và theo dõi các video hướng dẫn bài bản, giúp bạn rèn luyện đúng kỹ thuật và đạt được kết quả mong muốn",
            imageRes = R.drawable.rectangle_59
        ),
        OnboardingPage(
            title = "    Ăn uống khoa học\n Gợi ý bữa ăn phù hợp",
            description = "Ứng dụng gợi ý bữa ăn lành mạnh, cân bằng dinh dưỡng và hỗ trợ kiểm soát calo, giúp bạn duy trì năng lượng và cải thiện sức khỏe",
            imageRes = R.drawable.intro2
        ),
        OnboardingPage(
            title = "               Sống cân bằng\n Lối sống khoẻ mạnh mỗi ngày",
            description = "Kết hợp tập luyện và dinh dưỡng thông minh để tạo nên một lối sống lành mạnh, tăng cường sức đề kháng và nâng cao chất lượng cuộc sống",
            imageRes = R.drawable.intro3
        )
    ))
    val pages: StateFlow<List<OnboardingPage>> = _pages.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    fun goToNextPage() {
        if (_currentPage.value < _pages.value.lastIndex) {
            _currentPage.value++
        }
    }
    fun setCurrentPage(page: Int){
        _currentPage.value = page
    }
}
