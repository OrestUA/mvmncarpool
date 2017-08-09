package x.mvmn.carpool.model;

import java.util.List;

public interface DrivePath {

	public static interface PointCoordinates {
		public double getLat();

		public double getLon();
	}

	public double getStartLat();

	public double getStartLon();

	public double getEndLat();

	public double getEndLon();

	public List<? extends PointCoordinates> getWaypoints();

}
