package org.tagsys.tagbeat.cr;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import spark.utils.Assert;

public class CompressiveReading {

	// the length of processed signal
	private int N = 5000;
	// the frame size
	private int Q = 5;
	// the sparsity degree
	private int K = 5;
	// the row size
	private int M = (int) Math.ceil(N / Q);

	protected static Map<Integer, ComplexMatrix> eyeCache = new HashMap<Integer, ComplexMatrix>();
	
	
	private static CompressiveReading cr = null;
	
	public static CompressiveReading instance(){
		if(cr==null){
			cr = new CompressiveReading();
		}
		
		return cr;
	}

	public CompressiveReading() {
		
	}

	public CompressiveReading(int N, int Q, int K) {
		this.N = N;
		this.Q = Q;
		this.K = K;
		this.M = (int) Math.ceil(N / Q);
	}
	
	public void changeSampleNumber(int N){
		this.N = N;
		this.M = (int) Math.ceil(N / Q);
		System.out.println("Chanage parameter of N to "+ N);
	}
	
	public void changeFrameSize(int Q){
		this.Q = Q;
		this.M = (int) Math.ceil(N / Q);
		System.out.println("Change parameter of Q to " + Q);
	}
	
	public int getSampleNumber(){
		return this.N;
	}
	
	public int getFrameSize(){
		return this.Q;
	}
	
	public int getSparsity(){
		return this.K;
	}
	
	public void changeSparsity(int K){
		this.K = K;
		System.out.println("Change parameter of K to "+ K);
	}

	private ComplexMatrix loadEye() throws Exception {

		if (!eyeCache.containsKey(N)) {

			File file = new File("./basis/" + N + ".txt");
			if (!file.exists()) {
				throw new FourierBaseException(
						"[ERROR]:Cannot find the file of Fourier bases. Please generate them with Matlab firstly. Refer to the readme.md firsly.");
			}

			ComplexMatrix eye = new ComplexMatrix(N,N,file);

			eyeCache.put(N, eye);

		}

		return eyeCache.get(N);

	}

	private Signal preprocess(List<Long> time, List<Double> phase) {
		
		System.out.println("N["+N+"],Q["+Q+"],K["+K+"]");

		// ensure the length of samples is greater than N
		Long base = time.get(0);
		for (int i = 0; i < time.size(); i++) {
			time.set(i, (long) Math.floor((time.get(i) - base) / 1000.0));
		}
		

		Signal signal = new Signal();
		signal.timeIndicator = new boolean[N];
		signal.phaseSeries = MatrixUtils.createRealMatrix(N, 1);

		Arrays.fill(signal.timeIndicator, false);

		for (int i = 0; i < time.size(); i++) {
			int index = time.get(i).intValue();
			if(index<N){
				signal.timeIndicator[index] = true;
				signal.phaseSeries.setEntry(index, 0, Math.sin(phase.get(i)));
			}
		}
		
		signal.phi = MatrixUtils.createRealMatrix(M, N);

		for (int m = 0; m < M; m++) {
			for (int n = (m) * Q; n < (m + 1) * Q; n++) {
				if (signal.timeIndicator[n]) {
					signal.phi.setEntry(m, n, 1.0);
				}
			}
		}

		return signal;
	}

	public Signal recover(List<Long> time, List<Double> phase) throws Exception {
		
		Signal signal = preprocess(time, phase);

		return recover(signal);

	}
	
	private double seekFrequency(Signal signal){
		
		
		return 0;
		
	}

	public Signal recover(Signal signal) throws Exception {

		RealMatrix phi = signal.phi;
		RealMatrix phase = signal.phaseSeries;

		RealMatrix s = phi.multiply(phase); // M*1
		ComplexMatrix sc = new ComplexMatrix(s);
		int m = 2 * K;
		long start = System.currentTimeMillis();

		ComplexMatrix fftEyeNN = loadEye();
		
		System.out.println(fftEyeNN.N+"x"+fftEyeNN.M);

		ComplexMatrix Psi = fftEyeNN.scalarMultiply(1 / Math.sqrt(N));// N*N

		ComplexMatrix trans = Psi.transpose();
		ComplexMatrix T = new ComplexMatrix(M,N);
		for(int i=0;i<M;i++){
			for(int j=0;j<N;j++){
				double[] result = new double[]{0.0,0.0};
				for(int k=i*Q;k<(i+1)*Q;k++){
					if(phi.getEntry(i, k)==1){
						double[] temp = trans.getEntry(k,j);
						result[0] = result[0]+temp[0];
						result[1] = result[1]+temp[1];
					}
				}
				T.setEntry(i, j, result);
			}
		}
	
		ComplexMatrix hat_y = new ComplexMatrix(1, N);
		ComplexMatrix Aug_t = null;
		ComplexMatrix Aug_y = null;
		ComplexMatrix r_n = new ComplexMatrix(s);
		double[] product = new double[N];
		int[] pos_array = new int[m];
		ComplexMatrix temp;
		double[] zeroColumn = new double[M];

		for (int times = 0; times < m; times++) {
			for (int col = 0; col < N; col++) {
				temp = T.getColumnMatrix(col).transpose().multiply(r_n);
				product[col] = Math.sqrt(Math.pow(temp.real.getEntry(0, 0), 2) + Math.pow(temp.imag.getEntry(0, 0), 2));
			}
			double[] max = MathUtil.findMax(product);
			int pos = (int) max[1];
			Aug_t = ComplexMatrix.leftAppendRight(Aug_t, T.getColumnMatrix(pos));
			T.setColumnMatrix(pos, zeroColumn, zeroColumn);
			Aug_y = (Aug_t.transpose().multiply(Aug_t)).inverse().multiply(Aug_t.transpose()).multiply(sc);
			r_n = sc.minus(Aug_t.multiply(Aug_y));
			pos_array[times] = pos;

		}
		for (int i = 0; i < m; i++){
			hat_y.setEntry(0, pos_array[i], Aug_y.getEntry(i, 0));
		}
		ComplexMatrix hat_x = Psi.transpose().multiply(hat_y.dotTranspose());
		signal.recoveredSeries = hat_x.real;
		System.out.println("compressiveReading time: " + (System.currentTimeMillis() - start));
		
		
		
		
		return signal;
	}
	
	


	public static void main(String[] args) {
		try {
			long start = System.currentTimeMillis();

			List<Double> phaseList = new ArrayList<Double>();
			List<Long> timeList = new ArrayList<Long>();
			double timeThrehold = 0.4;
			Random rand = new Random();

			for (int i = 0; i < 3000; i++) {
				if (rand.nextDouble() < timeThrehold) {
					timeList.add(i * 1000L);
					phaseList.add(Math.cos(i / 50.0));
				}
			}
			
			Signal signal = new CompressiveReading().recover(timeList, phaseList);

			 long startoutput = System.currentTimeMillis();
			 output(signal.phaseSeries,"D:\\x.txt");
			 output(signal.recoveredSeries,"D:\\hatx.txt");
			 System.out.println("output time:"+(System.currentTimeMillis()-startoutput)/1000);
			 System.out.println("total time:"+(System.currentTimeMillis()-start)/1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void output(RealMatrix rm, String path) {

		FileWriter fw;
		double[] data = rm.getColumn(0);
		try {

			fw = new FileWriter(path);
			for (int i = 0; i < data.length; i++)
				fw.write(data[i] + " ");
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
