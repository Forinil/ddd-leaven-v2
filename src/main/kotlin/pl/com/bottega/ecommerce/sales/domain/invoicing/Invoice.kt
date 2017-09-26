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
package pl.com.bottega.ecommerce.sales.domain.invoicing

import java.util.ArrayList
import java.util.Collections

import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

import pl.com.bottega.ddd.annotations.domain.AggregateRoot
import pl.com.bottega.ddd.support.domain.BaseAggregateRoot
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData
import pl.com.bottega.ecommerce.sharedkernel.Money

/**
 *
 * @author Slawek
 */
@AggregateRoot
@Entity
class Invoice : BaseAggregateRoot {

    @Embedded
    lateinit var client: ClientData
        private set

    @Embedded
    @AttributeOverrides(AttributeOverride(name = "denomination", column = Column(name = "net_denomination")), AttributeOverride(name = "currencyCode", column = Column(name = "net_currencyCode")))
    lateinit var net: Money
        private set

    @Embedded
    @AttributeOverrides(AttributeOverride(name = "denomination", column = Column(name = "gros_denomination")), AttributeOverride(name = "currencyCode", column = Column(name = "gros_currencyCode")))
    lateinit var gros: Money
        private set

    @OneToMany(cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "invoiceId")
    @Fetch(FetchMode.JOIN)
    private lateinit var items: MutableList<InvoiceLine>

    internal constructor(invoiceId: AggregateId, client: ClientData) {
        this.aggregateId = invoiceId
        this.client = client
        this.items = ArrayList()

        this.net = Money.ZERO
        this.gros = Money.ZERO
    }

    /**
     * For JPA Only
     */
    private constructor()

    fun addItem(item: InvoiceLine) {
        items.add(item)

        net = net.add(item.net)
        gros = gros.add(item.gros)
    }

    /**
     *
     * @return immutable projection
     */
    fun getItems(): List<InvoiceLine> {
        return Collections.unmodifiableList(items)
    }

}
