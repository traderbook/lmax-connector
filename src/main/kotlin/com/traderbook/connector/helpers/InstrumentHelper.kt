package com.traderbook.connector.helpers

import com.traderbook.api.enums.Instruments

object InstrumentHelper {
    fun getInstrumentByValue(code: Long, instrumentsList: Map<Instruments, Long>): Instruments? {
        val instrumentFiltered = instrumentsList.filterValues { it == code }
        val instrument = instrumentFiltered.iterator()

        if(instrument.hasNext()) {
            return instrument.next().key
        }

        return null
    }
}