package hac;

public class ClusterPair implements Comparable<ClusterPair> {

	private Cluster _lCluster;
	private Cluster _rCluster;
	private Double _linkageDistance;

	public Cluster getLeft() {
		return _lCluster;
	}

	public void setLeft(Cluster lCluster) {
		this._lCluster = lCluster;
	}

	public Cluster getRight() {
		return _rCluster;
	}

	public void setRight(Cluster rCluster) {
		this._rCluster = rCluster;
	}

	public Double getLinkageDistance() {
		return _linkageDistance;
	}

	public void setLinkageDistance(Double distance) {
		this._linkageDistance = distance;
	}

	@Override
	public int compareTo(ClusterPair o) {
		
		int result;
		if (o == null || o.getLinkageDistance() == null) {
			result = -1;
		} else if (getLinkageDistance() == null) {
			result = 1;
		} else {
			result = getLinkageDistance().compareTo(o.getLinkageDistance());
		}

		return result;
		
	}

	public Cluster agglomerate(String name) {
		
		if (name == null) {
			StringBuilder sb = new StringBuilder();
			if (_lCluster != null) {
				sb.append(_lCluster.getName());
			}
			if (_rCluster != null) {
				if (sb.length() > 0) {
					sb.append(" AND ");
				}
				sb.append(_rCluster.getName());
			}
			name = sb.toString();
		}
		Cluster cluster = new Cluster(name);
		cluster.setDistance(getLinkageDistance());
		cluster.addChild(_lCluster);
		cluster.addChild(_rCluster);
		_lCluster.setParent(cluster);
		_rCluster.setParent(cluster);
		
//		for (int instance : _lCluster.getInstances()) {
//			cluster.setInstance(instance);			
//		}
//		for (int instance : _rCluster.getInstances()) {
//			cluster.setInstance(instance);
//		}
		
		return cluster;
		
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		if (_lCluster != null) {
			sb.append(_lCluster.getName());
		}
		if (_rCluster != null) {
			if (sb.length() > 0) {
				sb.append(" + ");
			}
			sb.append(_rCluster.getName());
		}
		sb.append(" : " + _linkageDistance);
		return sb.toString();
		
	}

}
