package com.cardee.account_verify.identity_card

import android.net.Uri
import com.cardee.mvp.BaseView


interface IdentityCardView : BaseView {
    fun onPhotosUploaded()
}