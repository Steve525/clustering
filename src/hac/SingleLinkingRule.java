package hac;

import java.util.Collection;

public class SingleLinkingRule implements LinkingRule {

	@Override
	public Double calculateDistance( Collection<Double> distances ) {
		
		double min = Double.NaN;
		
		for (Double distance : distances) {
			
			if (Double.isNaN(min) || distance < min) {
				min = distance;
			}
			
		}
		
		return min;
		
	}

}
