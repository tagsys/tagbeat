package org.tagsys.tagbeat.cr;

import java.io.*;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class ComplexMatrix {
	public RealMatrix real;
	public RealMatrix imag;
	public int M = 3000;
	public int N = 3000;

//	public ComplexMatrix() {
//
//	}

	public ComplexMatrix(int M, int N) {
		this.M = M;
		this.N = N;
		real = MatrixUtils.createRealMatrix(M, N);
		imag = MatrixUtils.createRealMatrix(M, N);

	}

	public ComplexMatrix(RealMatrix real) {
		this.real = real;

		imag = MatrixUtils.createRealMatrix(real.getRowDimension(), real.getColumnDimension());
		M = real.getRowDimension();
		N = real.getColumnDimension();
	}

	public ComplexMatrix(RealMatrix real, RealMatrix imag) {
		this.real = real;
		this.imag = imag;
		M = real.getRowDimension();
		N = real.getColumnDimension();
	}

	public ComplexMatrix(int M, int N, File file) throws Exception {
		this(M,N);
		real = MatrixUtils.createRealMatrix(M, N);
		imag = MatrixUtils.createRealMatrix(M, N);
		int row, column;
		try
		{
			DataInputStream inData=new DataInputStream(
					new BufferedInputStream(
							new FileInputStream(file)));
			for (row = 0;row<M;row++){
				for (column = 0;column<N;column++){
					if(inData.available()!=0){
						real.setEntry(row, column, inData.readDouble());
						imag.setEntry(row, column, inData.readDouble());
					}
				}
			}
			inData.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public ComplexMatrix scalarMultiply(double m) {
		RealMatrix rm = real.scalarMultiply(m);
		RealMatrix mm = imag.scalarMultiply(m);
		return new ComplexMatrix(rm, mm);
	}

	public ComplexMatrix add(ComplexMatrix cm) {
		RealMatrix rm = real.add(cm.real);
		RealMatrix mm = imag.add(cm.imag);

		return new ComplexMatrix(rm, mm);
	}

	public ComplexMatrix minus(ComplexMatrix cm) {
		RealMatrix rm = real.add(cm.real.scalarMultiply(-1));
		RealMatrix mm = imag.add(cm.imag.scalarMultiply(-1));

		return new ComplexMatrix(rm, mm);
	}

	public ComplexMatrix multiply(ComplexMatrix cm) {
		RealMatrix rm = real.multiply(cm.real).subtract(imag.multiply(cm.imag));
		RealMatrix mm = real.multiply(cm.imag).add(imag.multiply(cm.real));

		return new ComplexMatrix(rm, mm);
	}

	public ComplexMatrix transpose() {
		RealMatrix rm = real.transpose();
		RealMatrix mm = imag.transpose().scalarMultiply(-1);
		return new ComplexMatrix(rm, mm);
	}

	public ComplexMatrix dotTranspose() {
		RealMatrix rm = real.transpose();
		RealMatrix mm = imag.transpose();
		return new ComplexMatrix(rm, mm);
	}

	public ComplexMatrix inverse() {

		RealMatrix rm = MatrixUtils.inverse(real.add(imag.multiply(MatrixUtils.inverse(real).multiply(imag))));
		RealMatrix mm = MatrixUtils.inverse(real).multiply(imag).multiply(rm).scalarMultiply(-1);

		return new ComplexMatrix(rm, mm);
	}

	public ComplexMatrix getColumnMatrix(int n) {
		RealMatrix rm = real.getColumnMatrix(n);
		RealMatrix mm = imag.getColumnMatrix(n);

		return new ComplexMatrix(rm, mm);
	}

	public void setColumnMatrix(int n, double[] d1, double[] d2) {
		real.setColumn(n, d1);
		imag.setColumn(n, d2);
	}

	public void setEntry(int row, int column, double[] value) {
		real.setEntry(row, column, value[0]);
		imag.setEntry(row, column, value[1]);
	}

	public double[] getEntry(int row, int column) {
		double[] result = new double[2];
		result[0] = real.getEntry(row, column);
		result[1] = imag.getEntry(row, column);
		return result;
	}
	




	public static ComplexMatrix leftAppendRight(ComplexMatrix left, ComplexMatrix right) {
		if (left == null)
			return right;
		if (right == null)
			return left;
		if (left.M != right.M)
			return null;
		ComplexMatrix cm = new ComplexMatrix(left.M, left.N + right.N);
		cm.real.setSubMatrix(left.real.getData(), 0, 0);
		cm.real.setSubMatrix(right.real.getData(), 0, left.N);
		cm.imag.setSubMatrix(left.imag.getData(), 0, 0);
		cm.imag.setSubMatrix(right.imag.getData(), 0, left.N);
		return cm;

	}

	public double[] findMax() {
		double[] result = new double[3];
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				double abs = Math.sqrt(Math.pow(real.getEntry(i, j), 2) + Math.pow(imag.getEntry(i, j), 2));
				if (abs > result[0]) {
					result[0] = abs;
					result[1] = i;
					result[2] = j;
				}
			}
		}
		return result;
	}
}
