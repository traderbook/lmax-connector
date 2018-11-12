package com.traderbook.connector

import com.lmax.api.Callback
import com.lmax.api.FailureResponse
import com.lmax.api.Session
import com.traderbook.api.enums.Messages
import com.traderbook.api.models.BrokerAccount

class CustomLogoutCallback(private val connector: Connector, private val brokerAccount: BrokerAccount): Callback {
    override fun onSuccess() {
        connector.update(Messages.LOGOUT_SUCCESS, brokerAccount)
    }

    override fun onFailure(failureResponse: FailureResponse) {
        connector.update(Messages.LOGOUT_FAILURE, null)
    }
}