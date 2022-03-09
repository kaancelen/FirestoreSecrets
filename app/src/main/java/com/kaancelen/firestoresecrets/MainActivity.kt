package com.kaancelen.firestoresecrets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.kaancelen.firestoresecrets.entity.ApiSecrets
import com.kaancelen.firestoresecrets.local.ServiceOneSecretsProvider
import com.kaancelen.firestoresecrets.local.ServiceOneSecretsProviderImpl

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var serviceOneSecretsProvider: ServiceOneSecretsProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serviceOneSecretsProvider = createServiceOneSecretsProvider(applicationContext)
        textView = findViewById(R.id.status_text)

        if (!serviceOneSecretsProvider.hasSecrets()) {
            fetchFirestoreData(
                {
                    serviceOneSecretsProvider.setSecrets(it.clientId.orEmpty(), it.secret.orEmpty())
                    textView.text = getString(R.string.firestore_secret_success_text, it.clientId, it.secret)
                },
                {
                    textView.setText(R.string.firestore_secret_error_text)
                }
            )
        } else {
            val clientId = serviceOneSecretsProvider.getClientId()
            val secret = serviceOneSecretsProvider.getSecret()
            textView.text = getString(R.string.firestore_secret_locally_stored, clientId, secret)
        }
    }

    private fun fetchFirestoreData(success: FetchSuccess, failed: FetchFailed) {
        Firebase.firestore
            .collection("secrets")
            .document("service1")
            .get()
            .addOnFailureListener {
                failed()
            }
            .addOnSuccessListener { documentSnapshot ->
                val data = documentSnapshot.toObject<ApiSecrets>()
                if (data == null) failed() else success(data)
            }
    }

    private fun createServiceOneSecretsProvider(context: Context): ServiceOneSecretsProvider {
        return ServiceOneSecretsProviderImpl(createSecretPreferences(context))
    }

    private fun createSecretPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "service_one_secrets",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}

typealias FetchSuccess = (ApiSecrets) -> Unit
typealias FetchFailed = () -> Unit