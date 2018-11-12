package com.traderbook.connector

import com.lmax.api.Callback
import com.lmax.api.FailureResponse

class CustomSubscriptionCallback: Callback {
    override fun onSuccess() {
        println("Instrument subscribed")
    }

    override fun onFailure(failureResponse: FailureResponse) {
        println(failureResponse.message)
    }
}