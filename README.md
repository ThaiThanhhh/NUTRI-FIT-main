
# NUTRI-FIT

Một ứng dụng di động dành cho Android giúp bạn theo dõi chế độ dinh dưỡng và luyện tập thể dục, được xây dựng bằng Kotlin và Jetpack Compose.

## Giới thiệu

NUTRI-FIT là một người bạn đồng hành lý tưởng cho những ai muốn xây dựng một lối sống lành mạnh. Ứng dụng cho phép người dùng theo dõi lượng calo hàng ngày, quản lý các bài tập và đặt ra các mục tiêu sức khỏe cá nhân.

## Các chức năng chính

-   🔑 **Đăng nhập & Đăng ký:**
    -   Đăng ký và đăng nhập bằng Email và Mật khẩu.
    -   Đăng nhập nhanh chóng qua Google và GitHub.
    -   Xác thực email và chức năng quên mật khẩu.
-   👤 **Thiết lập hồ sơ:**
    -   Người dùng mới có thể tạo hồ sơ cá nhân (tên, thông tin cơ bản).
    -   Thiết lập các mục tiêu sức khỏe (ví dụ: giảm cân, tăng cơ).
-   🥗 **Theo dõi dinh dưỡng:**
    -   Ghi lại các bữa ăn hàng ngày (sáng, trưa, tối).
    -   Xem chi tiết thành phần dinh dưỡng của từng món ăn.
    -   Sử dụng ML Kit để nhận dạng món ăn qua hình ảnh.
-   💪 **Theo dõi luyện tập:**
    -   Tạo và quản lý các kế hoạch luyện tập.
    -   Truy cập thư viện các bài tập chi tiết với hướng dẫn.
    -   Theo dõi tiến độ và lịch sử luyện tập.
-   🏠 **Màn hình chính:**
    -   Bảng điều khiển tổng quan về lượng calo đã tiêu thụ và mục tiêu trong ngày.
    -   Truy cập nhanh đến các chức năng chính.

## Công nghệ sử dụng

-   **Ngôn ngữ:** [Kotlin](https://kotlinlang.org/)
-   **Giao diện người dùng:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
-   **Kiến trúc:** MVVM (Model-View-ViewModel)
-   **Asynchronous:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
-   **Backend & Authentication:** [Firebase Authentication](https://firebase.google.com/docs/auth)
-   **Cơ sở dữ liệu:** [Firebase Firestore](https://firebase.google.com/docs/firestore) để lưu trữ dữ liệu người dùng và [Room](https://developer.android.com/jetpack/androidx/releases/room) để lưu trữ dữ liệu offline.
-   **Networking:** [Retrofit](https://square.github.io/retrofit/) để giao tiếp với API.
-   **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
-   **Machine Learning:** [Google ML Kit](https://developers.google.com/ml-kit) (Image Labeling) để nhận diện thực phẩm.

## Cài đặt

1.  Clone repository về máy của bạn:
    ```bash
    git clone https://github.com/your-username/NUTRI-FIT.git
    ```
2.  Mở dự án bằng Android Studio.
3.  Để sử dụng các dịch vụ của Firebase (đăng nhập, cơ sở dữ liệu), bạn cần tạo một dự án trên [Firebase Console](https://console.firebase.google.com/) và tải file `google-services.json` đặt vào thư mục `app/` của dự án.
4.  Build và chạy ứng dụng.

