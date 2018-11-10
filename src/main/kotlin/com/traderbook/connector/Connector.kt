package com.traderbook.connector

import com.lmax.api.LmaxApi
import com.lmax.api.account.LoginRequest
import com.lmax.api.account.LoginRequest.ProductType
import com.traderbook.api.AccountType
import com.traderbook.api.enums.Messages
import com.traderbook.api.interfaces.IConnector
import com.traderbook.api.interfaces.IConnectorObserver

class Connector(private var connector: IConnectorObserver): IConnector, IConnectorObserver {
    private var lmax: LmaxApi? = null
    private var loginRequest: LoginRequest? = null
    private val customLoginCallback = CustomLoginCallback(this)

    override fun connection(accountType: AccountType, username: String, password: String) {
        val url = if (accountType == AccountType.DEMO) "https://web-order.london-demo.lmax.com/" else ""
        val productType = if (accountType == AccountType.DEMO) ProductType.CFD_DEMO else ProductType.CFD_LIVE

        lmax = LmaxApi(url)
        loginRequest = LoginRequest(username, password, productType)
    }

    override fun start() {
        lmax!!.login(loginRequest, customLoginCallback)
    }

    override fun stop() {
        customLoginCallback.logout()
    }

    override fun update(message: Messages, data: Any?) {
        connector.update(message, data)
    }

}