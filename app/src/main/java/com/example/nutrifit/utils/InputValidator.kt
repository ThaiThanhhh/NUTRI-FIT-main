package com.example.nutrifit.utils

import android.util.Patterns

/**
 * Lớp tiện ích để kiểm tra dữ liệu đầu vào.
 */
object InputValidator {

    /**
     * Kiểm tra xem một chuỗi có phải là địa chỉ email hợp lệ hay không.
     * @param email Chuỗi email cần kiểm tra.
     * @return `true` nếu email hợp lệ, ngược lại `false`.
     */
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Kiểm tra mật khẩu có đủ độ dài tối thiểu hay không.
     *
     * @param password Mật khẩu cần kiểm tra.
     * @return `true` nếu mật khẩu có ít nhất 6 ký tự, ngược lại `false`.
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Kiểm tra một chuỗi có rỗng hoặc chỉ chứa khoảng trắng hay không.
     * @param field Chuỗi cần kiểm tra.
     * @return `true` nếu trường không rỗng, ngược lại `false`.
     */
    fun isFieldNotEmpty(field: String): Boolean {
        return field.isNotBlank()
    }
}
