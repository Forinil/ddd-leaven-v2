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
package pl.com.bottega.ecommerce.sales.domain.offer

import pl.com.bottega.ddd.annotations.domain.ValueObject
import pl.com.bottega.ecommerce.sharedkernel.Money

@ValueObject
class Discount(val cause: String?, val value: Money?) {

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (cause?.hashCode() ?: 0)
        result = prime * result + (value?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val another = other as Discount?
        if (cause == null) {
            if (another!!.cause != null)
                return false
        } else if (cause != another!!.cause)
            return false
        if (value == null) {
            if (another.value != null)
                return false
        } else if (value != another.value)
            return false
        return true
    }


}
