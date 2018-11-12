package com.traderbook.connector

import com.lmax.api.FailureResponse
import com.lmax.api.Session
import com.lmax.api.account.LoginCallback
import com.lmax.api.orderbook.OrderBookSubscriptionRequest
import com.traderbook.api.enums.Instruments
import com.traderbook.api.enums.Messages
import com.traderbook.api.models.BrokerAccount
import com.traderbook.api.models.Instrument
import com.traderbook.api.models.InstrumentCollection
import com.traderbook.connector.helpers.InstrumentHelper
import kotlin.concurrent.thread

class CustomLoginCallback(private val connector: Connector) : LoginCallback {
    private val instrumentsRef = mapOf(
            Instruments.EURUSD to 4001L,
            Instruments.GBPUSD to 4002L
    )

    private val instruments = mutableMapOf<Instruments, Instrument>()

    private var session: Session? = null

    init {
        instrumentsRef.forEach { instruments[it.key] = Instrument(it.value.toString(), it.key, 0.0, 0.0, 0.0, 0.0) }
    }

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
    override fun onLoginSuccess(session: Session?) {
        this.session = session

        connector.update(Messages.SUCCESS_LOGIN, BrokerAccount(
                this.session!!.accountDetails.username,
                this.session!!.accountDetails.accountId.toString(),
                this.session!!.accountDetails.currency
        ))

        this.session!!.registerOrderBookEventListener {
            val instrumentName = InstrumentHelper.getInstrumentByValue(it.instrumentId, instrumentsRef)

            instruments[instrumentName]?.oldAsk = instruments[instrumentName]!!.ask
            instruments[instrumentName]?.ask = it.valuationAskPrice.toString().toDouble()
            instruments[instrumentName]?.oldBid = instruments[instrumentName]!!.bid
            instruments[instrumentName]?.bid = it.valuationBidPrice.toString().toDouble()

            connector.update(Messages.INSTRUMENTS_UPDATED, InstrumentCollection(instruments))
        }


        thread {
            instruments.forEach {
                this.session!!.subscribe(OrderBookSubscriptionRequest(it.value.id.toLong()), CustomSubscriptionCallback())
            }

            this.session!!.start()
        }
    }

    /**
     * Permet d'intercepter l'erreur d'authentification
     */
    override fun onLoginFailure(failureResponse: FailureResponse?) {
        if (failureResponse!!.message == "Invalid character found in beginning of XML comment: 'D'") {
            connector.update(Messages.BROKER_MAINTENANCE_MODE, null)
        } else {
            connector.update(Messages.BAD_CREDENTIALS, null)
        }
    }
}