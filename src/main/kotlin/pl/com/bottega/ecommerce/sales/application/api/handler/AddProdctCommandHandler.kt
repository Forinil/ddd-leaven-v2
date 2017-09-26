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
package pl.com.bottega.ecommerce.sales.application.api.handler

import javax.inject.Inject

import pl.com.bottega.cqrs.annotations.CommandHandlerAnnotation
import pl.com.bottega.cqrs.command.handler.CommandHandler
import pl.com.bottega.ecommerce.sales.application.api.command.AddProdctCommand
import pl.com.bottega.ecommerce.sales.domain.client.Client
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository
import pl.com.bottega.ecommerce.system.application.SystemContext

@CommandHandlerAnnotation
class AddProdctCommandHandler : CommandHandler<AddProdctCommand, Unit> {

    @Inject
    private val reservationRepository: ReservationRepository? = null

    @Inject
    private val productRepository: ProductRepository? = null

    @Inject
    private val suggestionService: SuggestionService? = null

    @Inject
    private val clientRepository: ClientRepository? = null

    @Inject
    private val systemContext: SystemContext? = null

    override fun handle(command: AddProdctCommand) {
        val reservation = reservationRepository!!.load(command.orderId)

        var product: Product? = productRepository!!.load(command.productId)

        if (!product!!.isAvailable) {
            val client = loadClient()
            product = suggestionService!!.suggestEquivalent(product, client)
        }

        reservation.add(product!!, command.quantity)

        reservationRepository.save(reservation)
    }

    private fun loadClient(): Client {
        return clientRepository!!.load(systemContext!!.systemUser.clientId!!)
    }

}
