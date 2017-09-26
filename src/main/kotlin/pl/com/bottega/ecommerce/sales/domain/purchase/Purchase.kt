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
package pl.com.bottega.ecommerce.sales.domain.purchase

import java.util.Collections
import java.util.Date

import javax.persistence.CascadeType
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OrderColumn

import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

import pl.com.bottega.ddd.annotations.domain.AggregateRoot
import pl.com.bottega.ddd.support.domain.BaseAggregateRoot
import pl.com.bottega.ecommerce.canonicalmodel.events.OrderSubmittedEvent
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData
import pl.com.bottega.ecommerce.sharedkernel.Money

/**
 * Models fact of purchase.
 *
 * @author Slawek
 */
@Entity
@AggregateRoot
class Purchase : BaseAggregateRoot {

    @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(FetchMode.JOIN)
    @OrderColumn(name = "itemNumber")
    @JoinColumn(name = "purchase_id")
    private lateinit var items: List<PurchaseItem>

    var isPaid: Boolean = false
        private set

    @Embedded
    lateinit var clientData: ClientData
        private set

    lateinit var purchaseDate: Date
        private set

    @Embedded
    lateinit var totalCost: Money
        private set


    private constructor()

    internal constructor(aggregateId: AggregateId, clientData: ClientData, items: List<PurchaseItem>, purchaseDate: Date,
                         paid: Boolean, totalCost: Money) {
        this.aggregateId = aggregateId
        this.clientData = clientData
        this.items = items
        this.purchaseDate = purchaseDate
        this.isPaid = paid
        this.totalCost = totalCost
    }

    fun confirm() {
        isPaid = true
        eventPublisher!!.publish(OrderSubmittedEvent(aggregateId))
    }

    fun getItems(): Collection<PurchaseItem> {
        return Collections.unmodifiableCollection(items) as Collection<PurchaseItem>
    }

}
