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
import kotlinx.android.synthetic.main.fragmnet_game_info.*

class GameInfoDialog : android.support.v4.app.DialogFragment() {

    val TAG = GameInfoDialog::class.java.simpleName
    private var customAlertDismissListener: CustomAlertDismissListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.fragmnet_game_info, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        only_ok_button.setOnClickListener {
            dismiss()
            customAlertDismissListener?.onPositiveClicked()
        }
    }


    fun setDismissListener(customAlertDismissListener: CustomAlertDismissListener) {
        this.customAlertDismissListener = customAlertDismissListener
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        manager?.beginTransaction()?.add(this, tag)?.commitAllowingStateLoss()
    }

}
