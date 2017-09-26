/*
 * Copyright 2011-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.com.bottega.ecommerce.shipping.domain

import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Column
import javax.persistence.Entity

import pl.com.bottega.ddd.annotations.domain.AggregateRoot
import pl.com.bottega.ddd.support.domain.BaseAggregateRoot
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId
import pl.com.bottega.ecommerce.shipping.domain.events.OrderShippedEvent
import pl.com.bottega.ecommerce.shipping.domain.events.ShipmentDeliveredEvent

/**
 * @author Rafał Jamróz
 */
@Entity
@AggregateRoot
class Shipment : BaseAggregateRoot {

    @AttributeOverrides(AttributeOverride(name = "aggregateId", column = Column(name = "orderId")))
    lateinit var orderId: AggregateId
        private set

    private var status: ShippingStatus? = null


    private constructor()

    internal constructor(shipmentId: AggregateId, orderId: AggregateId) {
        this.aggregateId = shipmentId
        this.orderId = orderId
        this.status = ShippingStatus.WAITING
    }

    /**
     * Shipment has been sent to the customer.
     */
    fun ship() {
        if (status != ShippingStatus.WAITING) {
            throw IllegalStateException("cannot ship in status " + status!!)
        }
        status = ShippingStatus.SENT
        eventPublisher!!.publish(OrderShippedEvent(orderId, aggregateId))
    }

    /**
     * Shipment has been confirmed received by the customer.
     */
    fun deliver() {
        if (status != ShippingStatus.SENT) {
            throw IllegalStateException("cannot deliver in status " + status!!)
        }
        status = ShippingStatus.DELIVERED
        eventPublisher!!.publish(ShipmentDeliveredEvent(aggregateId))
    }

}
