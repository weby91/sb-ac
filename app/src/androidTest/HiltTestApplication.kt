package jp.speakbuddy.edisonandroidexercise

import android.app.Application
import dagger.hilt.android.testing.CustomTestApplication

@CustomTestApplication(EdisonApplication::class)
interface HiltTestApplication