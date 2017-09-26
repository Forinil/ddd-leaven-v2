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
package pl.com.bottega.ecommerce.sales.domain.invoicing.tax

import pl.com.bottega.ddd.annotations.domain.DomainPolicyImpl
import pl.com.bottega.ecommerce.sales.domain.invoicing.Tax
import pl.com.bottega.ecommerce.sales.domain.invoicing.TaxPolicy
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType
import pl.com.bottega.ecommerce.sharedkernel.Money

/**
 * Sample Policy impl<br></br>
 *
 * @author Slawek
 */
@DomainPolicyImpl
class CrysisTaxPolicy(private val ratio: Double) : TaxPolicy {

    override fun calculateTax(productType: ProductType, net: Money): Tax {
        val desc = "sorry"
        val tax = net.multiplyBy(ratio)
        return Tax(tax, desc)
    }

}
