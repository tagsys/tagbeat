package org.tagsys.tagbeat;

import java.util.Arrays;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.MathUtils;

public class CompressiveReading {
		
	public static class PhaseSignal{
		
		public boolean[] timeIndicator;
		
		public RealMatrix phase;
		
	}
	
	public static PhaseSignal preprocess(long[] time, double[] phase){
		
		
		for(int i=0;i<time.length;i++){
			time[i] = (long)Math.floor((time[i]-time[0])/1000.0);
		}
				
		int length = (int)time[time.length-1];
		PhaseSignal signal = new PhaseSignal();
		signal.timeIndicator = new boolean[length];
		signal.phase = MatrixUtils.createRealMatrix(length,1);
		
		Arrays.fill(signal.timeIndicator, false);
		
		for(int i=0;i<time.length;i++){
			int index = (int)time[i];
			signal.timeIndicator[index] = true;
			signal.phase.setEntry(i,1,phase[i]);
		}
		
		return signal;
	}
	
	public static RealMatrix measure(int M, int N, int Q, boolean[] timeIndicator){
		
		RealMatrix phi = MatrixUtils.createRealMatrix(M, N);
		
		RealMatrix eye = MatrixUtils.createRealIdentityMatrix(N);
		FastFourierTransformer.transformInPlace(eye.getData(),DftNormalization.STANDARD, TransformType.FORWARD);
		
		
		for(int m=0;m<M;m++){
			for(int n=(m-1)*Q;n<m*Q;n++){
				if( timeIndicator[n]){
					phi.setEntry(m, n, 1.0);
				}
			}
		}
		
		return phi;
		
	}
	
	public static PhaseSignal compressiveReading(int M, int N, int K, RealMatrix signal, RealMatrix phi){
		
		RealMatrix s = phi.multiply(signal);
		
		int m= 2*K;
		
		return null;
	}
	
	public static void main(String[] args) {
		
		RealMatrix eye = MatrixUtils.createRealIdentityMatrix(8);
		double[][] data = eye.getData();
		FastFourierTransformer.transformInPlace(data,DftNormalization.STANDARD, TransformType.FORWARD);
		for(int i=0;i<data.length;i++){
			for(int j=0;j<data[0].length;j++){
				System.out.print(data[i][j]);
			}
			System.out.println();
		}
	}

}
