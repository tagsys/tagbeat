package org.tagsys.tagscreen;

import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;

public class MathUtil {
	
	public static double[] abs(Complex[] input){
		double[] result = new double[input.length];
		for(int i=0;i<result.length;i++){
			result[i] = Math.sqrt(input[i].getReal()*input[i].getReal()+input[i].getImaginary()*input[i].getImaginary());
		}
		return result;
	}
	
	public static Complex[] zero(Complex[] input){
		for(int i=0;i<input.length;i++){
			if(input[i]==null){
				input[i] = Complex.ZERO;
			}
		}
		return input;
			
	}
	
	public static Complex[] conj(Complex[] input){
		Complex[] result = new Complex[input.length];
		for(int i=0;i<input.length;i++){
			result[i] = input[i].conjugate();
		}
		return result;
	}
	
	public static Complex[] multiply(Complex[] a,Complex[] b){
		Complex[] result = new Complex[a.length];
		for(int i=0;i<a.length;i++){
			if(a[i]==null || b[i]==null){
				result[i] = Complex.ZERO;
			}else{
				result[i] = a[i].multiply(b[i]);
			}
		}
		
		return result;
	}
	
	public static double[] pad(double[] input){
		int i=1;
		for(;i<1024;i++){
			if((int)Math.pow(2, i)>=input.length){
				break;
			}
		}
		
	    return Arrays.copyOf(input, (int)Math.pow(2, i));
	}
	
	public static double[] pad(double[] input, int length){
		double[] result = new double[length];
		for(int i=0;i<length;i++){
			if(i<input.length){
				result[i] = input[i];
			}else{
				result[i] = 0;
			}
		}
		return result;
	}
	

	
	public static Complex[] pad(Complex[] input){
	    
		int i=1;
		for(;i<1024;i++){
			if((int)Math.pow(2, i)>=input.length){
				break;
			}
		}
		
	    return Arrays.copyOf(input, (int)Math.pow(2, i));
	   
	}
	
	public static void normalize(double[] signal){
		double max = Double.MIN_VALUE;
		for(int i=0;i<signal.length;i++){
			if(Math.abs(signal[i])>max){
				max = Math.abs(signal[i]);
			}
		}
		
		for(int i=0;i<signal.length;i++){
			signal[i] = signal[i]/max;
		}
	}
			

}
