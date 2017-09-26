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

import javax.inject.Inject

import pl.com.bottega.ddd.annotations.domain.DomainService
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository
import pl.com.bottega.ecommerce.sharedkernel.Money

/**
 * Sample Domain Service that contains logic that:
 *
 *  *  Does not have a natural place in any aggregate - we don't want to bloat Order with issuance of the Invoice
 *  *  Has broad dependencies - we don't want Order to become a 'God Class'
 *  *  Is used only (or not many) in one Use Case/user Story so is not essential for any Aggregate
 *
 *
 * Notice that this Domain Service is managed by Container in order to be able to inject dependencies like Repo
 *
 * @author Slawek
 */
@DomainService
class BookKeeper {

    @Inject
    private val productRepository: ProductRepository? = null

    @Inject
    private val invoiceFactory: InvoiceFactory? = null

    fun issuance(invoiceRequest: InvoiceRequest, taxPolicy: TaxPolicy): Invoice {
        val invoice = invoiceFactory!!.create(invoiceRequest.getClientData())

        for (item in invoiceRequest.getItems()) {
            val net = item.totalCost
            val tax = taxPolicy.calculateTax(item.productData.type, net)

            val invoiceLine = InvoiceLine(item.productData, item.quantity, net, tax)
            invoice.addItem(invoiceLine)
        }

        return invoice
    }

}
