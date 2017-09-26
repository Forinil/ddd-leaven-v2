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
package pl.com.bottega.ecommerce.sales.domain.productscatalog

import java.util.Date

import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

import pl.com.bottega.ddd.annotations.domain.AggregateRoot
import pl.com.bottega.ddd.support.domain.BaseAggregateRoot
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId
import pl.com.bottega.ecommerce.sharedkernel.Money

@Entity
@AggregateRoot
class Product : BaseAggregateRoot {

    @Embedded
    lateinit var price: Money
        private set

    lateinit var name: String
        private set

    @Enumerated(EnumType.STRING)
    lateinit var productType: ProductType
        private set

    //TODO explore domain rules
    val isAvailable: Boolean
        get() = !isRemoved

    private constructor()

    internal constructor(aggregateId: AggregateId, price: Money, name: String, productType: ProductType) {
        this.aggregateId = aggregateId
        this.price = price
        this.name = name
        this.productType = productType
    }

    fun generateSnapshot(): ProductData {
        return ProductData(aggregateId, price, name, productType, Date())
    }

}
