package com.kaancelen.firestoresecrets.local

interface ServiceOneSecretsProvider {

    fun setSecrets(clientId: String, secret: String)

    fun getClientId(): String?

    fun getSecret(): String?

    fun hasSecrets(): Boolean
}