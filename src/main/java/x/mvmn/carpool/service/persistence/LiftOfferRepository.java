package x.mvmn.carpool.service.persistence;

import org.springframework.data.repository.CrudRepository;

import x.mvmn.carpool.model.LiftOffer;

public interface LiftOfferRepository extends CrudRepository<LiftOffer, Integer> {

}
