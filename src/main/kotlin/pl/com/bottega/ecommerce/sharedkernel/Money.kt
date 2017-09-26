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
package pl.com.bottega.ecommerce.sharedkernel

import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency

import javax.persistence.Embeddable

import org.fest.util.Objects

import pl.com.bottega.ddd.annotations.domain.ValueObject

/**
 *
 */
@Embeddable
@ValueObject
open class Money : Serializable {

    private lateinit var denomination: BigDecimal

    lateinit var currencyCode: String
        private set

    val currency: Currency
        get() = Currency.getInstance(currencyCode)

    protected constructor()

    @JvmOverloads constructor(denomination: BigDecimal, currency: Currency = DEFAULT_CURRENCY) : this(denomination, currency.currencyCode) {}

    private constructor(denomination: BigDecimal, currencyCode: String) {
        this.denomination = denomination.setScale(2, RoundingMode.HALF_EVEN)
        this.currencyCode = currencyCode
    }

    @JvmOverloads constructor(denomination: Double, currency: Currency = DEFAULT_CURRENCY) : this(BigDecimal(denomination), currency.currencyCode) {}

    constructor(denomination: Double, currencyCode: String) : this(BigDecimal(denomination), currencyCode) {}

    fun multiplyBy(multiplier: Double): Money {
        return multiplyBy(BigDecimal(multiplier))
    }

    fun multiplyBy(multiplier: BigDecimal): Money {
        return Money(denomination.multiply(multiplier), currencyCode)
    }

    fun add(money: Money): Money {
        if (!compatibleCurrency(money)) {
            throw IllegalArgumentException("Currency mismatch")
        }

        return Money(denomination.add(money.denomination), determineCurrencyCode(money))
    }

    fun subtract(money: Money): Money {
        if (!compatibleCurrency(money))
            throw IllegalArgumentException("Currency mismatch")

        return Money(denomination.subtract(money.denomination), determineCurrencyCode(money))
    }

    /**
     * Currency is compatible if the same or either money object has zero value.
     */
    private fun compatibleCurrency(money: Money): Boolean {
        return isZero(denomination) || isZero(money.denomination) || currencyCode == money.currencyCode
    }

    private fun isZero(testedValue: BigDecimal?): Boolean {
        return BigDecimal.ZERO.compareTo(testedValue!!) == 0
    }

    /**
     * @return currency from this object or otherCurrencyCode. Preferred is the
     * one that comes from Money that has non-zero value.
     */
    private fun determineCurrencyCode(otherMoney: Money): Currency {
        val resultingCurrenctCode = if (isZero(denomination)) otherMoney.currencyCode else currencyCode
        return Currency.getInstance(resultingCurrenctCode)
    }

    fun greaterThan(other: Money): Boolean {
        return denomination.compareTo(other.denomination) > 0
    }

    fun lessThan(other: Money): Boolean {
        return denomination.compareTo(other.denomination) < 0
    }

    fun lessOrEquals(other: Money): Boolean {
        return denomination.compareTo(other.denomination) <= 0
    }

    override fun toString(): String {
        return String.format("%0$.2f %s", denomination, currency.symbol)
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (currencyCode.hashCode())
        result = prime * result + (denomination.hashCode())
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val another = other as Money?
        return compatibleCurrency(another!!) && Objects.areEqual(denomination, another.denomination)
    }

    companion object {

        val DEFAULT_CURRENCY: Currency = Currency.getInstance("EUR")

        val ZERO = Money(BigDecimal.ZERO)
    }

}
