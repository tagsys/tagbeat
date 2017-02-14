package org.tagsys.tagbeat.cr;

import org.apache.commons.math3.linear.RealMatrix;

public  class Signal{
	
		public static final int CODE_NO_ERROR = 0;
	
		public static final int CODE_NO_VIBRATION = 1;
	
		protected int code;
		
		protected boolean[] timeIndicator;
		
		protected RealMatrix phaseSeries;
		
		protected RealMatrix phi;
		
		protected RealMatrix recoveredSeries;
		
		protected double frequency;//the fundamental frequency
		

		public boolean[] getTimeIndicator() {
			return timeIndicator;
		}

		public void setTimeIndicator(boolean[] timeIndicator) {
			this.timeIndicator = timeIndicator;
		}

		public RealMatrix getPhaseSeries() {
			return phaseSeries;
		}

		public void setPhaseSeries(RealMatrix phaseSeries) {
			this.phaseSeries = phaseSeries;
		}

		public RealMatrix getPhi() {
			return phi;
		}

		public void setPhi(RealMatrix phi) {
			this.phi = phi;
		}

		public RealMatrix getRecoveredSeries() {
			return recoveredSeries;
		}

		public void setRecoveredSeries(RealMatrix recoveredSeries) {
			this.recoveredSeries = recoveredSeries;
		}
		
		public double getFrequency() {
			return frequency;
		}

		public void setFrequency(double frequency) {
			this.frequency = frequency;
		}
		
	
						
}