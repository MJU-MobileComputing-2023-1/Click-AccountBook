package com.example.click_accountbook

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

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
data class ItemInfo(
    val id: String = UUID.randomUUID().toString(), // Generate a new item ID
    val receiptId : String,
    val itemName: String,
    val itemCode: String,
    val itemCount: Float?,
    val itemPrice: Float?,
    val itemUnitPrice: Float?
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
                val priceObject = totalPriceObject.getJSONObject("price")
                if (priceObject.has("formatted")) {
                    totalPrice = priceObject.getJSONObject("formatted").getDouble("value").toFloat()
                }
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

    fun parseItems(ocrResult: String): List<ItemInfo> {
        val itemsList = mutableListOf<ItemInfo>()

        val jsonObject = JSONObject(ocrResult)
        val imagesArray = jsonObject.getJSONArray("images")
        val receiptObject =
            imagesArray.getJSONObject(0).getJSONObject("receipt").getJSONObject("result")

        if (receiptObject.has("subResults")) {
            val subResultsArray = receiptObject.getJSONArray("subResults")
            for (i in 0 until subResultsArray.length()) {
                val subResultObject = subResultsArray.getJSONObject(i)
                if (subResultObject.has("items")) {
                    val itemsArray = subResultObject.getJSONArray("items")
                    for (j in 0 until itemsArray.length()) {
                        val itemObject = itemsArray.getJSONObject(j)
                        val itemName = if (itemObject.has("name")) {
                            itemObject.getJSONObject("name").getString("text")
                        } else {
                            "Unknown name"
                        }
                        val itemCode = if (itemObject.has("code")) {
                            itemObject.getJSONObject("code").getString("text")
                        } else {
                            "Unknown code"
                        }
                        val itemCount = if (itemObject.has("count")) {
                            itemObject.getJSONObject("count").getDouble("text").toFloat()
                        } else {
                            0.0F
                        }
                        val itemPrice = if (itemObject.has("price")) {
                            val priceInfoObject = itemObject.getJSONObject("price")
                            if (priceInfoObject.has("price")) {
                                val priceObject = priceInfoObject.getJSONObject("price")
                                if (priceObject.has("formatted")) {
                                    priceObject.getJSONObject("formatted").getDouble("value").toFloat()
                                } else {
                                    0.0F
                                }
                            } else {
                                0.0F
                            }
                        } else {
                            0.0F
                        }
                        val itemUnitPrice = if (itemObject.has("price")) {
                            val priceInfoObject = itemObject.getJSONObject("price")
                            if (priceInfoObject.has("unitPrice")) {
                                val priceObject = priceInfoObject.getJSONObject("unitPrice")
                                if (priceObject.has("formatted")) {
                                    priceObject.getJSONObject("formatted").getDouble("value").toFloat()
                                } else {
                                    0.0F
                                }
                            } else {
                                0.0F
                            }
                        } else {
                            0.0F
                        }

                        val itemInfo = ItemInfo(
                            id = UUID.randomUUID().toString(), // Generate a new item ID here
                            receiptId = "",
                            itemName = itemName,
                            itemCode = itemCode,
                            itemCount = itemCount,
                            itemPrice = itemPrice,
                            itemUnitPrice = itemUnitPrice
                        )
                        itemsList.add(itemInfo)
                    }
                }
            }
        }

        return itemsList
    }

}