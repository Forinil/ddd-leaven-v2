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

import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.EnumType
import javax.persistence.Enumerated

import pl.com.bottega.ddd.annotations.domain.ValueObject
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId
import pl.com.bottega.ecommerce.sharedkernel.Money

@Embeddable
@ValueObject
class ProductData {

    @Embedded
    lateinit var productId: AggregateId
        private set

    @Embedded
    @AttributeOverrides(AttributeOverride(name = "denomination", column = Column(name = "productPrice_denomination")), AttributeOverride(name = "currencyCode", column = Column(name = "productPrice_currencyCode")))
    lateinit var price: Money
        private set

    lateinit var name: String
        private set

    lateinit var snapshotDate: Date
        private set

    @Enumerated(EnumType.STRING)
    lateinit var type: ProductType
        private set


    private constructor()

    internal constructor(productId: AggregateId, price: Money, name: String, type: ProductType,
                         snapshotDate: Date) {
        this.productId = productId
        this.price = price
        this.name = name
        this.snapshotDate = snapshotDate
        this.type = type
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (name.hashCode())
        result = prime * result + (price.hashCode())
        result = prime * result + (productId.hashCode())
        result = prime * result + (type.hashCode())
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val another = other as ProductData?
        if (name != another!!.name)
            return false
        if (price != another.price)
            return false
        if (productId != another.productId)
            return false
        return type == another.type
    }


}
