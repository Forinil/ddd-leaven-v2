package pl.com.bottega.ecommerce.sharedkernel.specification

/**
 *
 * @author Slawek
 *
 * @param <T>
</T> */
class DisjunctionSpecification<T>(private vararg val disjunction: Specification<T>) : CompositeSpecification<T>() {

    override fun isSatisfiedBy(candidate: T): Boolean {
        return disjunction.any { it.isSatisfiedBy(candidate) }
    }
}
