package x.mvmn.carpool.service.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import x.mvmn.carpool.model.LiftOffer;

public interface LiftOfferRepository extends JpaRepository<LiftOffer, Integer> {

}
