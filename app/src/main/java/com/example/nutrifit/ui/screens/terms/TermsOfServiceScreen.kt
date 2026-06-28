package com.example.nutrifit.ui.screens.terms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreen(navController: NavController) {

    val termsContent = listOf(
        "1. Giới thiệu" to "Chào mừng bạn đến với ứng dụng tập luyện và ăn uống của chúng tôi.\nBằng cách truy cập hoặc sử dụng ứng dụng, bạn đồng ý tuân thủ tất cả các điều khoản được đề cập dưới đây.\n\nNếu bạn không đồng ý với bất kỳ điều khoản nào, vui lòng ngừng sử dụng ứng dụng.",
        "2. Mục đích của ứng dụng" to "Ứng dụng được tạo nhằm:\n\n- Hỗ trợ người dùng theo dõi quá trình tập luyện.\n- Gợi ý chế độ ăn uống phù hợp mục tiêu cá nhân.\n- Cung cấp công cụ quản lý kế hoạch luyện tập và dinh dưỡng.\n\nỨng dụng không thay thế lời khuyên chuyên môn từ các chuyên gia y tế hoặc huấn luyện viên chuyên nghiệp.",
        "3. Thông tin sức khỏe & trách nhiệm người dùng" to "Khi sử dụng ứng dụng, bạn xác nhận rằng:\n\n- Bạn tự chịu trách nhiệm về sức khỏe của mình.\n- Bạn sẽ dừng tập luyện nếu cảm thấy đau, khó chịu hoặc có dấu hiệu bất thường.\n- Bạn sẽ tham khảo ý kiến bác sĩ trước khi bắt đầu bất kỳ chương trình luyện tập hoặc chế độ ăn nào, đặc biệt nếu bạn có vấn đề sức khỏe.\n- Bạn cung cấp thông tin cá nhân trung thực (tuổi, cân nặng, tình trạng sức khỏe…) để ứng dụng đề xuất nội dung phù hợp.",
        "4. Không đưa ra lời khuyên y tế" to "Mọi nội dung trong ứng dụng — bao gồm bài tập, thực đơn, bài viết, gợi ý — chỉ mang tính tham khảo.\n\nỨng dụng không cung cấp:\n\n- Chẩn đoán y khoa\n- Điều trị bệnh\n- Hướng dẫn thay thế chuyên gia dinh dưỡng hoặc bác sĩ\n\nNgười dùng cần tự đánh giá mức độ phù hợp của các bài tập hoặc thực đơn.",
        "5. Quyền riêng tư & bảo mật dữ liệu" to "Chúng tôi cam kết:\n\n- Bảo mật thông tin cá nhân của bạn\n- Không chia sẻ dữ liệu của bạn cho bên thứ ba nếu không có sự đồng ý\n- Chỉ sử dụng dữ liệu để cải thiện trải nghiệm và chức năng của ứng dụng\n\nDữ liệu bạn nhập vào ứng dụng (cân nặng, chiều cao, bài tập, ăn uống…) sẽ được xử lý an toàn theo chính sách của chúng tôi.",
        "6. Sử dụng hợp lệ" to "Người dùng cam kết không sử dụng ứng dụng cho các mục đích:\n\n- Vi phạm pháp luật\n- Tung tin sai sự thật\n- Can thiệp, phá hoại hệ thống hoặc gây hại cho dữ liệu người khác\n- Sử dụng bot hoặc khai thác lỗi của ứng dụng\n\nChúng tôi có quyền khóa tài khoản hoặc hạn chế truy cập nếu phát hiện hành vi không hợp lệ.",
        "7. Giới hạn trách nhiệm" to "Chúng tôi không chịu trách nhiệm cho:\n\n- Chấn thương, đau đớn hoặc tổn hại xảy ra khi bạn tập luyện\n- Kết quả không đạt như mong đợi (giảm cân, tăng cơ, thể trạng…)\n- Tổn thất phát sinh từ lỗi kết nối, dữ liệu hoặc gián đoạn dịch vụ\n\nBạn sử dụng ứng dụng hoàn toàn tự nguyện và tự chịu rủi ro.",
        "8. Thay đổi nội dung" to "Chúng tôi có thể cập nhật Điều khoản Sử dụng theo thời gian.\nKhi có thay đổi, thông báo sẽ được hiển thị trên ứng dụng.\n\nViệc tiếp tục sử dụng ứng dụng đồng nghĩa với việc bạn chấp nhận phiên bản điều khoản mới nhất."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Điều khoản sử dụng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4F66FF), titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillParentMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Cập nhật lần cuối: 15/11/2025",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(termsContent.size) { index ->
                val (title, content) = termsContent[index]
                Card(
                    modifier = Modifier.fillParentMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF3B66FF)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = content,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
