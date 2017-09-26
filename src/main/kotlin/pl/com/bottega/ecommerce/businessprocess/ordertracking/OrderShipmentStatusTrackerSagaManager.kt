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

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Query

import org.springframework.stereotype.Component

import pl.com.bottega.ecommerce.canonicalmodel.events.OrderSubmittedEvent
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId
import pl.com.bottega.ecommerce.shipping.domain.events.OrderShippedEvent
import pl.com.bottega.ecommerce.shipping.domain.events.ShipmentDeliveredEvent
import pl.com.bottega.ecommerce.system.saga.SagaManager
import pl.com.bottega.ecommerce.system.saga.annotations.LoadSaga

@Component
class OrderShipmentStatusTrackerSagaManager : SagaManager<OrderShipmentStatusTrackerSaga, OrderShipmentStatusTrackerData> {

    @PersistenceContext
    private val entityManager: EntityManager? = null

    @LoadSaga
    fun loadSaga(event: OrderSubmittedEvent): OrderShipmentStatusTrackerData {
        return findByOrderId(event.orderId)
    }

    @LoadSaga
    fun loadSaga(event: OrderShippedEvent): OrderShipmentStatusTrackerData {
        return findByOrderId(event.orderId)
    }

    @LoadSaga
    fun loadSaga(event: ShipmentDeliveredEvent): OrderShipmentStatusTrackerData {
        return findByShipmentId(event.shipmentId)
    }

    private fun findByOrderId(orderId: AggregateId): OrderShipmentStatusTrackerData {
        val query = entityManager!!.createQuery("from OrderShipmentStatusTrackerData where orderId=:orderId")
                .setParameter("orderId", orderId)
        return query.singleResult as OrderShipmentStatusTrackerData
    }

    private fun findByShipmentId(shipmentId: AggregateId): OrderShipmentStatusTrackerData {
        val query = entityManager!!.createQuery("from OrderShipmentStatusTrackerData where shipmentId=:shipmentId")
                .setParameter("shipmentId", shipmentId)
        return query.singleResult as OrderShipmentStatusTrackerData
    }

    override fun removeSaga(saga: OrderShipmentStatusTrackerSaga) {
        val sagaData = entityManager!!.merge(saga.data)
        entityManager.remove(sagaData)
    }

    override fun createNewSagaData(): OrderShipmentStatusTrackerData {
        val sagaData = OrderShipmentStatusTrackerData()
        entityManager!!.persist(sagaData)
        return sagaData
    }
}
