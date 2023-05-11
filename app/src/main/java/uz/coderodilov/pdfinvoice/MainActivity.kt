package uz.coderodilov.pdfinvoice

import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import uz.coderodilov.pdfinvoice.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var document:PdfDocument

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissionStatus()

    }

    private fun checkPermissionStatus() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        } else binding.btnGetCheck.setOnClickListener {
            // createInvoice()
            invoiceDialog()
        }
    }

    private fun invoiceDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.invoice_ui)
        dialog.setCancelable(false)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog.window!!.attributes = layoutParams

        val download = dialog.findViewById<ImageView>(R.id.btnDownload)

        dialog.show()

        download.setOnClickListener{
            generatePdfFromView(dialog.findViewById(R.id.zigzagView))
            dialog.dismiss()
        }
    }


    private fun generatePdfFromView(view: View) {
     // val viewBitmap = getBitmapFromView(view)
        document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(view.width, view.height, 1).create()
        val myPage = document.startPage(pageInfo)
        val canvas = myPage.canvas
        view.draw(canvas)
     // canvas.drawBitmap(viewBitmap, 0f, 0f, null)
        document.finishPage(myPage)
        createAndSaveFile(document)
        document.close()
    }

    private fun createAndSaveFile(document: PdfDocument) {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(path, "invoice.pdf")
        try {
            document.writeTo(FileOutputStream(file))
            Toast.makeText(this, "Invoice saved to storage", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }



    /*
    private fun getBitmapFromView(view: View) : Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background

        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }
     */

}