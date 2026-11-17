package de.surroundlab.app

import android.app.Application
import de.surroundlab.app.data.SurroundRepository

/**
 * Application entry point holding the single SurroundRepository instance.
 */
class SurroundLabApp : Application() {

    lateinit var repository: SurroundRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = SurroundRepository(this)
    }

    override fun onTerminate() {
        repository.shutdown()
        super.onTerminate()
    }
}
