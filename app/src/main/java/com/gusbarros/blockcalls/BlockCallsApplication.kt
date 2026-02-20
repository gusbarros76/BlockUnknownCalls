package com.gusbarros.blockcalls

import android.app.Application
import com.gusbarros.blockcalls.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application class para inicialização do Koin DI.
 */
class BlockCallsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar Koin
        startKoin {
            // Log apenas em debug builds
            androidLogger(Level.ERROR)
            androidContext(this@BlockCallsApplication)
            modules(appModule)
        }
    }
}
