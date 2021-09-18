package com.github.odaridavid.datastoresample

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "com.github.odaridavid.sharedpreference.AppDataStore"
)

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val saveHighScoreButton = findViewById<Button>(R.id.saveHighestScoreButton)
        val displayHighScore = findViewById<TextView>(R.id.displayHighestScoreTextView)
        val highScoreEditText = findViewById<EditText>(R.id.highestScoreEditText)

        // Read from data store

        val HIGH_SCORE_PREF_KEY: Preferences.Key<Int> = intPreferencesKey(HIGH_SCORE_KEY)

        val highScoreFlow: Flow<Int> = dataStore.data
            .map { appDataStore ->
                appDataStore[HIGH_SCORE_PREF_KEY] ?: 0
            }

        lifecycleScope.launchWhenCreated {
            highScoreFlow.collect { highestScoreValue ->
                displayHighScore.text = "Your current high score is $highestScoreValue"
            }
        }


        // Write to Data Store
        saveHighScoreButton.setOnClickListener {
            val highestScore = highScoreEditText.text.toString().toInt()
            lifecycleScope.launchWhenCreated {
                dataStore.edit { appDataStore ->
                    appDataStore[HIGH_SCORE_PREF_KEY] = highestScore
                }
            }
        }
    }

    companion object {
        private const val HIGH_SCORE_KEY = "HIGH_SCORE_KEY"
    }
}
