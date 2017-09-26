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
/**
 *
 */
package pl.com.bottega.ecommerce.sales.domain.invoicing

import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity

import pl.com.bottega.ddd.support.domain.BaseEntity
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData
import pl.com.bottega.ecommerce.sharedkernel.Money

/**
 * @author Slawek
 */
@Entity
class InvoiceLine : BaseEntity {

    @Embedded
    lateinit var product: ProductData
        private set

    var quantity: Int = 0
        private set

    @Embedded
    @AttributeOverrides(AttributeOverride(name = "denomination", column = Column(name = "net_denomination")), AttributeOverride(name = "currencyCode", column = Column(name = "net_currencyCode")))
    lateinit var net: Money
        private set

    @Embedded
    @AttributeOverrides(AttributeOverride(name = "denomination", column = Column(name = "gros_denomination")), AttributeOverride(name = "currencyCode", column = Column(name = "gros_currencyCode")))
    lateinit var gros: Money
        private set

    @Embedded
    lateinit var tax: Tax
        private set

    /**
     * JPA only
     */
    constructor()


    internal constructor(product: ProductData, quantity: Int, net: Money, tax: Tax) {
        this.product = product
        this.quantity = quantity
        this.net = net
        this.tax = tax

        this.gros = net.add(tax.amount)
    }
}
