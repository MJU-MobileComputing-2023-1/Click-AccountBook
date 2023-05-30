    package com.example.click_accountbook

    import android.graphics.Bitmap
    import android.util.Base64
    import android.util.Log
    import com.google.gson.Gson
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.withContext
    import okhttp3.MediaType.Companion.toMediaTypeOrNull
    import okhttp3.MultipartBody
    import okhttp3.OkHttpClient
    import okhttp3.Request
    import okhttp3.RequestBody
    import java.io.ByteArrayOutputStream
    import java.io.File
    import java.net.URL
    import java.time.Instant
    import java.util.*

    data class OCRRequest(
        val version: String,
        val requestId: String,
        val timestamp: Long,
        val images: List<ImageInfo>
    ) {
        data class ImageInfo(
            val format: String,
            val name: String,
            val data: String
        )
    }

    class NaverClovaOCR {
        private val client = OkHttpClient()
        private val gson = Gson()

        suspend fun performOCR(imageFile: File): String {
            return withContext(Dispatchers.IO) {
                val requestId = UUID.randomUUID().toString()
                val currentTimeMillis = System.currentTimeMillis()

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", imageFile.name, RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile))
                    .addFormDataPart(
                        "message", gson.toJson(
                            OCRRequest(
                                version = "V2",
                                requestId = requestId,
                                timestamp = currentTimeMillis,
                                images = listOf(
                                    OCRRequest.ImageInfo(
                                        format = "jpg",
                                        name = "demo",
                                        data = ""
                                    )
                                )
                            )
                        )
                    )
                    .build()

                val apiUrl = "https://lfa43mmyqz.apigw.ntruss.com/custom/v1/22851/4694ad8d9621f27cc6e5473bace22ee6651b762a3e52f687ea80b1a9cf590e5f/document/receipt"
                val secretKey = "Z2ZvYk16QUJkZ1pnb2tHYndueXRJbXF3d2xvYW5qTFQ="

                val request = Request.Builder()
                    .url(URL(apiUrl))
                    .addHeader("X-OCR-SECRET", secretKey)
                    .post(requestBody)
                    .build()

                try {
                    Log.d("NaverClovaOCR", "Launching Naver Clova OCR")
                    val response = client.newCall(request).execute()
                    Log.d("NaverClovaOCR", "Naver Clova OCR Executed")
                    val responseBody = response.body?.string() ?: throw RuntimeException("No response from OCR API")
                    Log.d("NaverClovaOCR", "OCR Response: $responseBody")
                    responseBody
                } catch (e: Exception) {
                    Log.e("NaverClovaOCR", "Error performing OCR: ${e.message}")
                    throw e
                }
            }
        }
    }