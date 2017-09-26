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
package pl.com.bottega.ecommerce.businessprocess.ordertracking

import javax.inject.Inject

import pl.com.bottega.ecommerce.canonicalmodel.events.OrderSubmittedEvent
import pl.com.bottega.ecommerce.sales.readmodel.orders.OrderFinder
import pl.com.bottega.ecommerce.shipping.domain.events.OrderShippedEvent
import pl.com.bottega.ecommerce.shipping.domain.events.ShipmentDeliveredEvent
import pl.com.bottega.ecommerce.system.saga.SagaInstance
import pl.com.bottega.ecommerce.system.saga.annotations.Saga
import pl.com.bottega.ecommerce.system.saga.annotations.SagaAction

@Saga
class OrderShipmentStatusTrackerSaga(override var data: OrderShipmentStatusTrackerData) : SagaInstance<OrderShipmentStatusTrackerData>(data) {

    @Inject
    private val orderFinder: OrderFinder? = null

    @SagaAction
    fun handleOrderCreated(event: OrderSubmittedEvent) {
        data.orderId = event.orderId
        completeIfPossible()
    }

    @SagaAction
    fun orderShipped(event: OrderShippedEvent) {
        data.orderId = event.orderId
        data.shipmentId = event.shipmentId
        completeIfPossible()
    }

    @SagaAction
    fun shipmentDelivered(event: ShipmentDeliveredEvent) {
        data.shipmentId = event.shipmentId
        data.shipmentReceived = true
        completeIfPossible()
    }

    private fun completeIfPossible() {
        if (data.orderId != null && data.shipmentId != null && data.shipmentReceived!!) {
            //TODO move process forward, ex call service or publish event

            markAsCompleted()
        }
    }
}
