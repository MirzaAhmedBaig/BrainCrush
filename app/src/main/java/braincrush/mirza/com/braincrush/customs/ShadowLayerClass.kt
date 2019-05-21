package braincrush.mirza.com.braincrush.customs

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.shapes.Shape
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur


internal open class ShadowLayerClass(var ctx: Context, var source: Drawable, var mask: Drawable, var shadow: Drawable, var shadowRadius: Float) : Shape() {
    private var matrix = Matrix()
    private var bitmap: Bitmap? = null

    override fun onResize(width: Float, height: Float) {
        val intrinsicWidth = source.intrinsicWidth
        val intrinsicHeight = source.intrinsicHeight
        source.setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        mask.setBounds(20, 20, intrinsicWidth - 20, intrinsicHeight - 20)
        shadow.setBounds(20, 20, intrinsicWidth - 20, intrinsicHeight - 20)

        val src = RectF(0f, 0f, intrinsicWidth.toFloat(), intrinsicHeight.toFloat())
        val dst = RectF(0f, 0f, width, height)
        matrix.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER)

        val p = Paint()
        p.style = Paint.Style.STROKE
        p.isAntiAlias = true
        p.color = Color.WHITE
        p.strokeWidth = 5f
        p.strokeCap = Paint.Cap.ROUND
        p.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        var c = Canvas(bitmap)

        shadow.draw(c)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            c.saveLayer(null, p)
        } else {
            c.saveLayer(null, p, Canvas.ALL_SAVE_FLAG)
        }
        mask.draw(c)
        c.restore()
        bitmap = blurRenderScript(bitmap!!)

        c = Canvas(bitmap)
        val count = c.saveLayer(null, null, Canvas.ALL_SAVE_FLAG)
        source.draw(c)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            c.saveLayer(null, p)
        } else {
            c.saveLayer(null, p, Canvas.ALL_SAVE_FLAG)
        }
        mask.draw(c)
        c.restoreToCount(count)
    }

    override fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawBitmap(bitmap, matrix, null)
    }

    private fun blurRenderScript(input: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(input.width, input.height, input.config)
        val rs = RenderScript.create(ctx)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val inAlloc = Allocation.createFromBitmap(rs, input, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_GRAPHICS_TEXTURE)
        val outAlloc = Allocation.createFromBitmap(rs, output)
        script.setRadius(shadowRadius)
        script.setInput(inAlloc)
        script.forEach(outAlloc)
        outAlloc.copyTo(output)
        rs.destroy()
        return output
    }
}