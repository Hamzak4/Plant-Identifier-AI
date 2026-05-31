package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.PlantDao
import com.example.data.local.PlantEntity
import com.example.data.local.EssayEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.DiseaseDetectionEntity
import com.example.data.local.ReminderEntity
import com.example.data.remote.Content
import com.example.data.remote.GenerateContentRequest
import com.example.data.remote.GenerationConfig
import com.example.data.remote.GeminiApiService
import com.example.data.remote.InlineData
import com.example.data.remote.Part
import com.example.data.remote.PlantDetectionResult
import com.example.data.remote.DiseaseDetectionResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlantRepositoryImpl(
    private val plantDao: PlantDao,
    private val apiService: GeminiApiService
) : PlantRepository {

    // --- Plants ---
    override fun getAllPlants(): Flow<List<PlantEntity>> {
        return plantDao.getAllPlants()
    }

    override suspend fun insertPlant(plant: PlantEntity): Long = withContext(Dispatchers.IO) {
        plantDao.insertPlant(plant)
    }

    override suspend fun deletePlant(plant: PlantEntity) = withContext(Dispatchers.IO) {
        plantDao.deletePlant(plant)
    }

    override suspend fun getPlantById(id: Int): PlantEntity? = withContext(Dispatchers.IO) {
        plantDao.getPlantById(id)
    }

    private suspend fun <T> executeWithRetry(
        actionName: String,
        modelsToTry: List<String> = listOf("gemini-1.5-flash", "gemini-2.0-flash", "gemini-2.5-flash"),
        block: suspend (model: String, apiKey: String) -> T
    ): T {
        val workingKey = ""
        val keysToTry = mutableListOf<String>()
        
        // Put configKey FIRST so we prioritize the user's custom API key configured in UI panel
        val configKey = BuildConfig.GEMINI_API_KEY
        if (!configKey.isNullOrBlank() && configKey != "MY_GEMINI_API_KEY" && configKey != "GEMINI_API_KEY" && configKey != "null") {
            keysToTry.add(configKey)
        }
        
        // Put workingKey as the second/fallback option
        if (!keysToTry.contains(workingKey)) {
            keysToTry.add(workingKey)
        }

        var lastException: Exception? = null

        for (apiKey in keysToTry) {
            for (model in modelsToTry) {
                try {
                    android.util.Log.i("PlantRepository", "Attempting $actionName using key: ${apiKey.take(6)}... model: $model")
                    return block(model, apiKey)
                } catch (e: retrofit2.HttpException) {
                    val errorBodyString = e.response()?.errorBody()?.string()
                    val parsedMessage = parseApiErrorMessage(errorBodyString)
                    val finalReason = if (!parsedMessage.isNullOrBlank()) {
                        "$parsedMessage (HTTP ${e.code()})"
                    } else {
                        "HTTP ${e.code()}: ${e.message()}"
                    }
                    val wrappingException = Exception("Key ${apiKey.take(6)}... Model $model fail: $finalReason", e)
                    lastException = wrappingException
                    android.util.Log.w("PlantRepository", "Action $actionName / Model $model failed with HTTP exception: $finalReason")
                } catch (e: Exception) {
                    lastException = e
                    android.util.Log.w("PlantRepository", "Action $actionName / Model $model failed with exception: ${e.message}. Retrying fallback...")
                }
            }
        }

        val errMsg = lastException?.message ?: "Unknown API Error"
        throw IllegalStateException(
            "Gemini AI Service ($actionName) failed.\n\n" +
            "Details: $errMsg\n\n" +
            "Please verify that your Gemini API key is correct and set in the Secrets Panel in AI Studio."
        )
    }

    override suspend fun identifyPlant(base64Image: String): PlantDetectionResult = withContext(Dispatchers.IO) {
        val systemInstruction = Content(
            parts = listOf(
                Part(text = """
                    You are an expert botanist and plant identification assistant. Analyze the image and identify the plant.
                    You MUST respond with a JSON object ONLY matching this schema:
                    {
                      "name": "Lavender",
                      "nameUrdu": "لیوینڈر",
                      "scientificName": "Lavandula angustifolia",
                      "family": "Lamiaceae",
                      "familyUrdu": "پودینہ خاندان (شفتالیہ)",
                      "description": "Lavender is a beautiful, fragrant flowering plant in the mint family...",
                      "descriptionUrdu": "لیوینڈر پودینہ کے خاندان سے تعلق رکھنے والا ایک خوبصورت اور خوشبودار پھولدار پودا ہے۔",
                      "careInstructions": "Prune annually, ensure dry soil between watering intervals, grow in warm environments...",
                      "careInstructionsUrdu": "سالانہ کانٹ چھانٹ کریں، پانی دینے کے درمیانی وقفے میں مٹی کے خشک ہونے کی تصدیق کریں، اور گرم ماحول میں اگائیں۔",
                      "waterRequirements": "Water deeply but infrequently, allowing soil to dry completely.",
                      "waterRequirementsUrdu": "مٹی کو مکمل طور پر خشک ہونے دینے کے بعد گہرا لیکن کبھی کبھار پانی دیں۔",
                      "sunlightRequirements": "Full, direct sun for at least 6 hours daily.",
                      "sunlightRequirementsUrdu": "باقاعدگی سے روزانہ کم از کم 6 گھنٹے براہ راست سورج کی روشنی دیں۔",
                      "confidenceScore": 95.0
                    }
                    Fill in these accurate details for the plant you identified in the picture. The confidenceScore must be a double between 0.0 and 100.0 indicating your certainty. Ensure all Urdu keys are translated into natural, readable Urdu text. Return ONLY this raw JSON object. Do not wrap it in any comments, markers, markdown tags, or explanatory text.
                """.trimIndent())
            )
        )

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = "Identify the plant in the provided image and return its full properties as a JSON object."),
                        Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image))
                    )
                )
            ),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.2f
            ),
            systemInstruction = systemInstruction
        )

        val responseText = executeWithRetry("identifyPlant") { model, apiKey ->
            val response = apiService.generateContent(model, apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response from AI identification service")
        }

        val cleanJsonText = responseText.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(PlantDetectionResult::class.java)

        adapter.fromJson(cleanJsonText)
            ?: throw IllegalStateException("Failed to parse JSON response output from Gemini: $responseText")
    }

    // --- Essays ---
    override fun getAllEssays(): Flow<List<EssayEntity>> {
        return plantDao.getAllEssays()
    }

    override suspend fun insertEssay(essay: EssayEntity): Long = withContext(Dispatchers.IO) {
        plantDao.insertEssay(essay)
    }

    override suspend fun deleteEssay(essay: EssayEntity) = withContext(Dispatchers.IO) {
        plantDao.deleteEssay(essay)
    }

    override suspend fun generateEssay(topic: String, language: String, wordCount: Int, educationLevel: String): String = withContext(Dispatchers.IO) {
        val prompt = "Write a highly comprehensive, structured, $wordCount-word essay about \"$topic\" in $language language tailored for $educationLevel level. Do not keep any intro placeholders, write full narrative paragraphs."
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        executeWithRetry("generateEssay") { model, apiKey ->
            val response = apiService.generateContent(model, apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty text response from AI essay service")
        }
    }

    // --- Chat Room ---
    override fun getAllMessages(): Flow<List<ChatMessageEntity>> {
        return plantDao.getAllMessages()
    }

    override suspend fun insertMessage(msg: ChatMessageEntity): Long = withContext(Dispatchers.IO) {
        plantDao.insertMessage(msg)
    }

    override suspend fun clearChat() = withContext(Dispatchers.IO) {
        plantDao.clearChat()
    }

    override suspend fun generateChatResponse(message: String, isExpertMode: Boolean, history: List<ChatMessageEntity>): String = withContext(Dispatchers.IO) {
        val systemPrompt = if (isExpertMode) {
            "You are a professional botanist. Answer plant-related questions accurately with scientific terminology and actionable plant protection remedies. Answer clearly, adapting to the user's preferred language (e.g., Urdu, English, Hindi)."
        } else {
            "You are a helpful and positive AI chatbot inside a Plant Identifier application."
        }

        val promptBuilder = StringBuilder()
        history.takeLast(10).forEach {
            if (!it.text.isNullOrBlank()) {
                val prefix = if (it.sender == "user") "User: " else "AI: "
                promptBuilder.append(prefix).append(it.text).append("\n")
            }
        }
        promptBuilder.append("User: ").append(message)

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = promptBuilder.toString())))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.5f)
        )

        executeWithRetry("generateChatResponse") { model, apiKey ->
            val response = apiService.generateContent(model, apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response from AI chat service")
        }
    }

    // --- Disease Detections ---
    override fun getAllDiseases(): Flow<List<DiseaseDetectionEntity>> {
        return plantDao.getAllDiseases()
    }

    override suspend fun insertDisease(disease: DiseaseDetectionEntity): Long = withContext(Dispatchers.IO) {
        plantDao.insertDisease(disease)
    }

    override suspend fun deleteDisease(disease: DiseaseDetectionEntity) = withContext(Dispatchers.IO) {
        plantDao.deleteDisease(disease)
    }

    override suspend fun detectDisease(base64Image: String, plantPart: String): DiseaseDetectionResult = withContext(Dispatchers.IO) {
        val systemInstruction = Content(
            parts = listOf(
                Part(text = """
                    You are an expert plant pathologist and clinical botanist. Verify the image showing a plant's field symptom ($plantPart) and analyze any disease.
                    You MUST respond with a JSON object ONLY matching this schema:
                    {
                      "diseaseName": "Powdery Mildew",
                      "severity": "Medium",
                      "symptoms": "White powdery spots or webby growth scattered on leaves and stems...",
                      "treatment": "Apply sulfur fungicide, avoid overhead sprinklers, and prune heavily infected stems.",
                      "confidenceScore": 96.0
                    }
                    Fill in these clinical details accurately. Return ONLY this raw JSON object. Do not wrap it in any comments, markdown tags, or explanatory text.
                """.trimIndent())
            )
        )

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = "Identify any plant diseases shown on this $plantPart and return a JSON object details."),
                        Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image))
                    )
                )
            ),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.2f
            ),
            systemInstruction = systemInstruction
        )

        val text = executeWithRetry("detectDisease") { model, apiKey ->
            val response = apiService.generateContent(model, apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response from AI disease analysis service")
        }

        val cleanJsonText = text.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(DiseaseDetectionResult::class.java)

        adapter.fromJson(cleanJsonText)
            ?: throw IllegalStateException("Failed to parse AI response: $text")
    }

    // --- Reminders ---
    override fun getAllReminders(): Flow<List<ReminderEntity>> {
        return plantDao.getAllReminders()
    }

    override suspend fun insertReminder(reminder: ReminderEntity): Long = withContext(Dispatchers.IO) {
        plantDao.insertReminder(reminder)
    }

    override suspend fun deleteReminder(reminder: ReminderEntity) = withContext(Dispatchers.IO) {
        plantDao.deleteReminder(reminder)
    }

    private fun parseApiErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(com.example.data.remote.GeminiErrorResponse::class.java)
            val response = adapter.fromJson(errorBody)
            response?.error?.message
        } catch (e: Exception) {
            try {
                val messageRegex = """"message"\s*:\s*"([^"]+)"""".toRegex()
                messageRegex.find(errorBody)?.groupValues?.getOrNull(1)
            } catch (ex: Exception) {
                null
            }
        }
    }
}
