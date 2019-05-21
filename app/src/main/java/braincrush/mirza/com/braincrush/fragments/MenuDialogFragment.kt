package braincrush.mirza.com.braincrush.fragments

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import braincrush.mirza.com.braincrush.R
import braincrush.mirza.com.braincrush.interfaces.PausedMenuListener
import kotlinx.android.synthetic.main.fragment_menu_dialog.*

class MenuDialogFragment : android.support.v4.app.DialogFragment() {

    val TAG = MenuDialogFragment::class.java.simpleName
    private var pausedMenuListener: PausedMenuListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.fragment_menu_dialog, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }


    private fun initListeners() {
        btn_resume.setOnClickListener {
            dismiss()
            pausedMenuListener?.onResumeGame()
        }
        btn_restart.setOnClickListener {
            dismiss()
            pausedMenuListener?.onRestartGame()
        }
        btn_quit.setOnClickListener {
            dismiss()
            pausedMenuListener?.onQuitGame()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        pausedMenuListener?.onResumeGame()
    }


    fun setMenuListener(pausedMenuListener: PausedMenuListener) {
        this.pausedMenuListener = pausedMenuListener
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        try {
            manager?.beginTransaction()?.add(this, tag)?.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}