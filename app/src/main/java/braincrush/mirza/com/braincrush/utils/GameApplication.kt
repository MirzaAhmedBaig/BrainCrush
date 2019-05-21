package braincrush.mirza.com.braincrush.utils

import android.app.Application
import io.realm.Realm


class GameApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }

}