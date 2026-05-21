package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.FoodItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    // High performance model request structures
    data class GeminiTextPart(val text: String)
    data class GeminiContent(val parts: List<GeminiTextPart>)
    data class GeminiRequest(val contents: List<GeminiContent>)

    suspend fun getRecipeRecommendations(activeItems: List<FoodItem>): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is missing or is placeholder")
            return@withContext getMockRecipesResponse(activeItems)
        }

        val itemsString = activeItems.joinToString("\n") { item ->
            "- ${item.name} (${item.quantity}), Category: ${item.category}, Expiry: ${getDaysLeftText(item.expiryDateMillis)}"
        }

        val prompt = """
            You are Chef Nova, an intelligent kitchen assistant and champion of a zero-waste, eco-friendly household!
            Here is a list of food items I currently have in my kitchen:
            $itemsString
            
            Based on these ingredients, please recommend 2-3 delicious, creative recipes that will help me consume these items quickly, especially prioritizing the ones that are closest to expiration to reduce food waste.
            
            Please tailor your recipes so that they use as many of these listed items as possible.
            
            Format your response in beautiful, well-spaced, clean text layout with appropriate headers:
            
            🍳 Chef Nova's Smart Zero-Waste Recipes
            
            ## [Recipe 1 Name]
            🌿 **Expiring Ingredients Used:** [list the ingredients on my list that are consumed in this recipe]
            🛒 **Additional Ingredients Needed:** [list any common pantry items needed]
            📖 **Step-by-Step Instructions:**
            1. [Step 1]
            2. [Step 2]
            ...
            
            ## [Recipe 2 Name]
            [Do the same for Recipe 2...]
            
            💡 **Zero-Waste Cooking Tip:** [Include a highly helpful, kitchen hack relevant to preserving these categories of ingredients]
            
            Keep the tone warm, friendly, eco-conscious, and encouraging!
        """.trimIndent()

        val jsonAdapter = moshi.adapter(GeminiRequest::class.java)
        val requestBodyData = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiTextPart(text = prompt))))
        )
        val requestJson = jsonAdapter.toJson(requestBodyData)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "Response unsuccessful (Code ${response.code}): $errBody")
                    throw IOException("Network error: ${response.code}")
                }
                
                val responseBodyStr = response.body?.string() ?: ""
                parseGeminiResponse(responseBodyStr)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching from Gemini: ${e.message}", e)
            getMockRecipesResponse(activeItems)
        }
    }

    private fun parseGeminiResponse(jsonString: String): String {
        return try {
            // Traverse JSON response using Moshi generic Map or regex for stability and light memory footprint
            val mapAdapter = moshi.adapter(Map::class.java)
            val jsonMap = mapAdapter.fromJson(jsonString)
            val candidates = jsonMap?.get("candidates") as? List<*>
            val firstCandidate = candidates?.firstOrNull() as? Map<*, *>
            val content = firstCandidate?.get("content") as? Map<*, *>
            val parts = content?.get("parts") as? List<*>
            val firstPart = parts?.firstOrNull() as? Map<*, *>
            val text = firstPart?.get("text") as? String
            text ?: "Unable to parse recipe suggestions."
        } catch (e: Exception) {
            Log.e(TAG, "Failed parsing Gemini JSON structures: ${e.message}")
            "Error rendering Chef Nova's recipes. Please try again."
        }
    }

    private fun getDaysLeftText(expiryDateMillis: Long): String {
        val diff = expiryDateMillis - System.currentTimeMillis()
        val days = (diff / (24 * 60 * 60 * 1000L)).toInt()
        return when {
            days < 0 -> "Expired"
            days == 0 -> "Expires today"
            days == 1 -> "1 day left"
            else -> "$days days left"
        }
    }

    private fun getMockRecipesResponse(items: List<FoodItem>): String {
        // Fallback or preview representation when API key is not entered yet
        val expiringItems = items.filter { (it.expiryDateMillis - System.currentTimeMillis()) < 3 * 24 * 60 * 60 * 1000L }
        val namesText = expiringItems.joinToString(", ") { it.name }.ifEmpty { "Milk, Bread" }
        return """
            🍳 Chef Nova's Smart Zero-Waste Recipes
            
            ## Recipe 1: Creamy Smart Bread Pudding
            🌿 **Expiring Ingredients Used:** Bread, Milk, Eggs
            🛒 **Additional Ingredients Needed:** Sugar, Cinnamon, Butter
            📖 **Step-by-Step Instructions:**
            1. Tear the expiring **Bread** into small, bite-sized pieces and spread them evenly in a greased baking dish.
            2. In a mixing bowl, scale and whisk the **Eggs** together with **Milk**, a pinch of sugar, and a dash of cinnamon.
            3. Pour the liquid egg-milk mixture over the bread cubes, pressing gently so the bread is fully submerged. Let sit for 10 minutes to absorb.
            4. Bake at 180°C (350°F) for 35 to 40 minutes, until the center is set and top is a toasted golden brown. Serve warm!
            
            ## Recipe 2: Quick Savory Cheese Omelet with Tomatoes
            🌿 **Expiring Ingredients Used:** Eggs, Cheese, Tomato
            🛒 **Additional Ingredients Needed:** Butter, Salt, Pepper, a fresh herb (optional)
            📖 **Step-by-Step Instructions:**
            1. Beat 2-3 **Eggs** in a bowl with a pinch of salt and pepper.
            2. Chop the **Tomato** into small cubes. Grate or crumble the expiring **Cheese**.
            3. Melt a teaspoon of butter in a non-stick skillet over medium heat. Pour in the beaten eggs.
            4. As the eggs set, sprinkle the diced tomatoes and cheese over one half. Fold the omelet in half, letting it cook for 1 additional minute until cheese is melted and bubbling.
            
            💡 **Zero-Waste Cooking Tip:** 
            Did you know you can freeze grated cheese if you don't plan to use it before expiration? Place it in an airtight freezer bag and add a teaspoon of cornstarch to keep the shreds from sticking!
        """.trimIndent()
    }
}
