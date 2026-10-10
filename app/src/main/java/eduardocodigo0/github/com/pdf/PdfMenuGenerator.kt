package eduardocodigo0.github.com.pdf

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import eduardocodigo0.github.com.R
import eduardocodigo0.github.com.data.CategoryLayoutType
import eduardocodigo0.github.com.data.MenuData
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class PdfMenuGenerator(private val context: Context) {

    private val pageWidth = 595
    private val pageHeight = 842

    private val primaryColor = Color.parseColor("#800020")
    private val primaryLightColor = Color.parseColor("#FADBD8")
    private val darkTextColor = Color.parseColor("#0F172A")
    private val grayTextColor = Color.parseColor("#334155")
    private val yellowColor = Color.parseColor("#FAFA33")

    private val strokeWidth = 1f

    fun generatePdf(menuData: MenuData, outputFile: File): File {
        val pdfDocument = PdfDocument()

        val pageInfo1 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page1 = pdfDocument.startPage(pageInfo1)
        drawPage1(page1.canvas, menuData)
        pdfDocument.finishPage(page1)

        val pageInfo2 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
        val page2 = pdfDocument.startPage(pageInfo2)
        drawPage2(page2.canvas, menuData)
        pdfDocument.finishPage(page2)

        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        return outputFile
    }

    private fun drawOutlinedText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        fillPaint: Paint,
        strokeColor: Int = Color.WHITE,
        strokeWidthVal: Float = 1f
    ) {
        val strokePaint = Paint(fillPaint).apply {
            color = strokeColor
            style = Paint.Style.STROKE
            strokeWidth = strokeWidthVal
            strokeJoin = Paint.Join.ROUND
        }
        canvas.drawText(text, x, y, strokePaint)
        canvas.drawText(text, x, y, fillPaint)
    }

    private fun drawBackgroundAndHeader(
        canvas: Canvas,
        bgResId: Int,
        headerText: String,
        phone1: String,
        phone2: String
    ): Float {
        try {
            val bgBitmap = BitmapFactory.decodeResource(context.resources, bgResId)
            if (bgBitmap != null) {
                canvas.drawBitmap(
                    bgBitmap,
                    null,
                    RectF(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat()),
                    null
                )
            }
        } catch (_: Exception) {}

        val borderPaint = Paint().apply {
            color = primaryColor
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawRect(RectF(15f, 15f, pageWidth - 15f, pageHeight - 15f), borderPaint)
        canvas.drawRect(RectF(18f, 18f, pageWidth - 18f, pageHeight - 18f), borderPaint)

        val currentY = 28f

        try {
            val refriBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img_refri_header)
            if (refriBitmap != null) {
                val imgWidth = 58f
                val imgHeight = (imgWidth * refriBitmap.height) / refriBitmap.width
                val imgRect = RectF(35f, currentY + 3f, 35f + imgWidth, currentY + 3f + imgHeight)
                canvas.drawBitmap(refriBitmap, null, imgRect, null)
            }
        } catch (_: Exception) {}

        try {
            val lancheBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img_lanche_header)
            if (lancheBitmap != null) {
                val imgWidth = 58f
                val imgHeight = (imgWidth * lancheBitmap.height) / lancheBitmap.width
                val imgRect = RectF(pageWidth - 35f - imgWidth, currentY + 3f, pageWidth - 35f, currentY + 3f + imgHeight)
                canvas.drawBitmap(lancheBitmap, null, imgRect, null)
            }
        } catch (_: Exception) {}

        val headerBox = RectF(98f, currentY, pageWidth - 98f, currentY + 62f)
        val fillPaint = Paint().apply {
            color = primaryLightColor
            style = Paint.Style.FILL
            isAntiAlias = true
            alpha = 220
        }
        canvas.drawRoundRect(headerBox, 8f, 8f, fillPaint)
        canvas.drawRoundRect(headerBox, 8f, 8f, borderPaint)

        val textCenterX = pageWidth / 2f

        val titlePaint = Paint().apply {
            color = yellowColor
            textSize = 23f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        drawOutlinedText(canvas, headerText.uppercase(), textCenterX, currentY + 32f, titlePaint, Color.BLACK, strokeWidth)

        val phonePaint = Paint().apply {
            color = darkTextColor
            textSize = 12.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val phoneText = if (phone2.isNotBlank()) "VIVO $phone1  |  CLARO $phone2" else "VIVO $phone1"
        drawOutlinedText(canvas, phoneText, textCenterX, currentY + 51f, phonePaint, Color.WHITE, strokeWidth)

        return currentY + 76f
    }

    private fun drawFooterPaymentInfo(canvas: Canvas, paymentNote: String) {
        val footerY = pageHeight - 48f
        val footerX = 35f

        val footerPaint = Paint().apply {
            color = primaryColor
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val note = if (paymentNote.isNotBlank()) paymentNote else "Aceitamos cartões Visa e Mastercard"
        drawOutlinedText(canvas, "💳 $note", footerX, footerY + 12f, footerPaint, Color.WHITE, strokeWidth)

        try {
            val cardsBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_cards_payment)
            if (cardsBitmap != null) {
                val imgWidth = 60f
                val imgHeight = (imgWidth * cardsBitmap.height) / cardsBitmap.width
                val imgRect = RectF(pageWidth - 35f - imgWidth, footerY, pageWidth - 35f, footerY + imgHeight)
                canvas.drawBitmap(cardsBitmap, null, imgRect, null)
            } else {
                val visaBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_visa)
                val masterBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_mastercard)
                if (visaBitmap != null && masterBitmap != null) {
                    val visaRect = RectF(pageWidth - 85f, footerY, pageWidth - 55f, footerY + 18f)
                    val masterRect = RectF(pageWidth - 50f, footerY, pageWidth - 25f, footerY + 18f)
                    canvas.drawBitmap(visaBitmap, null, visaRect, null)
                    canvas.drawBitmap(masterBitmap, null, masterRect, null)
                }
            }
        } catch (_: Exception) {}
    }

    private fun drawPage1(canvas: Canvas, menuData: MenuData) {
        var currentY = drawBackgroundAndHeader(
            canvas,
            R.drawable.bg_page1,
            menuData.header.restaurantName,
            menuData.header.phone1,
            menuData.header.phone2
        )

        val categoriesOnPage1 = menuData.categories.filter {
            it.layoutType == CategoryLayoutType.DRINKS_MULTI_PRICE || it.layoutType == CategoryLayoutType.DRINKS_SINGLE_PRICE
        }.sortedBy { it.orderIndex }

        val leftX = 35f
        val rightX = pageWidth - 35f

        for (category in categoriesOnPage1) {
            val categoryItems = menuData.items.filter { it.categoryId == category.id }
            if (categoryItems.isEmpty()) continue

            val bannerHeight = 24f
            val bannerRect = RectF(leftX, currentY, rightX, currentY + bannerHeight)
            val bannerPaint = Paint().apply {
                color = primaryColor
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            canvas.drawRoundRect(bannerRect, 4f, 4f, bannerPaint)

            val catTitlePaint = Paint().apply {
                color = Color.WHITE
                textSize = 12.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            drawOutlinedText(canvas, category.name.uppercase(), leftX + 10f, currentY + 17f, catTitlePaint, Color.BLACK, strokeWidth)

            if (category.columns.isNotEmpty()) {
                val colCount = category.columns.size
                val colWidth = 62f
                val colPaint = Paint().apply {
                    color = Color.WHITE
                    textSize = 9.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                category.columns.forEachIndexed { idx, colName ->
                    val colX = rightX - (colCount - idx) * colWidth + (colWidth / 2f)
                    drawOutlinedText(canvas, colName, colX, currentY + 16f, colPaint, Color.BLACK, strokeWidth)
                }
            }

            currentY += bannerHeight + 7f

            val itemPaint = Paint().apply {
                color = darkTextColor
                textSize = 11.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val pricePaint = Paint().apply {
                color = darkTextColor
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            val singlePricePaint = Paint().apply {
                color = darkTextColor
                textSize = 11.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
            }

            val dotPaint = Paint().apply {
                color = Color.DKGRAY
                style = Paint.Style.STROKE
                strokeWidth = 1f
                pathEffect = DashPathEffect(floatArrayOf(2f, 4f), 0f)
                isAntiAlias = true
            }

            for (item in categoryItems) {
                val itemTextY = currentY + 12f
                drawOutlinedText(canvas, item.name, leftX + 5f, itemTextY, itemPaint, Color.WHITE, strokeWidth)

                if (category.layoutType == CategoryLayoutType.DRINKS_MULTI_PRICE) {
                    val colCount = category.columns.size
                    val colWidth = 62f
                    val nameWidth = itemPaint.measureText(item.name)
                    val dotsStartX = leftX + 15f + nameWidth
                    val dotsEndX = rightX - (colCount * colWidth) - 10f

                    if (dotsEndX > dotsStartX) {
                        canvas.drawLine(dotsStartX, itemTextY - 3f, dotsEndX, itemTextY - 3f, dotPaint)
                    }

                    category.columns.forEachIndexed { idx, colName ->
                        val priceVal = item.pricesByColumn[colName]
                        val priceText = if (priceVal != null && priceVal > 0) formatPrice(priceVal) else "-"
                        val colX = rightX - (colCount - idx) * colWidth + (colWidth / 2f)
                        drawOutlinedText(canvas, priceText, colX, itemTextY, pricePaint, Color.WHITE, strokeWidth)
                    }
                } else {
                    val priceText = formatPrice(item.singlePrice)
                    val nameWidth = itemPaint.measureText(item.name)
                    val priceWidth = singlePricePaint.measureText(priceText)
                    val dotsStartX = leftX + 15f + nameWidth
                    val dotsEndX = rightX - priceWidth - 10f

                    if (dotsEndX > dotsStartX) {
                        canvas.drawLine(dotsStartX, itemTextY - 3f, dotsEndX, itemTextY - 3f, dotPaint)
                    }

                    drawOutlinedText(canvas, priceText, rightX - 5f, itemTextY, singlePricePaint, Color.WHITE, strokeWidth)
                }

                currentY += 21f
            }

            currentY += 12f
        }

        drawFooterPaymentInfo(canvas, menuData.header.paymentNote)
    }

    private fun drawPage2(canvas: Canvas, menuData: MenuData) {
        var currentY = drawBackgroundAndHeader(
            canvas,
            R.drawable.bg_page2,
            menuData.header.restaurantName,
            menuData.header.phone1,
            menuData.header.phone2
        )

        val categoryLanches = menuData.categories.firstOrNull { it.layoutType == CategoryLayoutType.LANCHES }
        val lanchesItems = menuData.items.filter { it.categoryId == categoryLanches?.id }

        val leftX = 35f
        val rightX = pageWidth - 35f

        val bannerHeight = 24f
        val bannerRect = RectF(leftX, currentY, rightX, currentY + bannerHeight)
        val bannerPaint = Paint().apply {
            color = primaryColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(bannerRect, 4f, 4f, bannerPaint)

        val catTitlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 12.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val catName = categoryLanches?.name ?: "LANCHES"
        drawOutlinedText(canvas, catName.uppercase(), leftX + 10f, currentY + 17f, catTitlePaint, Color.BLACK, strokeWidth)

        currentY += bannerHeight + 9f

        val namePaint = Paint().apply {
            color = darkTextColor
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val descPaint = Paint().apply {
            color = Color.WHITE
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val pricePaint = Paint().apply {
            color = darkTextColor
            textSize = 11.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }

        val dotPaint = Paint().apply {
            color = Color.DKGRAY
            style = Paint.Style.STROKE
            strokeWidth = 1f
            pathEffect = DashPathEffect(floatArrayOf(2f, 4f), 0f)
            isAntiAlias = true
        }

        for (item in lanchesItems) {
            val itemTopY = currentY

            val titleText = if (item.number.isNotBlank()) "${item.number} ${item.name}" else item.name
            drawOutlinedText(canvas, titleText, leftX + 5f, itemTopY + 11f, namePaint, Color.WHITE, strokeWidth)

            val priceText = formatPrice(item.singlePrice)
            drawOutlinedText(canvas, priceText, rightX - 5f, itemTopY + 11f, pricePaint, Color.WHITE, strokeWidth)

            val priceWidth = pricePaint.measureText(priceText)
            val titleWidth = namePaint.measureText(titleText)

            val dotsStartX = leftX + 15f + titleWidth
            val dotsEndX = rightX - priceWidth - 10f
            if (dotsEndX > dotsStartX) {
                canvas.drawLine(dotsStartX, itemTopY + 8f, dotsEndX, itemTopY + 8f, dotPaint)
            }

            if (item.description.isNotBlank()) {
                drawOutlinedText(canvas, item.description, leftX + 18f, itemTopY + 23f, descPaint, Color.DKGRAY, strokeWidth)
                currentY += 29f
            } else {
                currentY += 20f
            }
        }

        drawFooterPaymentInfo(canvas, menuData.header.paymentNote)
    }

    private fun formatPrice(amount: Double): String {
        return String.format(Locale("pt", "BR"), "%.2f", amount)
    }

}
