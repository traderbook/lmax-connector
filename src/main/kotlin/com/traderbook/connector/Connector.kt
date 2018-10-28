package com.traderbook.connector

import com.traderbook.api.AccountType
import com.traderbook.api.interfaces.IConnector

class Connector: IConnector {
    override fun getName(): String {
        return "Lmax Exchange"
    }
    
    override fun connection(accountType: AccountType, username: String, password: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}