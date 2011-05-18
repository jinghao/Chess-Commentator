package edu.berkeley.nlp.classification;

import java.io.IOException;
import java.io.Writer;

import jnisvmlight.LabeledFeatureVector;

public class JinghaoFeatureVector extends LabeledFeatureVector {
	private static final long serialVersionUID = -2361850878723731574L;

	public JinghaoFeatureVector(double d, int[] range, double[] vector) {
		super(d, range, vector);
	}

	public void write(Writer writer) throws IOException {
		writer.write(Double.toString(m_label * m_factor));
		writer.write(" ");
		for (int i = 0; i < m_vals.length; i++) {
			if (m_vals[i] != 0) {
				writer.write(Integer.toString(m_dims[i]));
				writer.write(":");
				writer.write(Double.toString(m_vals[i]));
				if (i < m_vals.length - 1)
					writer.write(" ");
			}
		}
		writer.write("\n");
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(m_label * m_factor);
		sb.append(" ");
		for (int i = 0; i < m_vals.length; i++) {
			sb.append(m_dims[i]);
			sb.append(":");
			sb.append(m_vals[i]);
			if (i < m_vals.length - 1)
				sb.append(" ");
		}
//		sb.append("\n");
		return sb.toString();
	}
}
