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
package pl.com.bottega.ecommerce.sales.domain.offer.discounts

import pl.com.bottega.ecommerce.sales.domain.offer.Discount
import pl.com.bottega.ecommerce.sales.domain.offer.DiscountPolicy
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product
import pl.com.bottega.ecommerce.sharedkernel.Money

class QuantityDiscount
/**
 *
 * @param rebate value of the rebate in %
 * @param minimalQuantity minimal quantity of the purchase that allows rebate
 */
(rebate: Double, private val minimalQuantity: Int) : DiscountPolicy {
    private val rebateRatio: Double = rebate / 100

    override fun applyDiscount(product: Product, quantity: Int, regularCost: Money): Discount? {
        return if (quantity >= minimalQuantity) Discount("over: " + quantity, regularCost.multiplyBy(rebateRatio)) else null
    }
}
