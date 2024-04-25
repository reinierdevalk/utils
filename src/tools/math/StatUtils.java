package tools.math;

/**
 * This class provides some functions for statistical data evaluation.
 * @author tweyde
 */
public class StatUtils {
	
	/**
	 * Calculates the average of the numbers in <code>data</code>. 
	 * 
	 * @param data The numbers to work with. 
	 * @return The average. 
	 */
	public static double mean(double data[]){
		double tmp = 0;
		for (int i = 0; i < data.length; i++) {
			tmp += data[i];
		}
		tmp /= data.length;
		return tmp;
	}
	
	
	/**
	 * Calculate the unbiased estimate of a distribution's 
	 * standard deviation based on the data. 
	 * 
	 * @param data The data to calculate the standard deviation on. 
	 * @return The sample standard deviation
	 */
	public static double standardDeviationSample(double data[]){
		double mean = mean(data);
		double tmp = 0;
		for (int i = 0; i < data.length; i++) {
			tmp += Math.pow(data[i] - mean,2);
		}
		tmp /= data.length -1;
		return tmp;
	}
	
	/**
	 * Calculate the unbiased estimate of a distribution's 
	 * standard deviation based on a sample data set. 
	 * 
	 * @param data The data to calculate the standard deviation on. 
	 * @param mean The mean of the data.
	 * @return The sample standard deviation.
	 */
	static double standardDeviationSample(double data[], double mean){
		double tmp = 0;
		for (int i = 0; i < data.length; i++) {
			tmp += Math.pow(data[i] - mean,2);
		}
		if(data.length > 0)
			tmp /= data.length -1;
		return tmp;
	}
	
	/**
	 * This computes the unbiased estimate of covariance from two paired sample data sets. 
	 * data1 and data2 have to contain 
	 *  
	 * @param data1 The first data set.
	 * @param data2 The second data set.
	 * @param d1mean the mean of the first data set.  
	 * @param d2mean the mean of the second data set .
	 * @return The covariance estimate.
	 */
	static double covarianceSample(double data1[], double data2[], double d1mean, double d2mean){
		if(data1.length != data2.length)
			throw new IllegalArgumentException("for correlationR, data1 and data2 need to have the same dimension");
		double cov = 0;
		for (int i = 0; i < data1.length; i++) {
			cov += (data1[i] - d1mean) * (data2[i] - d2mean);
		}
		if(data1.length > 0)
			cov /= data1.length - 1;
		return cov;
	}

	
	/**
	 * This method calculates an unbiased estimate of the Pearson correlation coefficient R for 
	 * two paired sample data sets.
	 * See <a link="http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient">
	 * http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient</a> for the definition. 
	 * 
	 * @param data1 The first data set.	  
	 * @param data2 The second data set.
	 * @param d1mean The mean of the first data set.
	 * @param d2mean The mean of the second data set.
	 * @return The estimated Pearson correlation coefficient R.
	 */
	double correlationRSample(double data1[], double data2[]){
		double d1mean = mean(data1);
		double d2mean = mean(data1);
		return correlationRSample(data1, data2, d1mean, d2mean, 
				standardDeviationSample(data1,d1mean),
				standardDeviationSample(data2,d2mean));
	}

	/**
	 * This method calculates an unbiased estimate of the Pearson correlation coefficient R for 
	 * two paired sample data sets with given means.
	 * See <a link="http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient">
	 * http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient</a> for the definition. 
	 * 
	 * @param data1 The first data set.	  
	 * @param data2 The second data set.
	 * @param d1mean The mean of the first data set.
	 * @param d2mean The mean of the second data set.
	 * @return The estimated Pearson correlation coefficient R.
	 */
	double correlationRSample(double data1[], double data2[], double d1mean, double d2mean){
		return correlationRSample(data1, data2, d1mean, d2mean, 
				standardDeviationSample(data1,d1mean),
				standardDeviationSample(data2,d2mean));
	}

	/**
	 * This method calculates an unbiased estimate of the Pearson correlation coefficient R for 
	 * two paired sample data sets with given means and sample standard deviations.
	 * See <a link="http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient">
	 * http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient</a> for the definition. 
	 * 
	 * @param data1 The first data set.	  
	 * @param data2 The second data set.
	 * @param d1mean The mean of the first data set.
	 * @param d2mean The mean of the second data set.
	 * @param d1std The sample standard deviation of the first data set.
	 * @param d2std The sample standard deviation of the second data set.
	 * @return The estimated Pearson correlation coefficient R.
	 */
	double correlationRSample(double data1[], double data2[], double d1mean, double d2mean, double d1std, double d2std){
		if(data1.length != data2.length)
			throw new IllegalArgumentException("for correlationR, data1 and data2 need to have the same dimension");
		double numer = covarianceSample(data1, data2, d1mean, d2mean);
		double denom = d1std * d2std;
		return numer/denom;
	}

}
