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
package pl.com.bottega.ecommerce.sales.application.listeners

import javax.inject.Inject

import pl.com.bottega.cqrs.query.PaginatedResult
import pl.com.bottega.ddd.annotations.event.EventListener
import pl.com.bottega.ddd.annotations.event.EventListeners
import pl.com.bottega.ecommerce.canonicalmodel.events.CustomerStatusChangedEvent
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId
import pl.com.bottega.ecommerce.sales.application.internal.discounts.DiscountingService
import pl.com.bottega.ecommerce.sales.readmodel.orders.OrderDto
import pl.com.bottega.ecommerce.sales.readmodel.orders.OrderFinder
import pl.com.bottega.ecommerce.sales.readmodel.orders.OrderQuery
import pl.com.bottega.ecommerce.sharedkernel.Money

/**
 * Sample Anti-corruption Layer: translates Customer-Client vocabulary
 * <br></br>
 * Applies discount
 *
 * @author Slawek
 */
@EventListeners
class ClientStatusChangedListener {

    @Inject
    private val discountingService: DiscountingService? = null
    @Inject
    private val orderFinder: OrderFinder? = null

    @EventListener
    fun handle(event: CustomerStatusChangedEvent) {
        val orderQuery = OrderQuery(null, event.customerId)
        val orders = orderFinder!!.query(orderQuery)

        val discount = calculateDiscout(event.customerId)

        for (dto in orders!!.items) {
            discountingService!!.applyDiscount(dto.orderId!!, discount)
        }
    }

    private fun calculateDiscout(customerId: AggregateId): Money {
        // TODO explore domain rules
        return Money(10.0)
    }
}
