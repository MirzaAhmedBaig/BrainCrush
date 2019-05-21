package braincrush.mirza.com.braincrush.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ScoreModel : RealmObject() {
    @PrimaryKey
    var levelNumber: Int = 0
    var levelScore: Int = 0
    var thumbnail: Int = 0
    var isLocked: Boolean = true
}
