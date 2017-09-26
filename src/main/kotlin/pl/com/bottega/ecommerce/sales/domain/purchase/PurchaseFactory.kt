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

import java.util.ArrayList
import java.util.Date

import javax.inject.Inject

import org.springframework.beans.factory.config.AutowireCapableBeanFactory

import pl.com.bottega.ddd.annotations.domain.DomainFactory
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId
import pl.com.bottega.ecommerce.sales.domain.client.Client
import pl.com.bottega.ecommerce.sales.domain.offer.Offer
import pl.com.bottega.ecommerce.sales.domain.offer.OfferItem
import pl.com.bottega.ecommerce.sharedkernel.Money
import pl.com.bottega.ecommerce.sharedkernel.exceptions.DomainOperationException

@DomainFactory
class PurchaseFactory {

    @Inject
    private val spring: AutowireCapableBeanFactory? = null

    /**
     *
     * @param orderId correlation id - correlates purchases and reservations
     * @param client
     * @param offer
     * @return
     */
    fun create(orderId: AggregateId, client: Client, offer: Offer): Purchase {
        if (!canPurchase(client, offer.availableItems))
            throw DomainOperationException(client.aggregateId, "client can not purchase")

        val items = ArrayList<PurchaseItem>(offer.availableItems!!.size)
        var purchaseTotlCost = Money.ZERO

        for (item in offer.availableItems!!) {
            val purchaseItem = PurchaseItem(item.productData, item.quantity, item.totalCost)
            items.add(purchaseItem)
            purchaseTotlCost = purchaseTotlCost.add(purchaseItem.totalCost)
        }

        val purchase = Purchase(orderId, client.generateSnapshot(),
                items, Date(), false, purchaseTotlCost)

        spring!!.autowireBean(purchase)

        return purchase
    }

    private fun canPurchase(client: Client, availabeItems: List<OfferItem>?): Boolean {
        return true//TODO explore domain rules
    }
}
