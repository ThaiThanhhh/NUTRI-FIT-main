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
import java.util.Locale

data class FoodLabelResult(
    val label: String,
    val confidence: Float,
    val isGeneric: Boolean = false,
    val isEnhanced: Boolean = false,
    val category: String = "unknown",
    val matchedFood: String? = null
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

    private val vietnameseFoodKeywords = listOf(
        "phở", "bún", "mì", "cơm", "xôi", "bánh", "cháo", "chè", "miến",
        "bánh mì", "bánh cuốn", "bánh xèo", "bánh khọt", "bánh bèo", "bánh nậm",
        "bánh bột lọc", "bánh chưng", "bánh tét", "bánh giò", "bánh ướt",
        "nem", "chả", "gỏi", "nộm", "lẩu", "canh", "súp", "riêu", "kho", "rang",
        "bún chả", "bún bò", "bún riêu", "bún mắm", "bún đậu", "bún thang",
        "mì quảng", "mì vịt tiềm", "mì hoành thánh", "miến gà", "miến lươn",
        "cơm tấm", "cơm gà", "cơm niêu", "cơm rang", "cơm hến",
        "cháo lòng", "cháo sườn", "cháo gà", "cháo trai", "cháo ếch",
        "nướng", "kho", "xào", "luộc", "chiên", "hấp", "rang", "om", "quay", "rim",
        "gà", "bò", "heo", "cá", "tôm", "cua", "mực", "ốc", "ếch", "vịt", "lươn",
        "trứng", "đậu", "nấm", "rau", "củ", "quả", "thịt", "hải sản",
        "rau muống", "rau cải", "rau lang", "bông cải", "bắp cải", "cà rốt",
        "cao lầu", "bánh canh", "bánh tráng", "bánh hỏi", "bò bía", "cơm cháy"
    )

    private val smartEnglishMapping = mapOf(
        "pho" to "phở bò", "noodle soup" to "phở bò", "beef noodle" to "phở bò",
        "chicken noodle" to "phở gà", "rice noodle" to "bún", "vermicelli" to "bún",
        "noodle" to "mì tôm", "ramen" to "ramen", "udon" to "udon", "pasta" to "pasta",
        "spaghetti" to "spaghetti", "rice" to "cơm tấm", "fried rice" to "cơm rang",
        "sticky rice" to "xôi mặn", "bread" to "bánh mì thịt", "sandwich" to "sandwich",
        "baguette" to "bánh mì thịt", "porridge" to "cháo gà", "congee" to "cháo sườn",
        "sweet soup" to "chè ba màu", "dessert" to "rau câu", "spring roll" to "chả giò",
        "salad" to "gỏi bò", "hot pot" to "lẩu thái", "soup" to "súp cua",
        "grilled" to "nướng", "fried" to "chiên", "stir fried" to "xào",
        "chicken" to "thịt gà", "beef" to "thịt bò", "pork" to "thịt heo", "fish" to "cá",
        "seafood" to "hải sản", "shrimp" to "tôm", "crab" to "cua", "squid" to "mực",
        "egg" to "trứng luộc", "vegetable" to "rau cải", "fruit" to "trái cây",
        "banana" to "chuối", "apple" to "táo", "orange" to "cam",
        "mango" to "xoài", "watermelon" to "dưa hấu", "grape" to "nho",
        "pineapple" to "dứa", "guava" to "ổi", "jackfruit" to "mít", "durian" to "sầu riêng"
    )

    private val combinedMapping = mapOf(
        "fried rice" to "cơm rang",
        "spring roll" to "chả giò",
        "sweet soup" to "chè ba màu",
        "noodle soup" to "phở bò",
        "sticky rice" to "xôi mặn",
        "fried chicken" to "gà rán",
        "beef noodle" to "phở bò",
        "chicken noodle" to "phở gà",
        "french fries" to "khoai tây chiên",
        "potato chips" to "khoai tây chiên"
    )

    private val foodCategories = mapOf(
        "phở" to listOf("phở", "noodle soup", "beef noodle", "chicken noodle"),
        "bún" to listOf("bún", "vermicelli", "rice noodle"),
        "mì" to listOf("mì", "noodle", "ramen", "udon", "pasta", "spaghetti"),
        "cơm" to listOf("cơm", "rice", "fried rice"),
        "bánh mì" to listOf("bánh mì", "bread", "sandwich", "baguette"),
        "cháo" to listOf("cháo", "porridge", "congee"),
        "chè" to listOf("chè", "sweet soup"),
        "thịt" to listOf("thịt", "meat", "chicken", "beef", "pork"),
        "hải sản" to listOf("hải sản", "seafood", "fish", "shrimp", "crab", "squid"),
        "rau" to listOf("rau", "vegetable", "green", "cabbage", "broccoli", "carrot"),
        "trái cây" to listOf("trái cây", "fruit", "apple", "orange", "banana", "mango", "watermelon")
    )

    private val genericLabels = listOf(
        "plate", "bowl", "tableware", "container", "package", "packaging", "dishware",
        "delicious", "tasty", "yummy", "grocery", "restaurant", "indoor", "fast food"
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

    private fun advancedLabelProcessing(mlLabels: List<com.google.mlkit.vision.label.ImageLabel>): List<FoodLabelResult> {
        return mlLabels.map { mlLabel ->
            val originalLabel = mlLabel.text ?: ""
            val confidence = mlLabel.confidence

            val (cleanedLabel, isGeneric, category) = classifyAndCleanLabel(originalLabel)
            val (enhancedLabel, matchedFood) = smartLabelMappingWithMatch(cleanedLabel)

            FoodLabelResult(
                label = if (enhancedLabel.isEmpty()) originalLabel else enhancedLabel,
                confidence = confidence,
                isGeneric = isGeneric,
                isEnhanced = enhancedLabel != cleanedLabel,
                category = category,
                matchedFood = matchedFood
            )
        }
            .filter { it.confidence > 0.20 && !it.isGeneric && it.label.isNotEmpty() }
            .sortedByDescending { it.confidence }
            .take(10)
    }

    private fun classifyAndCleanLabel(label: String): Triple<String, Boolean, String> {
        val lowerLabel = label.lowercase().trim()
        val isGeneric = isGenericLabel(lowerLabel)
        val category = detectFoodCategory(lowerLabel)
        return Triple(lowerLabel, isGeneric, category)
    }

    private fun smartLabelMappingWithMatch(label: String): Pair<String, String?> {
        if (label.isEmpty()) return Pair("", null)

        combinedMapping.forEach { (english, vietnamese) ->
            if (label == english || label.contains(english)) {
                return Pair(vietnamese, findExactFoodMatch(vietnamese))
            }
        }

        smartEnglishMapping.forEach { (english, vietnamese) ->
            if (label == english || label.contains(english)) {
                return Pair(vietnamese, findExactFoodMatch(vietnamese))
            }
        }

        vietnameseFoodKeywords.forEach { keyword ->
            if (label.contains(keyword)) {
                val matched = findExactFoodMatch(keyword)
                if (matched != null) return Pair(keyword, matched)
            }
        }

        val similarDb = findSimilarFoodInDatabase(label)
        return Pair(label, similarDb)
    }

    private fun findExactFoodMatch(keyword: String): String? {
        return NutritionRepository.getAllFoodNames().firstOrNull { food ->
            food == keyword || food.lowercase() == keyword.lowercase()
        }
    }

    private fun findSimilarFoodInDatabase(label: String): String? {
        val labelWords = label.split(" ", "-", "_").map { it.trim().lowercase() }
        return NutritionRepository.getAllFoodNames().firstOrNull { food ->
            val foodWords = food.split(" ", "-", "_").map { it.trim().lowercase() }
            calculateSimilarityScore(labelWords, foodWords) > 0.6
        }
    }

    private fun calculateSimilarityScore(words1: List<String>, words2: List<String>): Double {
        val commonWords = words1.intersect(words2).size
        val totalWords = maxOf(words1.size, words2.size)
        return if (totalWords > 0) commonWords.toDouble() / totalWords else 0.0
    }

    private fun detectFoodCategory(label: String): String {
        foodCategories.forEach { (category, keywords) ->
            if (keywords.any { keyword -> label.contains(keyword) }) {
                return category
            }
        }
        return "unknown"
    }

    private fun findOptimalFoodMatch(labels: List<FoodLabelResult>): Pair<String?, Float?> {
        if (labels.isEmpty()) return Pair(null, null)

        // 1. Tìm nhãn khớp từ điển tiếng Việt trước
        val directMatches = labels.filter { it.matchedFood != null }
        if (directMatches.isNotEmpty()) {
            val bestMatch = directMatches.maxByOrNull { it.confidence }
            return Pair(bestMatch?.matchedFood, bestMatch?.confidence)
        }

        // 2. Kiểm tra khoảng cách Levenshtein
        val candidateLabels = labels.filter { !it.isGeneric && it.confidence > 0.25 }
        candidateLabels.forEach { labelResult ->
            NutritionRepository.getAllFoodNames().forEach { dbFoodName ->
                if (hasSimilarKeywords(labelResult.label, dbFoodName)) {
                    return Pair(dbFoodName, labelResult.confidence)
                }
            }
        }

        // 3. Gom cụm thông minh về nhãn bao quát (Gốc gác quả táo, da người khi cầm hoa quả, v.v...)
        labels.forEach { labelResult ->
            val lowerLabel = labelResult.label.lowercase()
            if (lowerLabel.contains("fruit") || lowerLabel.contains("apple") || lowerLabel.contains("produce") || lowerLabel.contains("plant")) {
                return Pair("Trái cây", labelResult.confidence)
            }
            if (lowerLabel.contains("food") || lowerLabel.contains("dish") || lowerLabel.contains("skin") || lowerLabel.contains("meal")) {
                return Pair("Món ăn chung", labelResult.confidence)
            }
        }

        // 4. Trả về nhãn thô hoa mỹ đầu tiên nếu lọt lưới hoàn toàn (Toy, v.v...)
        val topLabel = labels.firstOrNull { !it.isGeneric }
        if (topLabel != null) {
            val formattedLabel = topLabel.label.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            return Pair(formattedLabel, topLabel.confidence)
        }

        return Pair(null, null)
    }

    private fun hasSimilarKeywords(label: String, food: String): Boolean {
        val labelWords = label.split(" ", "-", ",").map { it.trim().lowercase() }
        val foodWords = food.split(" ", "-", ",").map { it.trim().lowercase() }
        return labelWords.any { lWord ->
            foodWords.any { fWord ->
                lWord == fWord || calculateWordSimilarity(lWord, fWord) > 0.80
            }
        }
    }

    private fun calculateWordSimilarity(word1: String, word2: String): Double {
        if (word1 == word2) return 1.0
        val maxLength = maxOf(word1.length, word2.length)
        if (maxLength == 0) return 0.0
        val editDistance = calculateLevenshteinDistance(word1, word2)
        return 1.0 - (editDistance.toDouble() / maxLength)
    }

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

    private fun isGenericLabel(label: String): Boolean {
        return genericLabels.any { generic ->
            label == generic || calculateWordSimilarity(label, generic) > 0.85
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