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

import javax.persistence.Entity
import javax.persistence.ManyToOne

import pl.com.bottega.ddd.support.domain.BaseEntity
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product
import pl.com.bottega.ecommerce.sharedkernel.exceptions.DomainOperationException

@Entity
class ReservationItem : BaseEntity {

    @ManyToOne
    lateinit var product: Product
        private set

    var quantity: Int = 0
        private set

    private constructor()

    constructor(product: Product, quantity: Int) {
        this.product = product
        this.quantity = quantity
    }

    fun changeQuantityBy(change: Int) {
        val changed = quantity + change
        if (changed <= 0)
            throw DomainOperationException(null, "change below 1")
        this.quantity = changed
    }


}
