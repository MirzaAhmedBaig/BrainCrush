package braincrush.mirza.com.braincrush.models

import io.realm.RealmObject

data class ScoreModel(
        var levelNumber: Int = 0,
        var levelScore: Float = 0f,
        var thumbnail: Int = 0,
        var isLocked: Boolean = true
) : RealmObject()