package com.jywoo.payment.domain.supported;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Disabled
class VatCalcuratorBaseTest {


    @Test
    void calc() {
        long price = 1000L;
        long vat = Math.round((double) price / 11);

        assertEquals(vat, 91L);
    }

}