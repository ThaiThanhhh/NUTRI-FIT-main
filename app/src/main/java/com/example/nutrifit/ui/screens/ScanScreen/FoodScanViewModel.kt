package com.example.nutrifit.ui.screens.ScanScreen

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FoodLabelResult(
    val label: String,
    val confidence: Float,
    val isGeneric: Boolean = false,
    val isEnhanced: Boolean = false,
    val category: String = "unknown",
    val matchedFood: String? = null // Thêm field để lưu món ăn khớp
)

class FoodScanViewModel : ViewModel() {

    private val _capturedImage = MutableStateFlow<Bitmap?>(null)
    val capturedImage: StateFlow<Bitmap?> = _capturedImage

    private val _labels = MutableStateFlow<List<FoodLabelResult>>(emptyList())
    val labels: StateFlow<List<FoodLabelResult>> = _labels

    private val _estimatedCalories = MutableStateFlow<Int?>(null)
    val estimatedCalories: StateFlow<Int?> = _estimatedCalories

    private val _nutritionInfo = MutableStateFlow<NutritionInfo?>(null)
    val nutritionInfo: StateFlow<NutritionInfo?> = _nutritionInfo

    private val _detectedFoodName = MutableStateFlow<String?>(null)
    val detectedFoodName: StateFlow<String?> = _detectedFoodName

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing

    private val _detectionConfidence = MutableStateFlow<Float?>(null)
    val detectionConfidence: StateFlow<Float?> = _detectionConfidence

    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    // 🚀 CẢI THIỆN: Mở rộng từ khóa ẩm thực Việt Nam
    private val vietnameseFoodKeywords = listOf(
        // Món chính phổ biến
        "phở", "bún", "mì", "cơm", "xôi", "bánh", "cháo", "chè", "miến",
        "bánh mì", "bánh cuốn", "bánh xèo", "bánh khọt", "bánh bèo", "bánh nậm",
        "bánh bột lọc", "bánh chưng", "bánh tét", "bánh giò", "bánh ướt",

        // Món ăn đặc trưng
        "nem", "chả", "gỏi", "nộm", "lẩu", "canh", "súp", "riêu", "kho", "rang",
        "bún chả", "bún bò", "bún riêu", "bún mắm", "bún đậu", "bún thang",
        "mì quảng", "mì vịt tiềm", "mì hoành thánh", "miến gà", "miến lươn",
        "cơm tấm", "cơm gà", "cơm niêu", "cơm rang", "cơm hến",
        "cháo lòng", "cháo sườn", "cháo gà", "cháo trai", "cháo ếch",

        // Phương pháp chế biến
        "nướng", "kho", "xào", "luộc", "chiên", "hấp", "rang", "om", "quay", "rim",
        "tái", "chín", "sốt", "ướp", "trộn", "cuốn", "gói",

        // Nguyên liệu chính
        "gà", "bò", "heo", "cá", "tôm", "cua", "mực", "ốc", "ếch", "vịt", "lươn",
        "trứng", "đậu", "nấm", "rau", "củ", "quả", "thịt", "hải sản", "tảo",
        "thịt gà", "thịt bò", "thịt heo", "thịt vịt", "thịt cừu", "thịt dê",

        // Rau củ quả
        "rau muống", "rau cải", "rau lang", "bông cải", "bắp cải", "cà rốt",
        "su hào", "củ cải", "khoai", "ngô", "đậu bắp", "mướp", "bí", "bầu",

        // Đặc sản vùng miền
        "cao lầu", "mì căn", "bánh canh", "bánh đa", "bánh tráng", "bánh hỏi",
        "bò bía", "cơm cháy", "bánh căn", "bánh tráng nướng", "bánh tráng trộn"
    )

    // 🚀 CẢI THIỆN: Mở rộng ánh xạ thông minh
    private val smartEnglishMapping = mapOf(
        // Món ăn cụ thể
        "pho" to "phở", "noodle soup" to "phở", "beef noodle" to "phở bò",
        "chicken noodle" to "phở gà", "rice noodle" to "bún", "vermicelli" to "bún",
        "noodle" to "mì", "ramen" to "mì", "udon" to "mì", "pasta" to "mì ý",
        "spaghetti" to "mì ý", "rice" to "cơm", "fried rice" to "cơm rang",
        "sticky rice" to "xôi", "bread" to "bánh mì", "sandwich" to "bánh mì",
        "baguette" to "bánh mì", "porridge" to "cháo", "congee" to "cháo",
        "sweet soup" to "chè", "dessert" to "chè", "spring roll" to "chả giò",
        "salad" to "gỏi", "hot pot" to "lẩu", "soup" to "canh", "stew" to "hầm",

        // Phương pháp chế biến
        "grilled" to "nướng", "barbecue" to "nướng", "bbq" to "nướng",
        "fried" to "chiên", "deep fried" to "chiên giòn", "stir fried" to "xào",
        "steamed" to "hấp", "boiled" to "luộc", "braised" to "kho",
        "roasted" to "quay", "simmered" to "om", "marinated" to "ướp",

        // Protein
        "chicken" to "gà", "beef" to "bò", "pork" to "heo", "fish" to "cá",
        "seafood" to "hải sản", "shrimp" to "tôm", "prawn" to "tôm",
        "crab" to "cua", "squid" to "mực", "octopus" to "bạch tuộc",
        "snail" to "ốc", "frog" to "ếch", "egg" to "trứng", "duck" to "vịt",
        "lobster" to "tôm hùm", "clam" to "nghêu", "mussel" to "trai",

        // Rau củ
        "vegetable" to "rau", "green" to "rau xanh", "fruit" to "trái cây",
        "carrot" to "cà rốt", "cabbage" to "bắp cải", "broccoli" to "bông cải",
        "tomato" to "cà chua", "potato" to "khoai tây", "sweet potato" to "khoai lang",
        "mushroom" to "nấm", "tofu" to "đậu phụ", "bean" to "đậu",
        "corn" to "ngô", "pumpkin" to "bí đỏ", "cucumber" to "dưa leo",

        // Trái cây
        "banana" to "chuối", "apple" to "táo", "orange" to "cam",
        "mango" to "xoài", "watermelon" to "dưa hấu", "grape" to "nho",
        "pineapple" to "dứa", "guava" to "ổi", "jackfruit" to "mít",
        "durian" to "sầu riêng", "lychee" to "vải", "rambutan" to "chôm chôm"
    )

    // 🚀 CẢI THIỆN: Thêm mapping kết hợp
    private val combinedMapping = mapOf(
        "fried rice" to "cơm rang",
        "spring roll" to "chả giò",
        "sweet soup" to "chè",
        "noodle soup" to "phở",
        "sticky rice" to "xôi",
        "grilled meat" to "thịt nướng",
        "fried chicken" to "gà chiên",
        "beef noodle" to "phở bò",
        "chicken noodle" to "phở gà",
        "hot pot" to "lẩu",
        "rice vermicelli" to "bún",
        "fish sauce" to "nước mắm",
        "soy sauce" to "nước tương",
        "shrimp paste" to "mắm tôm",
        "vermicelli noodle" to "bún",
        "rice paper" to "bánh tráng",
        "rice cake" to "bánh chưng"
    )

    // 🚀 CẢI THIỆN: Nhóm món ăn theo category chi tiết
    private val foodCategories = mapOf(
        "phở" to listOf("phở", "noodle soup", "beef noodle", "chicken noodle"),
        "bún" to listOf("bún", "vermicelli", "rice noodle", "bun"),
        "mì" to listOf("mì", "noodle", "ramen", "udon", "pasta", "mi"),
        "cơm" to listOf("cơm", "rice", "fried rice", "com"),
        "bánh mì" to listOf("bánh mì", "bread", "sandwich", "baguette", "banh mi"),
        "cháo" to listOf("cháo", "porridge", "congee", "chao"),
        "chè" to listOf("chè", "sweet soup", "dessert", "che"),
        "bánh" to listOf("bánh", "cake", "pie", "pastry", "banh"),
        "thịt" to listOf("thịt", "meat", "chicken", "beef", "pork", "duck", "thit"),
        "hải sản" to listOf("hải sản", "seafood", "fish", "shrimp", "crab", "squid"),
        "rau" to listOf("rau", "vegetable", "green", "cabbage", "broccoli"),
        "trái cây" to listOf("trái cây", "fruit", "apple", "orange", "banana")
    )

    // 🚀 CẢI THIỆN: Danh sách từ khóa generic cần lọc
    private val genericLabels = listOf(
        "food", "dish", "meal", "cuisine", "ingredient", "produce",
        "vegetable", "fruit", "meat", "seafood", "breakfast", "lunch",
        "dinner", "snack", "beverage", "drink", "plate", "bowl", "tableware",
        "fast food", "asian food", "vietnamese food", "chinese food",
        "japanese food", "container", "package", "packaging", "dishware",
        "delicious", "tasty", "yummy", "fresh", "cooked", "raw", "ingredients",
        "produce", "grocery", "market", "restaurant", "dining", "eating"
    )

    fun updateImage(bitmap: Bitmap) {
        _capturedImage.value = bitmap
        detectLabels(bitmap)
    }

    private fun detectLabels(bitmap: Bitmap) {
        _isAnalyzing.value = true
        _detectionConfidence.value = null
        viewModelScope.launch {
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                labeler.process(image)
                    .addOnSuccessListener { mlLabels ->
                        val processedLabels = advancedLabelProcessing(mlLabels)
                        _labels.value = processedLabels

                        val (bestMatch, confidence) = findOptimalFoodMatch(processedLabels)
                        _detectedFoodName.value = bestMatch
                        _detectionConfidence.value = confidence

                        // Cập nhật nutrition info
                        val nutrition = bestMatch?.let { NutritionRepository.getNutritionInfo(it) }
                        _nutritionInfo.value = nutrition

                        val calories = bestMatch?.let { NutritionRepository.getCaloriesForLabel(it) }
                        _estimatedCalories.value = calories

                        _isAnalyzing.value = false
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                        resetDetectionState()
                        _isAnalyzing.value = false
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                resetDetectionState()
                _isAnalyzing.value = false
            }
        }
    }

    // 🚀 CẢI THIỆN: Xử lý nhãn nâng cao
    private fun advancedLabelProcessing(mlLabels: List<com.google.mlkit.vision.label.ImageLabel>): List<FoodLabelResult> {
        return mlLabels.map { mlLabel ->
            val originalLabel = mlLabel.text ?: ""
            val confidence = mlLabel.confidence

            // Phân loại và làm sạch nhãn
            val (cleanedLabel, isGeneric, category) = classifyAndCleanLabel(originalLabel)

            // Ánh xạ thông minh và tìm món ăn khớp
            val (enhancedLabel, matchedFood) = smartLabelMappingWithMatch(cleanedLabel)
            val isEnhanced = enhancedLabel != cleanedLabel

            FoodLabelResult(
                label = enhancedLabel,
                confidence = confidence,
                isGeneric = isGeneric,
                isEnhanced = isEnhanced,
                category = category,
                matchedFood = matchedFood
            )
        }
            .filter {
                // 🚀 Lọc tốt hơn: loại bỏ generic và confidence thấp
                it.confidence > 0.2 && !it.isGeneric
            }
            .sortedByDescending { it.confidence }
            .take(10) // Giới hạn số lượng kết quả
    }

    // 🚀 CẢI THIỆN: Phân loại và làm sạch nhãn
    private fun classifyAndCleanLabel(label: String): Triple<String, Boolean, String> {
        val lowerLabel = label.lowercase().trim()

        // Loại bỏ các từ không cần thiết
        val cleaned = lowerLabel
            .replace(Regex("(fresh|cooked|raw|delicious|tasty|yummy|dish|food|cuisine|ingredient)"), "")
            .replace(Regex("\\s+"), " ")
            .trim()

        // Kiểm tra generic label
        val isGeneric = isGenericLabel(cleaned)

        // Phân loại category
        val category = detectFoodCategory(cleaned)

        return Triple(cleaned, isGeneric, category)
    }

    // 🚀 CẢI THIỆN: Ánh xạ thông minh với tìm kiếm món ăn
    private fun smartLabelMappingWithMatch(label: String): Pair<String, String?> {
        var enhancedLabel = label
        var matchedFood: String? = null

        // Bước 1: Kiểm tra mapping kết hợp (ưu tiên cao)
        combinedMapping.forEach { (english, vietnamese) ->
            if (label.contains(english)) {
                enhancedLabel = vietnamese
                matchedFood = findExactFoodMatch(vietnamese)
                return@forEach
            }
        }

        // Bước 2: Kiểm tra từ khóa tiếng Việt trực tiếp
        if (matchedFood == null) {
            vietnameseFoodKeywords.forEach { keyword ->
                if (label.contains(keyword)) {
                    enhancedLabel = keyword
                    matchedFood = findExactFoodMatch(keyword)
                    return@forEach
                }
            }
        }

        // Bước 3: Ánh xạ từ tiếng Anh
        if (matchedFood == null) {
            smartEnglishMapping.forEach { (english, vietnamese) ->
                if (label.contains(english)) {
                    enhancedLabel = vietnamese
                    matchedFood = findExactFoodMatch(vietnamese)
                    return@forEach
                }
            }
        }

        // Bước 4: Tìm kiếm gần đúng trong database
        if (matchedFood == null) {
            matchedFood = findSimilarFoodInDatabase(label)
        }

        return Pair(enhancedLabel, matchedFood)
    }

    // 🚀 CẢI THIỆN: Tìm món ăn chính xác trong database
    private fun findExactFoodMatch(keyword: String): String? {
        return NutritionRepository.getAllFoodNames().firstOrNull { food ->
            food.contains(keyword) || keyword.contains(food)
        }
    }

    // 🚀 CẢI THIỆN: Tìm món ăn tương tự trong database
    private fun findSimilarFoodInDatabase(label: String): String? {
        val labelWords = label.split(" ", "-", "_").map { it.trim().lowercase() }

        return NutritionRepository.getAllFoodNames().firstOrNull { food ->
            val foodWords = food.split(" ", "-", "_").map { it.trim().lowercase() }

            // Kiểm tra độ tương đồng
            val similarityScore = calculateSimilarityScore(labelWords, foodWords)
            similarityScore > 0.6 // Ngưỡng tương đồng
        }
    }

    // 🚀 CẢI THIỆN: Tính điểm tương đồng giữa các từ
    private fun calculateSimilarityScore(words1: List<String>, words2: List<String>): Double {
        val commonWords = words1.intersect(words2).size
        val totalWords = maxOf(words1.size, words2.size)
        return if (totalWords > 0) commonWords.toDouble() / totalWords else 0.0
    }

    // 🚀 CẢI THIỆN: Phát hiện category
    private fun detectFoodCategory(label: String): String {
        foodCategories.forEach { (category, keywords) ->
            if (keywords.any { keyword -> label.contains(keyword) }) {
                return category
            }
        }
        return "unknown"
    }

    // 🚀 CẢI THIỆN: Tìm món ăn tối ưu với confidence
    private fun findOptimalFoodMatch(labels: List<FoodLabelResult>): Pair<String?, Float?> {
        if (labels.isEmpty()) return Pair(null, null)

        // Ưu tiên 1: Nhãn đã có matched food với confidence cao
        val exactMatches = labels.filter {
            it.matchedFood != null && it.confidence > 0.6
        }
        if (exactMatches.isNotEmpty()) {
            return Pair(exactMatches.first().matchedFood, exactMatches.first().confidence)
        }

        // Ưu tiên 2: Tìm theo category với confidence trung bình
        val categoryMatches = labels.filter {
            it.confidence > 0.4 && !it.isGeneric
        }
        categoryMatches.forEach { label ->
            val foodsInCategory = NutritionRepository.getFoodsByCategory(label.category)
            if (foodsInCategory.isNotEmpty()) {
                return Pair(foodsInCategory.first(), label.confidence)
            }
        }

        // Ưu tiên 3: Tìm kiếm mở rộng
        val allPossibleMatches = labels.filter { !it.isGeneric && it.confidence > 0.3 }
        allPossibleMatches.forEach { label ->
            NutritionRepository.getAllFoodNames().forEach { food ->
                if (hasSimilarKeywords(label.label, food)) {
                    return Pair(food, label.confidence)
                }
            }
        }

        // Ưu tiên 4: Trả về nhãn tốt nhất
        val bestLabel = labels.firstOrNull { !it.isGeneric }
        return Pair(bestLabel?.label, bestLabel?.confidence)
    }

    // 🚀 CẢI THIỆN: Kiểm tra từ khóa tương tự
    private fun hasSimilarKeywords(label: String, food: String): Boolean {
        val labelWords = label.split(" ", "-", ",").map { it.trim().lowercase() }
        val foodWords = food.split(" ", "-", ",").map { it.trim().lowercase() }

        return labelWords.any { labelWord ->
            foodWords.any { foodWord ->
                labelWord.contains(foodWord) || foodWord.contains(labelWord) ||
                        calculateWordSimilarity(labelWord, foodWord) > 0.7
            }
        }
    }

    // 🚀 CẢI THIỆN: Tính độ tương đồng giữa 2 từ
    private fun calculateWordSimilarity(word1: String, word2: String): Double {
        if (word1 == word2) return 1.0
        val maxLength = maxOf(word1.length, word2.length)
        val editDistance = calculateLevenshteinDistance(word1, word2)
        return 1.0 - (editDistance.toDouble() / maxLength)
    }

    // 🚀 CẢI THIỆN: Tính khoảng cách Levenshtein
    private fun calculateLevenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                dp[i][j] = minOf(
                    dp[i-1][j] + 1,
                    dp[i][j-1] + 1,
                    dp[i-1][j-1] + if (s1[i-1] == s2[j-1]) 0 else 1
                )
            }
        }

        return dp[s1.length][s2.length]
    }

    // 🚀 CẢI THIỆN: Kiểm tra generic label
    private fun isGenericLabel(label: String): Boolean {
        return genericLabels.any { generic ->
            label.contains(generic) || calculateWordSimilarity(label, generic) > 0.8
        }
    }

    private fun resetDetectionState() {
        _labels.value = emptyList()
        _estimatedCalories.value = null
        _nutritionInfo.value = null
        _detectedFoodName.value = null
        _detectionConfidence.value = null
    }

    fun clearDetection() {
        resetDetectionState()
        _capturedImage.value = null
    }
}