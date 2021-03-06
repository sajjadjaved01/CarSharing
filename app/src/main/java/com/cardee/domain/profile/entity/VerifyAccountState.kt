package com.cardee.domain.profile.entity

import android.databinding.BaseObservable
import android.databinding.ObservableBoolean


data class VerifyAccountState(val statusStrings: Array<String> = arrayOf("Added", "Add"),
                              val particularsAdded: ObservableBoolean = ObservableBoolean(),
                              val identityAdded: ObservableBoolean = ObservableBoolean(),
                              val licenseAdded: ObservableBoolean = ObservableBoolean(),
                              val photoAdded: ObservableBoolean = ObservableBoolean(),
                              val creditAdded: ObservableBoolean = ObservableBoolean(),
                              val depositAdded: ObservableBoolean = ObservableBoolean(),
                              var name: String = "",
                              var identityCard: String = "",
                              var address: String = "",
                              var phone: String = "",
                              var birthDate: String = "",
                              var licenseDate: String = "",
                              var expMonth: Int = 0,
                              var expYear: Int = 0,
                              var creditCardNum: String = "",
                              var cvv: Int = 0,
                              var primaryCard: Boolean = false) : BaseObservable()