package pl.com.bottega.ecommerce.sales.readmodel.offer

import pl.com.bottega.ddd.annotations.application.Finder

@Finder
interface Offer {

    fun find(query: OfferQuery): List<OfferedProductDto>
}
