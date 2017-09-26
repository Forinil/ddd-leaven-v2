package pl.com.bottega.ecommerce.sharedkernel.specification

import java.util.Arrays

/**
 *
 * @author Slawek
 *
 * @param <T>
</T> */
abstract class CompositeSpecification<T> : Specification<T> {

    override fun and(other: Specification<T>): Specification<T> {
        return AndSpecification(this, other)
    }

    override fun or(other: Specification<T>): Specification<T> {
        return OrSpecification(this, other)
    }

    override fun not(): Specification<T> {
        return NotSpecification(this)
    }

    override fun conjunction(vararg others: Specification<T>): Specification<T> {
        val list = Arrays.asList(*others)
        list.add(this)
        return Conjunction(list)
    }
}
