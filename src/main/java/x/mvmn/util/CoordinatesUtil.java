package x.mvmn.util;

import x.mvmn.carpool.model.DrivePath;
import x.mvmn.carpool.model.DrivePath.PointCoordinates;

public class CoordinatesUtil {

	private static final int COMPARISON_PRECISION = 1000000;

	public static boolean coordinatesMatch(double lat1, double lon1, double lat2, double lon2) {
		return Math.round(lat1 * COMPARISON_PRECISION) == Math.round(lat2 * COMPARISON_PRECISION)
				&& Math.round(lon1 * COMPARISON_PRECISION) == Math.round(lon2 * COMPARISON_PRECISION);
	}

	public static boolean pointCoordinatesMatch(PointCoordinates pc1, PointCoordinates pc2) {
		return CoordinatesUtil.coordinatesMatch(pc1.getLat(), pc1.getLon(), pc2.getLat(), pc2.getLon());
	}

	public static boolean pathCoordinatesMatch(DrivePath r1, DrivePath r2) {
		boolean match = r1.getWaypoints().size() == r2.getWaypoints().size();

		for (int i = 0; i < r1.getWaypoints().size() && match; i++) {
			match = CoordinatesUtil.pointCoordinatesMatch(r1.getWaypoints().get(i), r2.getWaypoints().get(i));
		}

		return match && CoordinatesUtil.coordinatesMatch(r1.getStartLat(), r1.getStartLon(), r2.getStartLat(), r2.getStartLon())
				&& CoordinatesUtil.coordinatesMatch(r1.getEndLat(), r1.getEndLon(), r2.getEndLat(), r2.getEndLon());
	}
}
