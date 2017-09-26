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
package pl.com.bottega.ecommerce.sales.domain.equivalent

import pl.com.bottega.ddd.annotations.domain.DomainFactory
import pl.com.bottega.ecommerce.sales.domain.client.Client
import pl.com.bottega.ecommerce.sales.domain.equivalent.specification.SameCategory
import pl.com.bottega.ecommerce.sales.domain.equivalent.specification.SimilarName
import pl.com.bottega.ecommerce.sales.domain.equivalent.specification.SimilarPrice
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product
import pl.com.bottega.ecommerce.sharedkernel.Money
import pl.com.bottega.ecommerce.sharedkernel.specification.DisjunctionSpecification
import pl.com.bottega.ecommerce.sharedkernel.specification.Specification

@DomainFactory
class ProductSpecificationFactory {

    fun create(client: Client,
               problematicProduct: Product): Specification<Product> {
        // TODO explore domain rules, maybe use genetic algorithm to breed spec;)
        return DisjunctionSpecification(
                SimilarPrice(problematicProduct.price, generateAcceptableDifference(client)),
                SimilarName(problematicProduct.name),
                SameCategory(problematicProduct.productType))
    }

    private fun generateAcceptableDifference(client: Client): Money {
        // TODO explore rules
        return Money(7.0)
    }

}
