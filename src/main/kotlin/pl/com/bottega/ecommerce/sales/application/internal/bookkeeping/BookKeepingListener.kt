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
package pl.com.bottega.ecommerce.sales.application.internal.bookkeeping

import javax.inject.Inject

import pl.com.bottega.ddd.annotations.event.EventListener
import pl.com.bottega.ddd.annotations.event.EventListeners
import pl.com.bottega.ecommerce.canonicalmodel.events.OrderSubmittedEvent
import pl.com.bottega.ecommerce.sales.domain.client.Client
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository
import pl.com.bottega.ecommerce.sales.domain.invoicing.BookKeeper
import pl.com.bottega.ecommerce.sales.domain.invoicing.Invoice
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceRepository
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceRequest
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceRequestFactory
import pl.com.bottega.ecommerce.sales.domain.invoicing.TaxAdvisor
import pl.com.bottega.ecommerce.sales.domain.purchase.Purchase
import pl.com.bottega.ecommerce.sales.domain.purchase.PurchaseRepository

@EventListeners
class BookKeepingListener {

    @Inject
    private val bookKeeper: BookKeeper? = null

    @Inject
    private val purchaseRepository: PurchaseRepository? = null

    @Inject
    private val invoiceRepository: InvoiceRepository? = null

    @Inject
    private val taxAdvisor: TaxAdvisor? = null

    @Inject
    private val clientRepository: ClientRepository? = null

    @Inject
    private val invoiceRequestFactory: InvoiceRequestFactory? = null

    @EventListener
    fun handle(event: OrderSubmittedEvent) {
        val purchase = purchaseRepository!!.load(event.orderId)

        val client = clientRepository!!.load(purchase.clientData.aggregateId)
        val request = invoiceRequestFactory!!.create(client, purchase)
        val invoice = bookKeeper!!.issuance(request, taxAdvisor!!.suggestBestTax(client))

        invoiceRepository!!.save(invoice)
    }
}
