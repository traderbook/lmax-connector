package com.traderbook.connector

import com.lmax.api.FailureResponse
import com.lmax.api.Session
import com.lmax.api.account.LoginCallback
import com.traderbook.api.enums.Instruments
import com.traderbook.api.enums.Messages
import com.traderbook.api.models.BrokerAccount
import kotlin.concurrent.thread

class CustomLoginCallback(private val connector: Connector) : LoginCallback {
    private var session: Session? = null

    /**
     * Permet la déconnexion d'un compte chez le broker.
     * Les informations du compte sont envoyé afin de renseigné l'application quel compte de trading est déconnecté
     * par le biais de la propriété accountId
     */
    fun logout() {
        val brokerAccount = BrokerAccount(
                this.session!!.accountDetails.username,
                this.session!!.accountDetails.accountId.toString(),
                this.session!!.accountDetails.currency
        )

        this.session!!.logout(CustomLogoutCallback(connector, brokerAccount))
    }

    /**
     * Permet d'intercepter le succès d'authentification
     */
    override fun onLoginSuccess(session: Session) {
        this.session = session

        connector.update(Messages.SUCCESS_LOGIN, BrokerAccount(
                session.accountDetails.username,
                session.accountDetails.accountId.toString(),
                session.accountDetails.currency
        ))

        thread { this.session!!.start() }
    }

    /**
     * Permet d'intercepter l'erreur d'authentification
     */
    override fun onLoginFailure(failureResponse: FailureResponse?) {
        if(failureResponse!!.message == "Invalid character found in beginning of XML comment: 'D'") {
            connector.update(Messages.BROKER_MAINTENANCE_MODE, null)
        } else {
            connector.update(Messages.BAD_CREDENTIALS, null)
        }
    }
}