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
package pl.com.bottega.ecommerce.sales.readmodel.impl

import com.google.common.collect.Lists.transform

import java.util.ArrayList

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

import pl.com.bottega.cqrs.query.PaginatedResult
import pl.com.bottega.ddd.annotations.domain.FinderImpl
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId
import pl.com.bottega.ecommerce.sales.domain.purchase.Purchase
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservedProduct
import pl.com.bottega.ecommerce.sales.readmodel.orders.OrderDto
import pl.com.bottega.ecommerce.sales.readmodel.orders.OrderFinder
import pl.com.bottega.ecommerce.sales.readmodel.orders.OrderQuery
import pl.com.bottega.ecommerce.sales.readmodel.orders.OrderStatus
import pl.com.bottega.ecommerce.sales.readmodel.orders.OrderedProductDto

import com.google.common.base.Function

@FinderImpl
class JpaOrderFinder : OrderFinder {

    @PersistenceContext
    private val entityManager: EntityManager? = null

    override fun find(orderId: AggregateId): OrderDto {
        val reservation = entityManager!!.find(Reservation::class.java, orderId)
        val purchase = entityManager.find(Purchase::class.java, orderId)

        return toOrderDto(reservation, purchase)
    }

    private fun toOrderDto(reservation: Reservation, purchase: Purchase?): OrderDto {
        val dto = OrderDto()
        dto.orderId = reservation.aggregateId
        val reservedProducts = reservation.reservedProducts
        dto.orderedProducts = ArrayList(transform(reservedProducts,
                reservedProductToOrderedProductDto()))
        if (purchase != null) {
            dto.status = OrderStatus.CONFIRMED

            // TODO CHECK PAYMENT!

        } else {
            dto.status = OrderStatus.NEW
        }
        return dto
    }

    private fun reservedProductToOrderedProductDto(): Function<ReservedProduct, OrderedProductDto> {
        return Function { product ->
            val dto = OrderedProductDto()
            dto.offerId = product!!.productId
            dto
        }
    }

    override fun query(orderQuery: OrderQuery): PaginatedResult<OrderDto>? {
        return null
    }

}
