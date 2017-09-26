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
package pl.com.bottega.ecommerce.sales.domain.reservation

import java.util.Date

import javax.inject.Inject

import org.springframework.beans.factory.config.AutowireCapableBeanFactory

import pl.com.bottega.ddd.annotations.domain.DomainFactory
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.AggregateId
import pl.com.bottega.ecommerce.sales.domain.client.Client
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation.ReservationStatus
import pl.com.bottega.ecommerce.sharedkernel.exceptions.DomainOperationException

@DomainFactory
class ReservationFactory {

    @Inject
    private val spring: AutowireCapableBeanFactory? = null

    fun create(client: Client): Reservation {
        if (!canReserve(client))
            throw DomainOperationException(client.aggregateId, "Client can not create reservations")

        val reservation = Reservation(AggregateId.generate(), ReservationStatus.OPENED, client.generateSnapshot(), Date())
        spring!!.autowireBean(reservation)

        addGratis(reservation, client)

        return reservation
    }

    private fun addGratis(reservation: Reservation, client: Client) {
        //TODO explore domain rules
    }

    private fun canReserve(client: Client): Boolean {
        return true//TODO explore domain rules (ex: cleint's debts, stataus etc)
    }

}
