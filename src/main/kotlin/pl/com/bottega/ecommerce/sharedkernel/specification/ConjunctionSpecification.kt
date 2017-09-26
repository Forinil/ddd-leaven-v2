package pl.com.bottega.ecommerce.sharedkernel.specification

/**
 *
 * @author Slawek
 *
 * @param <T>
</T> */
class ConjunctionSpecification<T>(private vararg val conjunction: Specification<T>) : CompositeSpecification<T>() {

    override fun isSatisfiedBy(candidate: T): Boolean {
        return conjunction.any { it.isSatisfiedBy(candidate) }
    }
}
