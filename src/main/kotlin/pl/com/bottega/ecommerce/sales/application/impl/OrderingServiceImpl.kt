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
package pl.com.bottega.ecommerce.sales.application.impl

import javax.inject.Inject

import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

import pl.com.bottega.ddd.annotations.application.ApplicationService
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId
import pl.com.bottega.ecommerce.sales.application.api.command.OrderDetailsCommand
import pl.com.bottega.ecommerce.sales.application.api.service.OfferChangedException
import pl.com.bottega.ecommerce.sales.application.api.service.OrderingService
import pl.com.bottega.ecommerce.sales.domain.client.Client
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService
import pl.com.bottega.ecommerce.sales.domain.offer.DiscountFactory
import pl.com.bottega.ecommerce.sales.domain.offer.Offer
import pl.com.bottega.ecommerce.sales.domain.payment.PaymentRepository
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository
import pl.com.bottega.ecommerce.sales.domain.purchase.PurchaseFactory
import pl.com.bottega.ecommerce.sales.domain.purchase.PurchaseRepository
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationFactory
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository
import pl.com.bottega.ecommerce.sharedkernel.exceptions.DomainOperationException
import pl.com.bottega.ecommerce.system.application.SystemContext

/**
 * Ordering Use Case steps<br></br>
 * Each step is a Domain Story<br></br>
 * <br></br>
 * Notice that application language is different (simpler) than domain language, ex: we don'nt want to exposure domain concepts like Purchase and Reservation to the upper layers, we hide them under the Order term
 * <br></br>
 * Technically App Service is just a bunch of procedures, therefore OO principles (ex: CqS, SOLID, GRASP) does not apply here
 *
 * @author Slawek
 */
@ApplicationService
class OrderingServiceImpl : OrderingService {

    @Inject
    private val systemContext: SystemContext? = null

    @Inject
    private val clientRepository: ClientRepository? = null

    @Inject
    private val reservationRepository: ReservationRepository? = null

    @Inject
    private val reservationFactory: ReservationFactory? = null

    @Inject
    private val purchaseFactory: PurchaseFactory? = null

    @Inject
    private val purchaseRepository: PurchaseRepository? = null

    @Inject
    private val productRepository: ProductRepository? = null

    @Inject
    private val paymentRepository: PaymentRepository? = null

    @Inject
    private val discountFactory: DiscountFactory? = null

    @Inject
    private val suggestionService: SuggestionService? = null

    // @Secured requires BUYER role
    override fun createOrder(): AggregateId? {
        val reservation = reservationFactory!!.create(loadClient())
        reservationRepository!!.save(reservation)
        return reservation.aggregateId
    }

    /**
     * DOMAIN STORY<br></br>
     * try to read this as a full sentence, this way: subject.predicate(completion)<br></br>
     * <br></br>
     * Load reservation by orderId<br></br>
     * Load product by productId<br></br>
     * Check if product is not available<br></br>
     * -if so, than suggest equivalent for that product based on client<br></br>
     * Reservation add product by given quantity
     */
    override fun addProduct(orderId: AggregateId, productId: AggregateId,
                            quantity: Int) {
        val reservation = reservationRepository!!.load(orderId)

        var product: Product? = productRepository!!.load(productId)

        if (!product!!.isAvailable) {
            val client = loadClient()
            product = suggestionService!!.suggestEquivalent(product, client)
        }

        reservation.add(product!!, quantity)

        reservationRepository.save(reservation)
    }

    /**
     * Can be invoked many times for the same order (with different params).<br></br>
     * Offer VO is not stored in the Repo, it is stored on the Client Tier instead.
     */
    override fun calculateOffer(orderId: AggregateId): Offer {
        val reservation = reservationRepository!!.load(orderId)

        val discountPolicy = discountFactory!!.create(loadClient())

        /*
		 * Sample pattern: Aggregate generates Value Object using function<br>
		 * Higher order function is closured by policy
		 */
        return reservation.calculateOffer(discountPolicy)
    }

    /**
     * DOMAIN STORY<br></br>
     * try to read this as a full sentence, this way: subject.predicate(completion)<br></br>
     * <br></br>
     * Load reservation by orderId<br></br>
     * Check if reservation is closed - if so, than Error<br></br>
     * Generate new offer from reservation using discount created per client<br></br>
     * Check if new offer is not the same as seen offer using delta = 5<br></br>
     * Create purchase per client based on seen offer<br></br>
     * Check if client can not afford total cost of purchase - if so, than Error<br></br>
     * Confirm purchase<br></br>
     * Close reservation<br></br>
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)//highest isolation needed because of manipulating many Aggregates
    @Throws(OfferChangedException::class)
    override fun confirm(orderId: AggregateId, orderDetailsCommand: OrderDetailsCommand, seenOffer: Offer) {
        val reservation = reservationRepository!!.load(orderId)
        if (reservation.isClosed)
            throw DomainOperationException(reservation.aggregateId, "reservation is already closed")

        /*
		 * Sample pattern: Aggregate generates Value Object using function<br>
		 * Higher order function is closured by policy
		 */
        val newOffer = reservation.calculateOffer(
                discountFactory!!.create(loadClient()))

        /*
		 * Sample pattern: Client Tier sends back old VOs, Server generates new VOs based on Aggregate state<br>
		 * Notice that this VO is not stored in Repo, it's stored on the Client Tier.
		 */
        if (!newOffer.sameAs(seenOffer, 5.0))
        //TODO load delta from conf.
            throw OfferChangedException(reservation.aggregateId!!, seenOffer, newOffer)

        val client = loadClient()//create per logged client, not reservation owner
        val purchase = purchaseFactory!!.create(reservation.aggregateId, client, seenOffer)

        if (!client.canAfford(purchase.totalCost))
            throw DomainOperationException(client.aggregateId, "client has insufficient money")

        purchaseRepository!!.save(purchase)//Aggregate must be managed by persistence context before firing events (synchronous listeners may need to load it)

        /*
		 * Sample model where one aggregate creates another. Client does not manage payment lifecycle, therefore application must manage it.
		 */
        val payment = client.charge(purchase.totalCost)
        paymentRepository!!.save(payment)

        purchase.confirm()
        reservation.close()

        reservationRepository.save(reservation)
        clientRepository!!.save(client)

    }

    private fun loadClient(): Client {
        return clientRepository!!.load(systemContext!!.systemUser.clientId!!)
    }
}
