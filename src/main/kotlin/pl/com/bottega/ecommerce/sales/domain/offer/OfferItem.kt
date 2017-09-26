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

import pl.com.bottega.ddd.annotations.domain.ValueObject
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData
import pl.com.bottega.ecommerce.sharedkernel.Money

@ValueObject
class OfferItem @JvmOverloads constructor(val productData: ProductData, val quantity: Int, val discount: Discount? = null) {

    var totalCost: Money

    init {

        var discountValue = Money.ZERO
        if (discount != null)
            discountValue = discountValue.subtract(discount.value!!)

        this.totalCost = productData.price.multiplyBy(quantity.toDouble()).subtract(discountValue)
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (discount?.hashCode() ?: 0)
        result = prime * result + (productData.hashCode())
        result = prime * result + quantity
        result = prime * result + (totalCost.hashCode())
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val another = other as OfferItem?
        if (discount == null) {
            if (another!!.discount != null)
                return false
        } else if (discount != another!!.discount)
            return false
        if (productData != another.productData)
            return false
        if (quantity != another.quantity)
            return false
        if (totalCost != another.totalCost)
            return false
        return true
    }

    /**
     *
     * @param item
     * @param delta acceptable percentage difference
     * @return
     */
    fun sameAs(item: OfferItem, delta: Double): Boolean {
        if (productData != item.productData)
            return false

        if (quantity != item.quantity)
            return false


        val max: Money?
        val min: Money?
        if (totalCost.greaterThan(item.totalCost)) {
            max = totalCost
            min = item.totalCost
        } else {
            max = item.totalCost
            min = totalCost
        }

        val difference = max.subtract(min)
        val acceptableDelta = max.multiplyBy(delta / 100)

        return acceptableDelta.greaterThan(difference)
    }


}
