package util.zunge;

public class CalcPR {
	static WSReader r;
	static int iterTimes = 20;
	static int zoom = 1000000;

	public CalcPR(WSReader r) {
		CalcPR.r = r;
	}

	public double[] calc(double[] bias, double d) {
		double[] temp = new double[bias.length];
		for (int i = 0; i < temp.length; ++i) {
			temp[i] = zoom / temp.length;
		}

		for (int i = 0; i < iterTimes; ++i) {
			temp = iter(temp, bias, d);
		}
		return temp;
	}

	public double[] iter(double[] input, double[] bias, double d) {
		// PR_n = d * bias + (1-d) * G * PR_(n-1)
		double[] ret = new double[r.ccpts()];
		for (int cur = 0; cur < r.ccpts(); ++cur) {
			ret[cur] = d * bias[cur];
			if (r.getInLink(cur) != null && r.getInLinkCount(cur) > 0) {
				for (int i = 0; i < r.getInLinkCount(cur); ++i) {
					int inId = r.getInLink(cur)[i];
					ret[cur] += (1 - d) * input[inId] / r.getOutLinkCount(inId);
				}
			}
		}
		return normalize(ret);
	}

	public double[] normalize(double[] input) {
		double sum = 0;
		if (input != null && input.length > 0) {
			for (int i = 0; i < input.length; ++i) {
				sum += input[i];
			}
			if (sum == 0) {
				return input;
			} else {
				double[] ret = new double[input.length];
				for (int i = 0; i < input.length; ++i) {
					ret[i] = zoom * input[i] / sum;
				}
				return ret;
			}
		}
		return input;
	}
}
