package x.mvmn.carpool.service.persistence;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import x.mvmn.carpool.model.LiftOffer;
import x.mvmn.carpool.model.Route;

public interface LiftOfferRepository extends JpaRepository<LiftOffer, Integer>, JpaSpecificationExecutor<LiftOffer> {

	public int countByRoute(Route route);

	@Query("select lo from LiftOffer lo LEFT JOIN FETCH lo.route r LEFT JOIN FETCH r.waypoints WHERE lo.id = :id")
	public LiftOffer findByIdFetchRouteAndWaypoints(@Param("id") int id);

	public static Specification<LiftOffer> specUserId(int userId) {
		return new Specification<LiftOffer>() {
			@Override
			public Predicate toPredicate(Root<LiftOffer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("user").get("id"), userId);
			}
		};
	}

	public static Specification<LiftOffer> specNotUserId(int userId) {
		return new Specification<LiftOffer>() {
			@Override
			public Predicate toPredicate(Root<LiftOffer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.notEqual(root.get("user").get("id"), userId);
			}
		};
	}

	public static Specification<LiftOffer> specValidAfter(long validAfter) {
		return new Specification<LiftOffer>() {
			@Override
			public Predicate toPredicate(Root<LiftOffer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.greaterThanOrEqualTo(root.get("timeValidTo"), validAfter);
			}
		};
	}

	public static Specification<LiftOffer> specValidBefore(long velidBefore) {
		return new Specification<LiftOffer>() {
			@Override
			public Predicate toPredicate(Root<LiftOffer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.lessThanOrEqualTo(root.get("timeValidFrom"), velidBefore);
			}
		};
	}
}
