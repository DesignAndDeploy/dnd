package edu.teco.dnd.deploy;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public interface DistributionEvaluator {
	int evaluate(Distribution distribution);
	
	Map<Distribution, Integer> evaluate(Collection<? extends Distribution> distributions);
	
	Entry<Distribution, Integer> getBestDistribution(Collection<? extends Distribution> distributions);
}
