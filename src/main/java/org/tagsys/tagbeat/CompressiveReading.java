package org.tagsys.tagbeat;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.Random;

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
		//FastFourierTransformer.transformInPlace(eye.getData(),DftNormalization.STANDARD, TransformType.FORWARD);
		
		
		for(int m=0;m<M;m++){
			for(int n=(m)*Q;n<(m+1)*Q;n++){
				if( timeIndicator[n]){
					phi.setEntry(m, n, 1.0);
				}
			}
		}
		
		return phi;
		
	}
	
	public static RealMatrix compressiveReading(int M, int N, int K, RealMatrix signal, RealMatrix phi) throws Exception{
		long time1,time2;
		RealMatrix s = phi.multiply(signal); //M*1
		ComplexMatrix sc = new ComplexMatrix(s);
		int m= 2*K;
		long start = System.currentTimeMillis();
		ComplexMatrix fftEyeNN = new ComplexMatrix("C:\\eye3000.txt");
		long startcr = System.currentTimeMillis();
		System.out.println("read time: "+(System.currentTimeMillis()-start)/1000.0);
		time1=System.currentTimeMillis();
		ComplexMatrix Psi = fftEyeNN.scalarMultiply(1/Math.sqrt(N));//N*N
		time2=System.currentTimeMillis();
		System.out.println("time0: "+(time2-time1));
		time1=time2;
		ComplexMatrix T=new ComplexMatrix(phi).multiply(Psi.transpose());//M*N
		time2=System.currentTimeMillis();
		System.out.println("time00: "+(time2-time1));
		time1=time2;
		ComplexMatrix hat_y = new ComplexMatrix(1,N);
		ComplexMatrix Aug_t = null;
		ComplexMatrix Aug_y = null;
		ComplexMatrix r_n = new ComplexMatrix(s);
		double[] product = new double[N];
		int[] pos_array = new int[m];
		ComplexMatrix temp;
		double[] zeroColumn = new double[M];
		
		
		for(int times = 0; times < m; times++){
			time1=System.currentTimeMillis();
			
			for(int col = 0; col < N; col++){
				temp = T.getColumnMatrix(col).transpose().multiply(r_n);
				product[col] = Math.sqrt(Math.pow(temp.real.getEntry(0, 0), 2)+Math.pow(temp.imag.getEntry(0, 0), 2));
			}
			time2=System.currentTimeMillis();
			System.out.println("time1: "+(time2-time1));
			time1=time2;
			double[] max = MathUtil.findMax(product);
			int pos =(int) max[1];
			Aug_t = ComplexMatrix.LeftAppendRight(Aug_t, T.getColumnMatrix(pos));
			time2=System.currentTimeMillis();
			System.out.println("time2: "+(time2-time1));
			time1=time2;
			T.setColumnMatrix(pos, zeroColumn, zeroColumn);
			time2=System.currentTimeMillis();
			System.out.println("time3: "+(time2-time1));
			time1=time2;
			Aug_y = (Aug_t.transpose().multiply(Aug_t)).inverse().multiply(Aug_t.transpose()).multiply(sc);
			time2=System.currentTimeMillis();
			System.out.println("time4: "+(time2-time1));
			time1=time2;
			r_n=sc.minus(Aug_t.multiply(Aug_y));
			time2=System.currentTimeMillis();
			System.out.println("time5: "+(time2-time1));
			time1=time2;
			pos_array[times]=pos;
			
		}
		for(int i = 0; i < m;i++)
			hat_y.setEntry(0, pos_array[i],Aug_y.getEntry(i, 0));
		ComplexMatrix hat_x = Psi.transpose().multiply(hat_y.dotTranspose());
		System.out.println("compressiveReading time: "+(System.currentTimeMillis()-startcr));
		return hat_x.real;
	}
	
	public static void main(String[] args) {
		try {
			long start = System.currentTimeMillis();
			int N = 3000;
			int Q = 5;
			int M = N/Q;
			int K=10;
			boolean[] timeIndicator = new boolean[N];
			double timeThrehold = 0.4;
			Random rand = new Random();
			for(int i = 0 ; i< N;i++){
				if(rand.nextDouble()<timeThrehold){
					timeIndicator[i] = true;
				}
				else
					timeIndicator[i] = false;
			}
			
			RealMatrix Phi = measure(M,N,Q,timeIndicator);
			RealMatrix x =  MatrixUtils.createRealMatrix(N,1);
			double[] data = new double[N];
			for(int i = 0; i < N;i++)
				data[i] = Math.cos(i/50.0);
			x.setColumn(0, data);
			
			RealMatrix hat_x = compressiveReading( M, N, K, x, Phi);
			
			long startoutput = System.currentTimeMillis();
			output(x,"D:\\x.txt");
			output(hat_x,"D:\\hatx.txt");
			System.out.println("output time: "+(System.currentTimeMillis()-startoutput)/1000);
			System.out.println("total time: "+(System.currentTimeMillis()-start)/1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static void output(RealMatrix rm,String path){
		
		FileWriter fw;
		double[] data = rm.getColumn(0);
		try {
			
			fw = new FileWriter(path);
			for(int i = 0; i < data.length;i++)
			fw.write(data[i]+" ");
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
