package com.ghardari.app.data.repository

import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ghardari.app.data.model.AppLanguage
import com.ghardari.app.data.model.RationItem

object PrintReportHelper {

    fun generateShopkeeperText(items: List<RationItem>, monthKey: String, lang: AppLanguage): String {
        val sb = StringBuilder()
        sb.append("🛒 *گهرداري - ماهوار راشن ۽ سودا سلف لسٽ*\n")
        sb.append("📅 مهينو: *$monthKey*\n")
        sb.append("==============================\n")
        sb.append("نمبر | شيءِ ۽ برانڊ | مقدار | قيمت\n")
        sb.append("------------------------------\n")

        items.forEachIndexed { index, item ->
            val brandStr = if (item.company.isNotBlank()) " (${item.company})" else ""
            val qtyStr = if (item.quantity % 1.0 == 0.0) "${item.quantity.toInt()} ${item.unit}" else "${item.quantity} ${item.unit}"
            val priceStr = if (item.actualPrice > 0) "Rs. ${item.actualPrice.toInt()}" else "[       ]"
            sb.append("${index + 1}. ${item.name}$brandStr — *$qtyStr* — $priceStr\n")
        }

        sb.append("==============================\n")
        val totalActual = items.filter { it.isPurchased && it.actualPrice > 0 }.sumOf { it.actualPrice }
        if (totalActual > 0) {
            sb.append("ڪل رقم: *Rs. ${totalActual.toInt()}*\n")
        } else {
            sb.append("دڪاندار جي صحيح / ڪل رقم: ________________\n")
        }
        return sb.toString()
    }

    fun printShopkeeperDocument(context: Context, items: List<RationItem>, monthKey: String, lang: AppLanguage) {
        val htmlContent = buildHtmlDocument(items, monthKey)
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                val printAdapter = webView.createPrintDocumentAdapter("Ghardari_Ration_$monthKey")
                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("id", "print", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager?.print("Ghardari_Ration_$monthKey", printAdapter, printAttributes)
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    private fun buildHtmlDocument(items: List<RationItem>, monthKey: String): String {
        val rows = StringBuilder()
        items.forEachIndexed { index, item ->
            val brandStr = if (item.company.isNotBlank()) "<br><small style='color:#666;'>برانڊ: ${item.company}</small>" else ""
            val qtyStr = if (item.quantity % 1.0 == 0.0) "${item.quantity.toInt()} ${item.unit}" else "${item.quantity} ${item.unit}"
            val priceStr = if (item.actualPrice > 0) "Rs. ${item.actualPrice.toInt()}" else "&nbsp;"
            rows.append(
                """
                <tr>
                    <td style="text-align:center; padding:8px; border:1px solid #ccc;">${index + 1}</td>
                    <td style="padding:8px; border:1px solid #ccc; font-weight:bold;">${item.name} $brandStr</td>
                    <td style="text-align:center; padding:8px; border:1px solid #ccc;">$qtyStr</td>
                    <td style="text-align:center; padding:8px; border:1px solid #ccc; min-width:80px;">$priceStr</td>
                </tr>
                """.trimIndent()
            )
        }

        return """
        <!DOCTYPE html>
        <html dir="rtl" lang="sd">
        <head>
            <meta charset="utf-8">
            <title>گهرداري راشن لسٽ</title>
            <style>
                body { font-family: sans-serif; padding: 20px; direction: rtl; }
                h2, h4 { margin: 4px 0; text-align: center; }
                table { width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 14px; }
                th { background-color: #0F766E; color: white; padding: 8px; border: 1px solid #0F766E; }
                .footer { margin-top: 30px; display: flex; justify-content: space-between; font-size: 14px; }
            </style>
        </head>
        <body>
            <h2>گهرداري - ماهوار راشن ۽ سودا سلف جي لسٽ</h2>
            <h4>مهينو: $monthKey</h4>
            <table>
                <thead>
                    <tr>
                        <th style="width:40px;">نمبر</th>
                        <th>شيءِ جو نالو ۽ ڪمپني</th>
                        <th style="width:90px;">مقدار</th>
                        <th style="width:110px;">دڪاندار جي قيمت</th>
                    </tr>
                </thead>
                <tbody>
                    $rows
                </tbody>
            </table>
            <div style="margin-top:25px; padding:10px; border-top:1px dashed #666;">
                <p><strong>ڪل رقم (Total Bill):</strong> ________________________</p>
                <p><strong>دڪان / دڪاندار جو نالو:</strong> ________________________</p>
            </div>
        </body>
        </html>
        """.trimIndent()
    }
}
