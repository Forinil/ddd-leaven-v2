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

import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity

import pl.com.bottega.ddd.annotations.domain.ValueObject
import pl.com.bottega.ddd.support.domain.BaseEntity
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData
import pl.com.bottega.ecommerce.sharedkernel.Money

/**
 * Models purchased items - contains copied data in case on catalog proces and discount change
 * @author Slawek
 */
@ValueObject
@Entity
class PurchaseItem : BaseEntity {

    @Embedded
    lateinit var productData: ProductData
        private set

    var quantity: Int = 0
        private set

    @AttributeOverrides(AttributeOverride(name = "denomination", column = Column(name = "purchaseTotalCost_denomination")), AttributeOverride(name = "currencyCode", column = Column(name = "purchaseTotalCost_currencyCode")))
    lateinit var totalCost: Money
        private set

    private constructor()

    constructor(productData: ProductData, quantity: Int, totalCost: Money) {
        this.productData = productData
        this.quantity = quantity
        this.totalCost = totalCost
    }


}
