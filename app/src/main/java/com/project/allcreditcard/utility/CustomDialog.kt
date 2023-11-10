package com.project.allcreditcard.utility

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.TextView
import com.project.allcreditcard.R

class CustomDialog(context: Context) {
    private val dialog = Dialog(context)
    //private lateinit var listener: CustomDialogOkClickedListener

    private lateinit var dialogContent: TextView
    private lateinit var okBtn: Button

    fun show(title: String, content: String) {
        dialog.setContentView(R.layout.custom_dialog)
        dialog.setCancelable(false)

        dialogContent = dialog.findViewById(R.id.dialogContent)
        okBtn = dialog.findViewById(R.id.okBtn)

        dialog.setTitle(title)
        dialogContent.text = content

        okBtn.setOnClickListener {
            //listener.onOkClicked("확인 클릭")
            dialog.dismiss()
        }
        dialog.show()
    }

    /*fun setOnClickedListener(listener: (String) -> Unit) {
        this.listener = object: CustomDialogOkClickedListener {
            override fun onOkClicked(content: String) {
                listener(content)
            }

        }
    }

    //확인 버튼 클릭 리스너 생성 (필요시 사용 예정)
    interface CustomDialogOkClickedListener {
        fun onOkClicked(content: String)
    }*/
}