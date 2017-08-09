package x.mvmn.carpool.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import x.mvmn.carpool.service.persistence.LiftJoinRequestRepository;
import x.mvmn.carpool.service.persistence.LiftRequestRepository;

@Component
public class LiftRequestCleanupJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(LiftRequestCleanupJob.class);

	@Autowired
	LiftRequestRepository liftRequestRepository;

	@Autowired
	LiftJoinRequestRepository liftJoinRequestRepository;

	@Scheduled(fixedRate = 1000 * 60 * 60 * 24)
	public void doCleanup() {
		int ljrc = 0;
		int lrc = 0;
		LOGGER.info("Starting old lift requests cleanup.");
		try {
			long unixTime24HoursAgo = System.currentTimeMillis() / 1000 - 60 * 60 * 24;
			ljrc = liftJoinRequestRepository.deleteByLiftRequestTimeValidToLessThan(unixTime24HoursAgo);
			lrc = liftRequestRepository.deleteByTimeValidToLessThan(unixTime24HoursAgo);
		} catch (Exception e) {
			LOGGER.error("Error occurred during old lift requests cleanup.", e);
		}
		LOGGER.info("Finished old lift requests cleanup. Removed outdated entities: {} lift join requests, {} lift requests.", new Integer(ljrc),
				new Integer(lrc));
	}
}
