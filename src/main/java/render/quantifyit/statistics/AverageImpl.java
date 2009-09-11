package render.quantifyit.statistics;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AverageImpl implements Average {

	@Override
	public double mean(double... elements) {
		if (elements == null){
			throw new IllegalArgumentException("Please give at least one value.");
		}		
		int count = 0;
		double sum = 0;
		for (double element : elements){
			sum += element;
			count ++;
		}
		return sum/count;
	}

	
	@Override
	public double median(double... elements) {
		if (elements == null || elements.length == 0){
			throw new IllegalArgumentException("Please give at least one value.");
		}
		Arrays.sort(elements);
		double median = 0;
		int medianPosition = elements.length/2;
		
		if (elements.length%2 == 0){
			median = (elements[medianPosition - 1] + elements[medianPosition])/2;
		}else{
			median = elements[medianPosition];
		}
		return median;
	}

	@Override
	public Double[] mode(double... elements) {
		if (elements == null || elements.length == 0){
			throw new IllegalArgumentException("Please give at least one value.");
		}
		Set<Double> modes = new HashSet<Double>(); 
		int previousFrequency = 0;
		for (int i = 0; i < elements.length; i++) {
			int frequency = 0;
			for (int j = 0; j < elements.length; j++){
				if(elements[i] == elements[j]){
					frequency++;
				}
			}
			if(previousFrequency > frequency){
				modes.add(elements[i - 1]);
			} else if(previousFrequency < frequency) {
				modes.clear();
				modes.add(elements[i]);
			} else {
				modes.add(elements[i]);
				if(i != 0){
					modes.add(elements[i-1]);					
				}
			}
			previousFrequency = frequency;
		}
		
		return modes.toArray(new Double[]{});
	}


}
