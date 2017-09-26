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
package pl.com.bottega.ecommerce.sales.domain.offer

import java.util.ArrayList

import pl.com.bottega.ddd.annotations.domain.ValueObject
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId

/**
 * Offer that is available per client (including availability and discounts)
 *
 * @author Slawek
 */
@ValueObject
class Offer(availabeItems: List<OfferItem>, unavailableItems: List<OfferItem>) {

    var availableItems: List<OfferItem>? = ArrayList()
        private set

    var unavailableItems: List<OfferItem> = ArrayList()
        private set

    init {
        this.availableItems = availabeItems
        this.unavailableItems = unavailableItems
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (availableItems?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val another = other as Offer?
        if (availableItems == null) {
            if (another!!.availableItems != null)
                return false
        } else if (availableItems != another!!.availableItems)
            return false
        return true
    }

    /**
     *
     * @param seenOffer
     * @param delta acceptable difference in percent
     * @return
     */
    fun sameAs(seenOffer: Offer, delta: Double): Boolean {
        if (availableItems!!.size != seenOffer.availableItems!!.size)
            return false

        for (item in availableItems!!) {
            val sameItem = seenOffer.findItem(item.productData.productId) ?: return false
            if (!sameItem.sameAs(item, delta))
                return false
        }

        return true
    }

    private fun findItem(productId: AggregateId): OfferItem? {
        return availableItems!!.firstOrNull { it.productData.productId == productId }
    }


}
