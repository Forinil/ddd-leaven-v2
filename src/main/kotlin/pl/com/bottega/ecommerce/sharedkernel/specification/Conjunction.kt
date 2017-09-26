package pl.com.bottega.ecommerce.sharedkernel.specification

class Conjunction<T>(private val list: List<Specification<T>>) : CompositeSpecification<T>() {

    override fun isSatisfiedBy(candidate: T): Boolean {
        return list.any { it.isSatisfiedBy(candidate) }
    }

}
