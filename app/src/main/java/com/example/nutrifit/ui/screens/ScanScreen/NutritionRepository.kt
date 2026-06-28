package com.example.nutrifit.ui.screens.ScanScreen

data class NutritionInfo(
    val weight: String,
    val calories: String,
    val protein: String,
    val carbs: String,
    val fat: String,
    val fiber: String
)

object NutritionRepository {
    // DATABASE MỞ RỘNG - PHONG PHÚ VÀ ĐA DẠNG
    private val nutritionMap: Map<String, NutritionInfo> = mapOf(
        // === MÓN VIỆT NAM TRUYỀN THỐNG ===

        // PHỞ & BÚN
        "phở bò" to NutritionInfo("350g", "350-420", "30g", "45g", "8g", "3g"),
        "phở gà" to NutritionInfo("350g", "320-380", "28g", "40g", "6g", "3g"),
        "bún chả" to NutritionInfo("400g", "450-520", "25g", "55g", "15g", "4g"),
        "bún bò huế" to NutritionInfo("400g", "480-550", "35g", "50g", "18g", "4g"),
        "bún riêu" to NutritionInfo("350g", "380-450", "20g", "45g", "12g", "5g"),
        "bún đậu mắm tôm" to NutritionInfo("300g", "420-500", "25g", "35g", "20g", "3g"),
        "bún mắm" to NutritionInfo("400g", "400-480", "30g", "40g", "15g", "5g"),

        // MÌ & MIẾN
        "mì quảng" to NutritionInfo("350g", "380-450", "22g", "48g", "12g", "4g"),
        "mì xào" to NutritionInfo("300g", "420-500", "18g", "55g", "15g", "3g"),
        "mì tôm" to NutritionInfo("200g", "350-400", "10g", "45g", "15g", "2g"),
        "miến gà" to NutritionInfo("300g", "280-350", "20g", "35g", "8g", "2g"),
        "miến xào" to NutritionInfo("250g", "300-380", "15g", "40g", "12g", "3g"),

        // CƠM & XÔI
        "cơm tấm" to NutritionInfo("450g", "550-650", "35g", "60g", "20g", "4g"),
        "cơm gà" to NutritionInfo("400g", "480-550", "40g", "50g", "15g", "3g"),
        "cơm rang" to NutritionInfo("350g", "420-500", "18g", "55g", "16g", "3g"),
        "cơm niêu" to NutritionInfo("200g", "260-300", "5g", "55g", "1g", "1g"),
        "xôi mặn" to NutritionInfo("200g", "380-450", "15g", "50g", "12g", "3g"),
        "xôi ngọt" to NutritionInfo("150g", "320-380", "8g", "55g", "8g", "2g"),
        "xôi xéo" to NutritionInfo("180g", "350-400", "10g", "52g", "10g", "3g"),

        // BÁNH TRUYỀN THỐNG
        "bánh mì thịt" to NutritionInfo("200g", "380-450", "20g", "45g", "15g", "3g"),
        "bánh mì chảo" to NutritionInfo("250g", "450-520", "25g", "40g", "20g", "4g"),
        "bánh cuốn" to NutritionInfo("200g", "280-350", "15g", "40g", "8g", "3g"),
        "bánh xèo" to NutritionInfo("250g", "420-500", "15g", "35g", "22g", "4g"),
        "bánh khọt" to NutritionInfo("150g", "320-380", "12g", "25g", "18g", "2g"),
        "bánh bèo" to NutritionInfo("200g", "250-300", "8g", "45g", "6g", "2g"),
        "bánh nậm" to NutritionInfo("150g", "200-250", "10g", "30g", "5g", "2g"),
        "bánh bột lọc" to NutritionInfo("150g", "220-280", "12g", "35g", "6g", "1g"),
        "bánh chưng" to NutritionInfo("200g", "450-520", "15g", "60g", "15g", "3g"),
        "bánh tét" to NutritionInfo("200g", "420-480", "12g", "55g", "16g", "3g"),
        "bánh giò" to NutritionInfo("180g", "280-350", "18g", "30g", "10g", "2g"),

        // CHÁO & SÚP
        "cháo lòng" to NutritionInfo("300g", "280-350", "25g", "25g", "12g", "2g"),
        "cháo sườn" to NutritionInfo("300g", "250-300", "20g", "30g", "8g", "2g"),
        "cháo gà" to NutritionInfo("300g", "220-280", "25g", "20g", "6g", "2g"),
        "súp cua" to NutritionInfo("250g", "150-200", "12g", "15g", "8g", "2g"),
        "súp măng gà" to NutritionInfo("250g", "180-230", "18g", "12g", "10g", "3g"),

        // GỎI & NỘM
        "gỏi đu đủ" to NutritionInfo("200g", "180-230", "15g", "20g", "8g", "5g"),
        "gỏi ngó sen" to NutritionInfo("180g", "150-200", "12g", "18g", "6g", "4g"),
        "gỏi bò" to NutritionInfo("200g", "220-280", "25g", "15g", "10g", "3g"),
        "nộm hoa chuối" to NutritionInfo("150g", "120-160", "8g", "20g", "4g", "4g"),
        "gỏi cuốn" to NutritionInfo("80g", "120-150", "8g", "15g", "3g", "2g"),
        "bì cuốn" to NutritionInfo("80g", "150-180", "10g", "12g", "8g", "1g"),

        // NEM & CHẢ
        "nem rán" to NutritionInfo("50g", "180-220", "8g", "12g", "15g", "1g"),
        "nem nướng" to NutritionInfo("60g", "200-250", "15g", "8g", "16g", "1g"),
        "chả giò" to NutritionInfo("50g", "180-220", "8g", "12g", "15g", "1g"),
        "chả lụa" to NutritionInfo("100g", "150-180", "20g", "5g", "8g", "0g"),
        "chả chiên" to NutritionInfo("100g", "220-280", "25g", "8g", "15g", "1g"),

        // LẨU
        "lẩu thái" to NutritionInfo("500g", "380-450", "35g", "25g", "20g", "6g"),
        "lẩu nấm" to NutritionInfo("450g", "250-300", "20g", "20g", "12g", "8g"),
        "lẩu bò" to NutritionInfo("550g", "450-520", "40g", "15g", "25g", "5g"),
        "lẩu hải sản" to NutritionInfo("500g", "350-420", "35g", "18g", "18g", "4g"),

        // CHÈ & ĐỒ NGỌT
        "chè đậu đen" to NutritionInfo("200g", "220-280", "8g", "40g", "5g", "6g"),
        "chè ba màu" to NutritionInfo("200g", "250-300", "4g", "50g", "6g", "2g"),
        "chè khúc bạch" to NutritionInfo("150g", "180-230", "6g", "30g", "5g", "1g"),
        "chè thái" to NutritionInfo("200g", "200-250", "3g", "45g", "4g", "2g"),
        "chè chuối" to NutritionInfo("200g", "230-280", "3g", "48g", "5g", "3g"),
        "rau câu" to NutritionInfo("100g", "80-120", "2g", "20g", "0g", "1g"),
        "thạch dừa" to NutritionInfo("150g", "120-160", "1g", "28g", "3g", "2g"),

        // MÓN ĂN VẶT
        "bánh tráng trộn" to NutritionInfo("150g", "280-350", "8g", "45g", "12g", "4g"),
        "bánh tráng nướng" to NutritionInfo("100g", "220-280", "6g", "30g", "10g", "2g"),
        "bánh căn" to NutritionInfo("150g", "250-300", "10g", "35g", "8g", "2g"),
        "bánh ướt" to NutritionInfo("200g", "280-350", "12g", "45g", "8g", "2g"),
        "bánh hỏi" to NutritionInfo("150g", "220-280", "8g", "40g", "5g", "2g"),
        "bò bía" to NutritionInfo("100g", "120-150", "6g", "20g", "3g", "2g"),
        "cơm cháy" to NutritionInfo("100g", "180-230", "4g", "35g", "5g", "1g"),

        // === MÓN QUỐC TẾ ===

        // MÓN Á
        "sushi" to NutritionInfo("100g", "200-250", "7g", "38g", "3g", "1g"),
        "sashimi" to NutritionInfo("150g", "180-220", "25g", "2g", "8g", "0g"),
        "ramen" to NutritionInfo("400g", "450-520", "25g", "55g", "15g", "4g"),
        "udon" to NutritionInfo("350g", "380-450", "12g", "65g", "8g", "3g"),
        "kimchi" to NutritionInfo("100g", "25-30", "2g", "5g", "0.5g", "2g"),
        "bibimbap" to NutritionInfo("400g", "420-500", "20g", "60g", "15g", "6g"),
        "pad thai" to NutritionInfo("350g", "400-480", "18g", "55g", "12g", "4g"),
        "tom yum" to NutritionInfo("300g", "180-230", "15g", "12g", "10g", "3g"),

        // MÓN ÂU
        "pizza" to NutritionInfo("150g", "266-300", "12g", "35g", "12g", "3g"),
        "pasta" to NutritionInfo("200g", "260-300", "10g", "50g", "2g", "4g"),
        "spaghetti" to NutritionInfo("200g", "280-320", "12g", "45g", "8g", "3g"),
        "lasagna" to NutritionInfo("250g", "350-400", "25g", "30g", "18g", "4g"),
        "hamburger" to NutritionInfo("250g", "295-350", "20g", "30g", "15g", "3g"),
        "sandwich" to NutritionInfo("200g", "320-380", "18g", "35g", "12g", "3g"),
        "hotdog" to NutritionInfo("150g", "280-330", "12g", "20g", "18g", "1g"),

        // ĐỒ CHIÊN
        "gà rán" to NutritionInfo("200g", "450-500", "30g", "20g", "30g", "2g"),
        "khoai tây chiên" to NutritionInfo("150g", "312-350", "4g", "40g", "18g", "4g"),
        "tempura" to NutritionInfo("150g", "280-330", "10g", "25g", "15g", "2g"),
        "tonkatsu" to NutritionInfo("200g", "420-480", "35g", "15g", "25g", "2g"),

        // === NGUYÊN LIỆU CHÍNH ===

        // THỊT ĐỎ
        "thịt bò" to NutritionInfo("100g", "250", "26g", "0g", "15g", "0g"),
        "thịt bò xào" to NutritionInfo("150g", "280-330", "35g", "8g", "12g", "2g"),
        "thịt heo" to NutritionInfo("100g", "242", "25g", "0g", "14g", "0g"),
        "thịt heo quay" to NutritionInfo("100g", "320-380", "25g", "0g", "25g", "0g"),
        "thịt gà" to NutritionInfo("100g", "239", "27g", "0g", "14g", "0g"),
        "thịt vịt" to NutritionInfo("100g", "337", "19g", "0g", "28g", "0g"),

        // HẢI SẢN
        "cá" to NutritionInfo("100g", "206", "22g", "0g", "12g", "0g"),
        "cá chiên" to NutritionInfo("150g", "250-300", "30g", "5g", "15g", "1g"),
        "cá kho" to NutritionInfo("150g", "200-250", "25g", "8g", "10g", "2g"),
        "tôm" to NutritionInfo("100g", "100", "24g", "1g", "1g", "0g"),
        "tôm hùm" to NutritionInfo("200g", "180-220", "40g", "2g", "3g", "0g"),
        "cua" to NutritionInfo("200g", "160-200", "35g", "2g", "4g", "0g"),
        "mực" to NutritionInfo("150g", "120-150", "25g", "5g", "3g", "0g"),
        "ốc" to NutritionInfo("100g", "130-160", "20g", "8g", "4g", "0g"),
        "nghêu" to NutritionInfo("100g", "80-100", "15g", "5g", "2g", "0g"),

        // TRỨNG
        "trứng luộc" to NutritionInfo("50g", "78", "6g", "1g", "5g", "0g"),
        "trứng chiên" to NutritionInfo("80g", "120-150", "8g", "2g", "10g", "0g"),
        "trứng ốp la" to NutritionInfo("50g", "90-110", "6g", "1g", "7g", "0g"),

        // ĐẬU & NẤM
        "đậu phụ" to NutritionInfo("100g", "76", "8g", "2g", "4g", "1g"),
        "đậu phụ chiên" to NutritionInfo("100g", "120-150", "12g", "5g", "8g", "2g"),
        "đậu hũ" to NutritionInfo("100g", "70-80", "7g", "3g", "4g", "1g"),
        "nấm" to NutritionInfo("100g", "22", "3g", "3g", "0.3g", "1g"),
        "nấm xào" to NutritionInfo("150g", "80-100", "5g", "8g", "4g", "3g"),

        // === RAU CỦ ===
        "rau muống" to NutritionInfo("100g", "23", "3g", "4g", "0.4g", "2g"),
        "rau muống xào" to NutritionInfo("150g", "80-100", "4g", "8g", "5g", "3g"),
        "rau cải" to NutritionInfo("100g", "25", "3g", "5g", "0.3g", "2g"),
        "rau lang" to NutritionInfo("100g", "22", "2g", "4g", "0.3g", "2g"),
        "cà rốt" to NutritionInfo("100g", "41", "1g", "10g", "0.2g", "3g"),
        "bông cải" to NutritionInfo("100g", "34", "3g", "7g", "0.4g", "3g"),
        "bắp cải" to NutritionInfo("100g", "25", "1g", "6g", "0.1g", "3g"),
        "su hào" to NutritionInfo("100g", "27", "2g", "6g", "0.1g", "2g"),
        "củ cải" to NutritionInfo("100g", "18", "1g", "4g", "0.1g", "2g"),

        // === TRÁI CÂY ===
        "chuối" to NutritionInfo("100g", "89", "1g", "23g", "0.3g", "3g"),
        "táo" to NutritionInfo("100g", "52", "0.3g", "14g", "0.2g", "2g"),
        "cam" to NutritionInfo("100g", "47", "1g", "12g", "0.1g", "2g"),
        "xoài" to NutritionInfo("100g", "60", "1g", "15g", "0.4g", "2g"),
        "dưa hấu" to NutritionInfo("100g", "30", "1g", "8g", "0.2g", "0.4g"),
        "nho" to NutritionInfo("100g", "69", "1g", "18g", "0.2g", "1g"),
        "dứa" to NutritionInfo("100g", "50", "1g", "13g", "0.1g", "1g"),
        "ổi" to NutritionInfo("100g", "68", "3g", "14g", "1g", "5g"),
        "mít" to NutritionInfo("100g", "95", "2g", "23g", "0.6g", "1g"),
        "sầu riêng" to NutritionInfo("100g", "147", "2g", "27g", "5g", "4g"),

        // === KHOAI & CỦ ===
        "khoai lang" to NutritionInfo("100g", "86", "2g", "20g", "0.1g", "3g"),
        "khoai tây" to NutritionInfo("100g", "77", "2g", "17g", "0.1g", "2g"),
        "khoai môn" to NutritionInfo("100g", "112", "2g", "26g", "0.2g", "4g"),
        "khoai từ" to NutritionInfo("100g", "118", "2g", "28g", "0.2g", "5g"),

        // === ĐỒ UỐNG ===
        "trà sữa" to NutritionInfo("400ml", "250-350", "6g", "45g", "8g", "0g"),
        "cà phê đen" to NutritionInfo("200ml", "5-10", "0.3g", "1g", "0.1g", "0g"),
        "cà phê sữa" to NutritionInfo("200ml", "80-120", "3g", "12g", "4g", "0g"),
        "sinh tố" to NutritionInfo("350ml", "180-250", "3g", "35g", "5g", "4g"),
        "nước ép" to NutritionInfo("300ml", "100-150", "1g", "25g", "0.2g", "1g"),
        "nước mía" to NutritionInfo("300ml", "180-220", "0g", "45g", "0g", "0g"),
        "nước dừa" to NutritionInfo("300ml", "45-60", "2g", "9g", "0g", "3g")
    ).mapKeys { it.key.lowercase() }

    /**
     * Trả về thông tin dinh dưỡng chi tiết cho label
     */
    fun getNutritionInfo(label: String): NutritionInfo? {
        val cleanLabel = label.lowercase().trim()

        // Tìm kiếm chính xác trước
        nutritionMap[cleanLabel]?.let { return it }

        // Tìm kiếm gần đúng
        return nutritionMap.entries
            .firstOrNull { entry ->
                cleanLabel.contains(entry.key) || entry.key.contains(cleanLabel)
            }?.value
    }

    /**
     * Trả về calories đơn giản
     */
    fun getCaloriesForLabel(label: String): Int? {
        return getNutritionInfo(label)?.calories?.split("-")?.first()?.toIntOrNull()
    }

    /**
     * Tìm món ăn tương tự dựa trên từ khóa
     */
    fun findSimilarFood(keyword: String): String? {
        val lowerKeyword = keyword.lowercase().trim()

        // Mapping thông minh từ các từ khóa phổ biến
        val smartMapping = mapOf(
            // Từ ML Kit thường trả về
            "noodle" to "mì",
            "soup" to "phở",
            "rice" to "cơm",
            "bread" to "bánh mì",
            "porridge" to "cháo",
            "dessert" to "chè",
            "meat" to "thịt",
            "chicken" to "thịt gà",
            "beef" to "thịt bò",
            "pork" to "thịt heo",
            "fish" to "cá",
            "seafood" to "hải sản",
            "vegetable" to "rau",
            "fruit" to "trái cây",
            "fried" to "chiên",
            "grilled" to "nướng",
            "steamed" to "hấp",
            "boiled" to "luộc"
        )

        // Kiểm tra mapping trước
        smartMapping.forEach { (english, vietnamese) ->
            if (lowerKeyword.contains(english)) {
                return vietnamese
            }
        }

        // Tìm trong database
        return getAllFoodNames().firstOrNull { food ->
            food.contains(lowerKeyword) || lowerKeyword.contains(food)
        }
    }

    /**
     * Tìm món ăn theo category
     */
    fun getFoodsByCategory(category: String): List<String> {
        val categoryMapping = mapOf(
            "phở" to listOf("phở bò", "phở gà"),
            "bún" to listOf("bún chả", "bún bò huế", "bún riêu", "bún đậu mắm tôm", "bún mắm"),
            "mì" to listOf("mì quảng", "mì xào", "mì tôm"),
            "cơm" to listOf("cơm tấm", "cơm gà", "cơm rang", "cơm niêu"),
            "bánh mì" to listOf("bánh mì thịt", "bánh mì chảo"),
            "bánh" to listOf("bánh cuốn", "bánh xèo", "bánh khọt", "bánh bèo", "bánh nậm", "bánh bột lọc"),
            "cháo" to listOf("cháo lòng", "cháo sườn", "cháo gà"),
            "chè" to listOf("chè đậu đen", "chè ba màu", "chè khúc bạch", "chè thái", "chè chuối"),
            "thịt" to listOf("thịt bò", "thịt heo", "thịt gà", "thịt vịt"),
            "hải sản" to listOf("cá", "tôm", "cua", "mực", "ốc", "nghêu"),
            "rau" to listOf("rau muống", "rau cải", "rau lang", "bông cải", "bắp cải"),
            "trái cây" to listOf("chuối", "táo", "cam", "xoài", "dưa hấu", "nho")
        )

        return categoryMapping[category] ?: emptyList()
    }

    /**
     * Trả về tất cả các món ăn có trong database
     */
    fun getAllFoodNames(): List<String> {
        return nutritionMap.keys.toList()
    }

    /**
     * Trả về số lượng món ăn trong database
     */
    fun getFoodCount(): Int {
        return nutritionMap.size
    }

    /**
     * Tìm kiếm món ăn theo từ khóa
     */
    fun searchFoods(query: String): List<String> {
        val lowerQuery = query.lowercase().trim()
        return getAllFoodNames().filter { it.contains(lowerQuery) }
    }

    /**
     * Lấy danh sách món ăn phổ biến
     */
    fun getPopularFoods(): List<String> {
        return listOf(
            "phở bò", "bún chả", "cơm tấm", "bánh mì thịt",
            "bánh xèo", "gỏi cuốn", "nem rán", "chè đậu đen"
        )
    }

    /**
     * Lấy danh sách món ăn theo loại
     */
    fun getFoodsByType(type: String): List<String> {
        val typeMapping = mapOf(
            "món chính" to listOf("phở bò", "bún chả", "cơm tấm", "mì quảng", "bánh xèo"),
            "món phụ" to listOf("nem rán", "gỏi cuốn", "chả giò", "bánh khọt"),
            "đồ ngọt" to listOf("chè đậu đen", "chè ba màu", "chè thái", "rau câu"),
            "đồ uống" to listOf("trà sữa", "cà phê sữa", "sinh tố", "nước ép"),
            "ăn vặt" to listOf("bánh tráng trộn", "bánh tráng nướng", "bò bía", "cơm cháy")
        )
        return typeMapping[type] ?: emptyList()
    }
}