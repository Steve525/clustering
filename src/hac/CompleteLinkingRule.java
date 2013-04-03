package hac;

import java.util.Collection;

public class CompleteLinkingRule implements LinkingRule {

	@Override
	public Double calculateDistance(Collection<Double> distances) {
		
		double max = Double.NaN;
		
		for (Double distance : distances) {
			
			if (Double.isNaN(max) || distance > max)
				max = distance;
			
		}
		
		return max;
		
	}

}
