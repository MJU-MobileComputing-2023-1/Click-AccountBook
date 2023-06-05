package com.example.click_accountbook

import org.json.JSONArray
import org.json.JSONObject

data class ReceiptInfo(
    val storeName: String,
    val storeSubName: String,
    val bizNum: String?,
    val address: String?,
    val tel: String?,
    val date: String,
    val time: String,
    val cardCompany: String?,
    val cardNumber: String?,
    val confirmNum: String?,
    val totalPrice: Float,
)



class ReceiptOCR {
    fun parseReceipt(ocrResult: String): ReceiptInfo {
        // Set default values
        var storeName = "Unknown Store Name"
        var storeSubName = "Unknown Sub Name"
        var bizNum = "Unknown Business Number"
        var address = "Unknown Address"
        var tel = "Unknown Telephone Number"
        var date = "Unknown Date"
        var time = "Unknown Time"
        var cardCompany = "Unknown Card Company"
        var cardNumber = "Unknown Card Number"
        var confirmNum = "Unknown Confirmation Number"
        var totalPrice = 0.0F


        // Parse the OCR result and extract the required fields
        val jsonObject = JSONObject(ocrResult)
        val imagesArray = jsonObject.getJSONArray("images")
        val receiptObject =
            imagesArray.getJSONObject(0).getJSONObject("receipt").getJSONObject("result")

        if (receiptObject.has("storeInfo")) {
            val storeInfoObject = receiptObject.getJSONObject("storeInfo")
            if (storeInfoObject.has("name"))
                storeName = storeInfoObject.getJSONObject("name").getString("text")
            if (storeInfoObject.has("bizNum"))
                bizNum = storeInfoObject.getJSONObject("bizNum").getString("text")
            if (storeInfoObject.has("addresses"))
                address =
                    storeInfoObject.getJSONArray("addresses").getJSONObject(0).getString("text")
            if (storeInfoObject.has("tel"))
                tel = storeInfoObject.getJSONArray("tel").getJSONObject(0).getString("text")
        }

        if (receiptObject.has("paymentInfo")) {
            val paymentInfoObject = receiptObject.getJSONObject("paymentInfo")
            if (paymentInfoObject.has("date"))
                date = paymentInfoObject.getJSONObject("date").getString("text")
            if (paymentInfoObject.has("time"))
                time = paymentInfoObject.getJSONObject("time").getString("text")
            if (paymentInfoObject.has("cardInfo")) {
                val cardInfoObject = paymentInfoObject.getJSONObject("cardInfo")
                if (cardInfoObject.has("company"))
                    cardCompany = cardInfoObject.getJSONObject("company").getString("text")
                if (cardInfoObject.has("number"))
                    cardNumber = cardInfoObject.getJSONObject("number").getString("text")
            }
            if (paymentInfoObject.has("confirmNum"))
                confirmNum = paymentInfoObject.getString("confirmNum")
        }
        if (receiptObject.has("totalPrice")) {
            val totalPriceObject = receiptObject.getJSONObject("totalPrice")
            if (totalPriceObject.has("price")) {
                val priceString = totalPriceObject.getJSONObject("price").getString("text")
                val priceWithoutComma = priceString.replace(",", "")
                totalPrice = priceWithoutComma.toDouble().toFloat()
            }
        }


        return ReceiptInfo(
            storeName = storeName,
            storeSubName = storeSubName,
            bizNum = bizNum,
            address = address,
            tel = tel,
            date = date,
            time = time,
            cardCompany = cardCompany,
            cardNumber = cardNumber,
            confirmNum = confirmNum,
            totalPrice = totalPrice
        )
    }

    fun parseItems(itemsJsonArray: JSONArray?): List<Item> {
        val items = mutableListOf<Item>()

        if (itemsJsonArray != null && itemsJsonArray.length() > 0) {
            for (i in 0 until itemsJsonArray.length()) {
                val itemObject = itemsJsonArray.getJSONObject(i)

                val name =
                    if (itemObject.has("name")) itemObject.getString("name") else "Unknown Name IN"
                val code =
                    if (itemObject.has("code")) itemObject.getString("code") else "Unknown Code IN"
                val count =
                    if (itemObject.has("count")) itemObject.getDouble("count").toFloat() else 0.0F
                var price = 0.0F
                var unitPrice = 0.0F
                if (itemObject.has("priceInfo")) {
                    val priceInfoObject = itemObject.getJSONObject("priceInfo")
                    price = if (priceInfoObject.has("price")) priceInfoObject.getDouble("price")
                        .toFloat() else 0.0F
                    unitPrice =
                        if (priceInfoObject.has("unitPrice")) priceInfoObject.getDouble("unitPrice")
                            .toFloat() else 0.0F
                }

                val item = Item(
                    id = "", // Generate a new item ID
                    receiptId = "", // Use the receipt ID
                    itemName = name,
                    itemCode = code,
                    itemCount = count,
                    itemPrice = price,
                    itemUnitPrice = unitPrice
                )

                items.add(item)
            }
        } else {
            // Add a default item when itemsJsonArray is null or empty
            val defaultItem = Item(
                id = "", // Generate a new item ID
                receiptId = "", // Use the receipt ID
                itemName = "Unknown Name",
                itemCode = "Unknown Code",
                itemCount = 0.0F,
                itemPrice = 0.0F,
                itemUnitPrice = 0.0F
            )
            items.add(defaultItem)
        }

        return items
    }
}