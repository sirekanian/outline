package org.sirekanyan.outline

import org.acra.ACRA
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.HttpSenderConfigurationBuilder
import org.acra.sender.HttpSender

object CrashReporter {

    fun init(app: App) {
        val httpSenderConfig = HttpSenderConfigurationBuilder()
            .withUri(BuildConfig.ACRA_URI)
            .withHttpMethod(HttpSender.Method.POST)
            .withBasicAuthLogin(BuildConfig.ACRA_LOGIN)
            .withBasicAuthPassword(BuildConfig.ACRA_PASSWORD)
            .withEnabled(true)
            .build()
        val config = CoreConfigurationBuilder()
            .withBuildConfigClass(BuildConfig::class.java)
            .withPluginConfigurations(httpSenderConfig)
            .build()
        ACRA.init(app, config)
    }

    fun handleException(throwable: Throwable) {
        ACRA.errorReporter.handleException(throwable)
    }

}