package com.github.vzakharchenko.dynamic.orm.core.transaction.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.TransactionEvent;
import com.github.vzakharchenko.dynamic.orm.core.transaction.event.transaction.TransactionalCombinedEvent;

/**
 * send event before or after commit Transaction
 *
 * @see TransactionEvent
 * @see TransactionalCombinedEvent
 */
public interface TransactionalEventPublisher
        extends ApplicationEventPublisherAware, ApplicationEventPublisher {
}
