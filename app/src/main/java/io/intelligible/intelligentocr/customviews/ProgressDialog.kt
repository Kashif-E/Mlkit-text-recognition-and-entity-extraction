package io.intelligible.intelligentocr.customviews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import io.intelligible.intelligentocr.databinding.CustomProgressDialogBinding


class ProgressDialog(
    context: Context,
    private val isCancelable: Boolean = true
) : AlertDialog(context) {

    override fun show() {
        super.show()
        setProgressText()
    }

    override fun onStart() {
        super.onStart()
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.setCancelable(isCancelable)
    }

    override fun setCancelable(flag: Boolean) {
        super.setCancelable(isCancelable)
    }

    private fun setProgressText() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = CustomProgressDialogBinding.inflate(inflater)
        val doubleBounce: Sprite =DoubleBounce()
        view.progressBarDialog.setIndeterminateDrawable(doubleBounce)
        setContentView(view.root)
    }
}