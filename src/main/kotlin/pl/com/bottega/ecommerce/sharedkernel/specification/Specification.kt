package pl.com.bottega.ecommerce.sharedkernel.specification

/**
 *
 * @author Slawek
 *
 * @param <T>
</T> */
interface Specification<T> {
    fun isSatisfiedBy(candidate: T): Boolean

    fun and(other: Specification<T>): Specification<T>

    fun or(other: Specification<T>): Specification<T>

    fun conjunction(vararg others: Specification<T>): Specification<T>

    operator fun not(): Specification<T>
}
