package x.mvmn.carpool.service.persistence;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import x.mvmn.carpool.model.LiftJoinRequest;

public interface LiftJoinRequestRepository extends JpaRepository<LiftJoinRequest, Integer>, JpaSpecificationExecutor<LiftJoinRequest> {

	public static Specification<LiftJoinRequest> specPassengerUserId(int userId) {
		return new Specification<LiftJoinRequest>() {
			@Override
			public Predicate toPredicate(Root<LiftJoinRequest> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("user").get("id"), userId);
			}
		};
	}

	public static Specification<LiftJoinRequest> specDriverUserId(int userId) {
		return new Specification<LiftJoinRequest>() {
			@Override
			public Predicate toPredicate(Root<LiftJoinRequest> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("offer").get("user").get("id"), userId);
			}
		};
	}

	public static Specification<LiftJoinRequest> specLiftOfferId(int liftOfferId) {
		return new Specification<LiftJoinRequest>() {
			@Override
			public Predicate toPredicate(Root<LiftJoinRequest> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("offer").get("id"), liftOfferId);
			}
		};
	}

	public static Specification<LiftJoinRequest> specValidAfter(long validAfter) {
		return new Specification<LiftJoinRequest>() {
			@Override
			public Predicate toPredicate(Root<LiftJoinRequest> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.greaterThanOrEqualTo(root.get("offer").get("timeValidTo"), validAfter);
			}
		};
	}

	public static Specification<LiftJoinRequest> specDriverInitiated(boolean driverInitiated) {
		return new Specification<LiftJoinRequest>() {
			@Override
			public Predicate toPredicate(Root<LiftJoinRequest> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("driverInitiated"), driverInitiated);
			}
		};
	}

	public static Specification<LiftJoinRequest> specApproved(boolean approved) {
		return new Specification<LiftJoinRequest>() {
			@Override
			public Predicate toPredicate(Root<LiftJoinRequest> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("approved"), approved);
			}
		};
	}

	public static Specification<LiftJoinRequest> specApprovedIsNull() {
		return new Specification<LiftJoinRequest>() {
			@Override
			public Predicate toPredicate(Root<LiftJoinRequest> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.isNull(root.get("approved"));
			}
		};
	}
}
