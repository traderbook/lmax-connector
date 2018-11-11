package helpers

import com.traderbook.api.enums.Instruments
import com.traderbook.connector.helpers.InstrumentHelper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class InstrumentHelperTest {
    private val instrumentsRef = mapOf(
            Instruments.EURUSD to 4001L,
            Instruments.GBPUSD to 4002L
    )

    @Nested
    inner class GetInstrumentByValue {
        @Test
        fun shouldReturnValue() {
            val instrument = InstrumentHelper.getInstrumentByValue(4001L, instrumentsRef)

            assert(instrument == Instruments.EURUSD)
        }

        @Test
        fun shouldReturnNull() {
            val instrument = InstrumentHelper.getInstrumentByValue(4003L, instrumentsRef)

            assertNull(instrument)
        }

    }
}