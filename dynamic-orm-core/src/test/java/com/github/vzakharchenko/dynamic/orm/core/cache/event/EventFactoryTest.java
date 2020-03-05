package com.github.vzakharchenko.dynamic.orm.core.cache.event;

import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.TransactionEventType;
import com.github.vzakharchenko.dynamic.orm.model.Testtable;
import com.github.vzakharchenko.dynamic.orm.qModel.QTesttable;
import org.testng.annotations.Test;

import java.util.HashMap;

import static org.testng.Assert.assertNotNull;

public class EventFactoryTest {
    @Test
    public void testEventFactory() {
        DiffEvent diffEvent = EventFactory.updateDiffEvent(
                QTesttable.testtable,
                Testtable.class, new HashMap<>());
        TransactionEventType transactionType = diffEvent.getTransactionType();
        assertNotNull(transactionType);
        assertNotNull(diffEvent.getListIds());
        assertNotNull(diffEvent.getModelClass());
        assertNotNull(diffEvent.getTransactionHistory());
        assertNotNull(diffEvent.cacheEventType());
    }
}
