package braincrush.mirza.com.braincrush

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_dummy.*

private val TAG = DummyActivity::class.java.simpleName
private val list = arrayOf("1", "2", "3", "4", "5")

class DummyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)
        main_show_dialog_btn.setOnClickListener {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM)
            startActivity(intent)

        }
    }

}

fun main(args: Array<String>) {
    var newList = list.clone()

    list.forEach {
        System.out.print("\nList : $it")
    }

    newList.forEach {
        System.out.print("\nNew List : $it")
    }

    list[2] = "mirza"

    list.forEach {
        System.out.print("\nM List : $it")
    }

    newList.forEach {
        System.out.print("\nM New List : $it")
    }
}