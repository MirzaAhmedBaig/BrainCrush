package braincrush.mirza.com.braincrush.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.interfaces.CustomAlertDismissListener
import kotlinx.android.synthetic.main.fragment_alert_dialog.*
import kotlinx.android.synthetic.main.fragment_alert_dialog.view.*


class AlertDialogFragment : android.support.v4.app.DialogFragment() {
    companion object {
        val SIMPLE_TYPE = 0
        val YES_NO_TYPE = 1
    }

    val TAG = AlertDialogFragment::class.java.simpleName
    private var customAlertDismissListener: CustomAlertDismissListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.fragment_alert_dialog, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBackground(view)
        initListeners()
    }

    private fun initBackground(view: View) {
        arguments?.let {
            with(it) {
                titleTV.text = getString("title")
                messageTV.text = getString("msg")
                if (getInt("type") == YES_NO_TYPE) {
                    view.only_ok_button.visibility = View.GONE
                    view.ok_button.visibility = View.VISIBLE
                    view.cancel_button.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initListeners() {
        ok_button.setOnClickListener {
            dismiss()
            customAlertDismissListener?.onPositiveClicked()
        }
        cancel_button.setOnClickListener {
            dismiss()
            customAlertDismissListener?.onNegativeClicked()
        }
        only_ok_button.setOnClickListener {
            dismiss()
            customAlertDismissListener?.onPositiveClicked()
        }
    }


    fun setDismissListener(customAlertDismissListener: CustomAlertDismissListener) {
        this.customAlertDismissListener = customAlertDismissListener
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        try {
            manager?.beginTransaction()?.add(this, tag)?.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
