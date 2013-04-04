package hac;

import java.util.Collection;

public class AverageLinkingRule implements LinkingRule {

	@Override
	public Double calculateDistance(Collection<Double> distances) {
		double averageDistance = 0;
		for (Double dist : distances) {
			averageDistance += dist;
		}
		if (distances.size() > 0) {
			averageDistance = averageDistance / distances.size();
		} else {
			averageDistance = 0;
		}
		return averageDistance;
	}

}
