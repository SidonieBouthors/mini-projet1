package cs107;

import java.util.List;

public final class SignatureChecks {
	
	/** =========================
	 *  !!!! DO NOT MODIFY !!!!
	 *  =========================
	 *  
	 *  Any submission for which this file does not compile will not be accepted
	 *  
	 */

    @SuppressWarnings("unused")
    public static final void check() {
	boolean[][] image = new boolean[3][3];
	
	boolean[] neighbours = Fingerprint.getNeighbours(image, 1, 1);
	int blackTrans = Fingerprint.blackNeighbours(neighbours);
	int trans = Fingerprint.transitions(neighbours);
	boolean same = Fingerprint.identical(image, image);
	boolean[][] skeleton = Fingerprint.thin(image);
	boolean[][] skeleton1 = Fingerprint.thinningStep(image, 0);
	
	boolean[][] connectedPixels = Fingerprint.connectedPixels(image, 1, 1, 3);
	double slope = Fingerprint.computeSlope(connectedPixels, 1, 1);
	double angle = Fingerprint.computeAngle(connectedPixels, 1, 1, slope);
	int orientation = Fingerprint.computeOrientation(image, 1, 1, 3);
	List<int[]> minutiae = Fingerprint.extract(image);

	int[] minutia = new int[3];
	
	int[] minutiaR = Fingerprint.applyRotation(minutia, 0, 0, 0);
	int[] minutiaT = Fingerprint.applyTranslation(minutia, 0, 0);

	int[] minutiaRT = Fingerprint.applyTransformation(minutia, 0, 0, 0, 0, 0);
	List<int[]> minutiaeRT = Fingerprint.applyTransformation(minutiae, 0, 0, 0, 0, 0);

	int cnt = Fingerprint.matchingMinutiaeCount(minutiae, minutiae, 10, 10);
	boolean match = Fingerprint.match(minutiae, minutiae);
    }
}

						
