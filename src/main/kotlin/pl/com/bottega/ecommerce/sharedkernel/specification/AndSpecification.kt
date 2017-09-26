package pl.com.bottega.ecommerce.sharedkernel.specification

/**
 *
 * @author Slawek
 *
 * @param <T>
</T> */
class AndSpecification<T>(private val a: Specification<T>, private val b: Specification<T>) : CompositeSpecification<T>() {

    override fun isSatisfiedBy(candidate: T): Boolean {
        return a.isSatisfiedBy(candidate) && b.isSatisfiedBy(candidate)
    }
}
