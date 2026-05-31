package com.example.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    val mimeType: String,
    val data: String // Base64 encoded string
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?
)

@JsonClass(generateAdapter = true)
data class PlantDetectionResult(
    val name: String,
    val nameUrdu: String = "",
    val scientificName: String,
    val family: String,
    val familyUrdu: String = "",
    val description: String,
    val descriptionUrdu: String = "",
    val careInstructions: String,
    val careInstructionsUrdu: String = "",
    val waterRequirements: String,
    val waterRequirementsUrdu: String = "",
    val sunlightRequirements: String,
    val sunlightRequirementsUrdu: String = "",
    val confidenceScore: Double
)

@JsonClass(generateAdapter = true)
data class GeminiErrorResponse(
    val error: GeminiErrorDetails?
)

@JsonClass(generateAdapter = true)
data class GeminiErrorDetails(
    val code: Int?,
    val message: String?,
    val status: String?
)
