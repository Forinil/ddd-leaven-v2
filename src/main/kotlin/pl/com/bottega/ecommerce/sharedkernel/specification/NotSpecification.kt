package pl.com.bottega.ecommerce.sharedkernel.specification

/**
 *
 * @author Slawek
 *
 * @param <T>
</T> */
class NotSpecification<T>(private val wrapped: Specification<T>) : CompositeSpecification<T>() {

    override fun isSatisfiedBy(candidate: T): Boolean {
        return !wrapped.isSatisfiedBy(candidate)
    }
}
