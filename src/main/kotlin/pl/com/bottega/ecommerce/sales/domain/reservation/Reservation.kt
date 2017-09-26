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
package pl.com.bottega.ecommerce.sales.domain.reservation

import java.util.ArrayList
import java.util.Date

import javax.persistence.CascadeType
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

import pl.com.bottega.ddd.annotations.domain.AggregateRoot
import pl.com.bottega.ddd.annotations.domain.Function
import pl.com.bottega.ddd.annotations.domain.Invariant
import pl.com.bottega.ddd.annotations.domain.InvariantsList
import pl.com.bottega.ddd.support.domain.BaseAggregateRoot
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData
import pl.com.bottega.ecommerce.sales.domain.offer.Discount
import pl.com.bottega.ecommerce.sales.domain.offer.DiscountPolicy
import pl.com.bottega.ecommerce.sales.domain.offer.Offer
import pl.com.bottega.ecommerce.sales.domain.offer.OfferItem
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product
import pl.com.bottega.ecommerce.sharedkernel.Money

/**
 * Reservation is just a "wish list". System can not guarantee that user can buy desired products.
 * Reservation generates Offer VO, that is calculated based on current prices and current avability.
 *
 * @author Slawek
 */

@InvariantsList("closed: closed reservation cano not be modified", "duplicates: can not add already added product, increase quantity instead")
@Entity
@AggregateRoot
class Reservation : BaseAggregateRoot {

    @Enumerated(EnumType.STRING)
    lateinit var status: ReservationStatus
        private set

    @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "reservation")
    @Fetch(FetchMode.JOIN)
    lateinit var items: MutableList<ReservationItem>
        private set

    @Embedded
    lateinit var clientData: ClientData
        private set

    lateinit var createDate: Date
        private set

    val isClosed: Boolean
        get() = status == ReservationStatus.CLOSED

    val reservedProducts: List<ReservedProduct>
        get() {
            val result = ArrayList<ReservedProduct>(items.size)

            items.mapTo(result) { ReservedProduct(it.product.aggregateId, it.product.name, it.quantity, calculateItemCost(it)) }

            return result
        }

    enum class ReservationStatus {
        OPENED, CLOSED
    }

    private constructor() {}

    internal constructor(aggregateId: AggregateId, status: ReservationStatus, clientData: ClientData, createDate: Date) {
        this.aggregateId = aggregateId
        this.status = status
        this.clientData = clientData
        this.createDate = createDate
        this.items = ArrayList()
    }

    @Invariant("closed", "duplicates")
    fun add(product: Product, quantity: Int) {
        if (isClosed)
            domainError("Reservation already closed")
        if (!product.isAvailable)
            domainError("Product is no longer available")

        if (contains(product)) {
            increase(product, quantity)
        } else {
            addNew(product, quantity)
        }
    }

    /**
     * Sample function closured by policy
     * Higher order function closured by policy function
     *
     * Function loads current prices, and prepares offer according to the current availability and given discount
     * @param discountPolicy
     * @return
     */
    @Function
    fun calculateOffer(discountPolicy: DiscountPolicy): Offer {
        val availableItems = ArrayList<OfferItem>()
        val unavailableItems = ArrayList<OfferItem>()

        for (item in items) {
            if (item.product.isAvailable) {
                val discount = discountPolicy.applyDiscount(item.product, item.quantity, item.product.price)
                val offerItem = OfferItem(item.product.generateSnapshot(), item.quantity, discount)

                availableItems.add(offerItem)
            } else {
                val offerItem = OfferItem(item.product.generateSnapshot(), item.quantity)

                unavailableItems.add(offerItem)
            }
        }

        return Offer(availableItems, unavailableItems)
    }

    private fun addNew(product: Product, quantity: Int) {
        val item = ReservationItem(product, quantity)
        items.add(item)
    }

    private fun increase(product: Product, quantity: Int) {
        for (item in items) {
            if (item.product == product) {
                item.changeQuantityBy(quantity)
                break
            }
        }
    }

    operator fun contains(product: Product): Boolean {
        for (item in items) {
            if (item.product == product)
                return true
        }
        return false
    }

    @Invariant("closed")
    fun close() {
        if (isClosed)
            domainError("Reservation is already closed")
        status = ReservationStatus.CLOSED
    }

    private fun calculateItemCost(item: ReservationItem): Money {
        return item.product.price.multiplyBy(item.quantity.toDouble())
    }
}
