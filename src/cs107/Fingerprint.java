package cs107;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides tools to compare fingerprint.
 */
public class Fingerprint {

  /**
   * The number of pixels to consider in each direction when doing the linear
   * regression to compute the orientation.
   */
  public static final int ORIENTATION_DISTANCE = 16;

  /**
   * The maximum distance between two minutiae to be considered matching.
   */
  public static final int DISTANCE_THRESHOLD = 5;

  /**
   * The number of matching minutiae needed for two fingerprint to be considered
   * identical.
   */
  public static final int FOUND_THRESHOLD = 20;

  /**
   * The distance between two angle to be considered identical.
   */
  public static final int ORIENTATION_THRESHOLD = 20;

  /**
   * The offset in each direction for the rotation to test when doing the
   * matching.
   */
  public static final int MATCH_ANGLE_OFFSET = 2;

  /**
   * Returns an array containing the value of the 8 neighbours of the pixel at
   * coordinates <code>(row, col)</code>.
   * <p>
   * The pixels are returned such that their indices corresponds to the following
   * diagram:<br>
   * ------------- <br>
   * | 7 | 0 | 1 | <br>
   * ------------- <br>
   * | 6 | _ | 2 | <br>
   * ------------- <br>
   * | 5 | 4 | 3 | <br>
   * ------------- <br>
   * <p>
   * If a neighbours is out of bounds of the image, it is considered white.
   * <p>
   * If the <code>row</code> or the <code>col</code> is out of bounds of the
   * image, the returned value should be <code>null</code>.
   *
   * @param image array containing each pixel's boolean value.
   * @param row   the row of the pixel of interest, must be between
   *              <code>0</code>(included) and
   *              <code>image.length</code>(excluded).
   * @param col   the column of the pixel of interest, must be between
   *              <code>0</code>(included) and
   *              <code>image[row].length</code>(excluded).
   * @return An array containing each neighbours' value.
   */
  public static boolean[] getNeighbours(boolean[][] image, int row, int col) {
	  //image is assumed rectangular (all lines are the same length)
	  //image is assumed not null
	  assert (image != null); 
	  
	  //if row and col refer to a pixel out of image bounds, return null
	  if(row >= image.length || col >= image[0].length) {
		  return null;
	  }
	  
	  //initialise array of neighbours
	  boolean[] neighbours = new boolean[8]; 
	  
	  /*Case of non-border pixels*
	  - If the pixels in upper left and lower right corners of the pixel 
	  at (row, col) exist (are in bounds): then all neighbour pixels exist 
	  (as the image is assumed a rectangle)
	  - Therefore we do not need to check conditions for each neighbour
	  (more efficient as this will be the case for most pixels of the image) 
	   */
      if (row + 1 < image.length && row - 1 >= 0 && col + 1 < image[row].length && col - 1 >= 0 ) {
          neighbours[0] = image[row - 1][col];
          neighbours[1] = image[row - 1][col+1];
          neighbours[2] = image[row][col+1];
          neighbours[3] = image[row + 1][col + 1];
          neighbours[4] = image[row + 1][col];
          neighbours[5] = image[row + 1][col-1];
          neighbours[6] = image[row][col - 1];
          neighbours[7] = image[row - 1][col-1];
          return neighbours;
      }
      
      //*Case of pixels that did not fit previous criteria (border pixels)*
      
      //Up Middle -> 0
      if (row - 1 >= 0) { 
    	  neighbours[0] = image[row - 1][col];}
      //Up Right -> 1
      if (row - 1 >= 0 && col + 1 < image[row].length) { 
    	  neighbours[1] = image[row - 1][col+1];}
      //Centre Right -> 2
      if (col + 1 < image[row].length) { 
    	  neighbours[2] = image[row][col+1];}
      //Down Right -> 3
      if (row + 1 < image.length && col + 1 < image[row + 1].length) { 
    	  neighbours[3] = image[row + 1][col + 1];}
      //Down Middle -> 4
      if (row + 1 < image.length) {                        
          neighbours[4] = image[row + 1][col];}
      //Down Left -> 5
      if (col - 1 >= 0 && row + 1 < image.length) {        
          neighbours[5] = image[row + 1][col-1];}
      //Centre Left -> 6
      if (col - 1 >= 0) {                         
          neighbours[6] = image[row][col - 1];}
      //Up Left -> 7
      if (col - 1 >= 0 && row - 1 >= 0) {         
          neighbours[7] = image[row - 1][col-1];}
      //return
      return neighbours;
  }

  /**
   * Computes the number of black (<code>true</code>) pixels among the neighbours
   * of a pixel.
   *
   * @param neighbours array containing each pixel value. The array must respect
   *                   the convention described in
   *                   {@link #getNeighbours(boolean[][], int, int)}.
   * @return the number of black neighbours.
   */
  public static int blackNeighbours(boolean[] neighbours) {
	  
	  //the input is assumed in the correct format
	  assert (neighbours != null);
	  assert (neighbours.length == 8);
	  
	  int numberBlackNeighbours = 0;
	  
	  //iterate through neighbours and count true (black) pixels
	  for (boolean neighbour : neighbours) {
		  if (neighbour){
			  numberBlackNeighbours += 1;
		  }
	  }
	  return numberBlackNeighbours;
  }
  
  /**
   * Computes the number of white to black transitions among the neighbours of
   * pixel.
   *
   * @param neighbours array containing each pixel value. The array must respect
   *                   the convention described in
   *                   {@link #getNeighbours(boolean[][], int, int)}.
   * @return the number of white to black transitions.
   */
  public static int transitions(boolean[] neighbours) {
	  
	  //the input is necessarily in the correct format
	  assert (neighbours.length == 8);
	  
	  int numberTransitions = 0;
	  
	  // Dealing with case of transition 7-0
	  if (!neighbours[7] && neighbours[0]) {
		  numberTransitions += 1;
	  }
	  //Dealing with all other transition cases 0-1 --> 6-7
	  for (int i = 0; i<7; ++i) {
		  if (!neighbours[i] && neighbours[i + 1]) {
			  numberTransitions+=1;
		  }
	  }
	  return numberTransitions;
  }

  /**
   * Returns <code>true</code> if the images are identical and false otherwise.
   *
   * @param image1 array containing each pixel's boolean value.
   * @param image2 array containing each pixel's boolean value.
   * @return <code>True</code> if they are identical, <code>false</code>
   *         otherwise.
   */
  public static boolean identical(boolean[][] image1, boolean[][] image2) {
	  
	  //null images are not expected
	  assert(image1!=null & image2!=null);
	  
	  //Dealing with images with different amount of rows (not identical)
	  if (image1.length != image2.length) {
		  return false;
	  }

	  for (int i = 0; i < image1.length; ++i) {
		  //Dealing with images with different amounts of columns (not identical)
		  if (image1[i].length != image2[i].length) {
			  return false;
		  }
		  //Checking that every element is the same in each image
		  for (int j = 0; j < image1[i].length; ++j) {
			  if (image1[i][j] != image2[i][j]) {
				  return false; //different value at same indices = not identical
			  }
		  }
	  }
	  return true; //if no incompatibilities are found, return true (identical)
  }

  /**
   * Internal method used by {@link #thin(boolean[][])}.
   *
   * @param image array containing each pixel's boolean value.
   * @param step  the step to apply, Step 0 or Step 1.
   * @return A new array containing each pixel's value after the step.
   */
  public static boolean[][] thinningStep(boolean[][] image, int step) {
	  //image assumed rectangular
	  //image assumed not null
	  assert (image!=null);
	  
	  //initialising array to store thin version of image
	  boolean[][] imageCopy = new boolean[image.length][image[0].length];
	  
	  //double iteration to iterate over every pixel of image
	  for (int i = 0; i < image.length; ++i) {
		  for (int j = 0; j < image[i].length; ++j) {
			  
			  //Computing neighbours and black neighbours for the current pixel
			  boolean[] neighbours = getNeighbours(image, i, j);
			  int blackNeighbours = blackNeighbours(neighbours);
			  
			  //Checking for conditions that are common to both steps
			  if (image[i][j]
				&& neighbours!=null
				&& 2 <= blackNeighbours && blackNeighbours <= 6
				&& transitions(neighbours) == 1) {
				  
				  //conditions particular to step 0
				  if (step == 0
					&& (!neighbours[0] || !neighbours[2] || !neighbours[4]) 
					&& (!neighbours[2] || !neighbours[4] || !neighbours[6])) {
					  
					  imageCopy[i][j] = false; //black pixel not copied over
				  } 
				  //conditions particular to step 1
				  else if (step == 1
					&& (!neighbours[0] || !neighbours[6] || !neighbours[2]) 
					&& (!neighbours[0] || !neighbours[4] || !neighbours[6])) {
							
					  imageCopy[i][j] = false; //black pixel not copied over
				  }
				  else {
					  imageCopy[i][j] = image[i][j]; //pixel copied over
				  }
			  }
			  else {
				  imageCopy[i][j] = image[i][j]; //pixel copied over
			  }
		  }
	  }
	  return imageCopy;
  }
  
  /**
   * Compute the skeleton of a boolean image.
   *
   * @param image array containing each pixel's boolean value.
   * @return array containing the boolean value of each pixel of the image after
   *         applying the thinning algorithm.
   */
  public static boolean[][] thin(boolean[][] image) {
	  //image is assumed to always be a rectangle
	  //image is assumed not null
	  assert(image!=null);
	  
	  //2 Copies of image created (by iteration)
	  boolean[][] imageCopy1 = new boolean[image.length][image[0].length];
	  boolean[][] imageCopy2 = new boolean[image.length][image[0].length];
	  for (int i = 0; i < image.length; ++i) {
		  for (int j = 0; j < image[i].length; ++j) {
			  imageCopy1[i][j] = image[i][j];
			  imageCopy2[i][j] = image[i][j];
		  }
	  }
	  
	  //initialise variable checking if image is thin
	  boolean thin = false;
	  
	  //iterate while image is not thin 
	  while (!thin) {
		  
		  imageCopy1=thinningStep(imageCopy1,0);
		  imageCopy1=thinningStep(imageCopy1,1);
		  
		  //stop iteration when the thinningStep no longer changes the image
		  if (identical(imageCopy1, imageCopy2)) {
			  thin = true;
		  }
		  else {
			  //storing previous version of image at each step to check if image has changed
			  for (int i = 0; i < image.length; ++i) {
				  for (int j = 0; j < image[i].length; ++j) {
					  imageCopy2[i][j] = imageCopy1[i][j];
				  }
			  }
		  } 
	  }
	  return imageCopy1; //return thinned image
  }

	/**
	 * Computes all pixels that are connected to the pixel at coordinate
	 * <code>(row, col)</code> and within the given distance of the pixel.
	 *
	 * @param image    array containing each pixel's boolean value.
	 * @param row      the first coordinate of the pixel of interest.
	 * @param col      the second coordinate of the pixel of interest.
	 * @param distance the maximum distance at which a pixel is considered.
	 * @return An array where <code>true</code> means that the pixel is within
	 * <code>distance</code> and connected to the pixel at
	 * <code>(row, col)</code>.
	 */
	public static boolean[][] connectedPixels(boolean[][] image, int row, int col, int distance) {
	  //assert that the minutia pixel is black
	  assert (image[row][col]);

	  //initialising the final image of connected pixels (same size as image)
	  boolean[][] connectedPixels = new boolean[image.length][image[0].length];
	  
	  //initialise list to store coordinates of all connectedPixels
	  ArrayList<int[]> coordConnectedPixels = new ArrayList<>();
	  
	  //adding the minutia pixel in the final image and coordinate list
	  coordConnectedPixels.add(new int[]{row, col});
	  connectedPixels [row][col] = true;
	  
	  //initialising variables
	  int j = 0;
	  int x = row;
	  int y = col;
	  int[] coords = new int[] {x , y};

	  //iterating over every found pixel and checking for its neighbours until every connected pixel within distance is found
	  while (j < coordConnectedPixels.size()) {

		  //x and y are the coordinates of the current pixel
		  x = coordConnectedPixels.get(j)[0];
		  y = coordConnectedPixels.get(j)[1];

		  //get the neighbours of current pixel
		  boolean[] neighbours = getNeighbours(image, x, y);
		  
		  //assert neighbours is not null
		  assert (neighbours != null);

		  //iterate over every neighbour of the current pixel (if it is black and within distance, add to connectedPixels)
		  for (int i = 0; i <= 7; ++i) {
			  
			  //checking if current neighbour is black
			  if (neighbours[i]) {

				  //using switch to get the coordinates of the current neighbour
				  switch (i) {
				  		case 0: coords = new int[] {x - 1, y};
				  				break;
				  		case 1: coords = new int[] {x - 1, y + 1};
		  						break;
				  		case 2: coords = new int[] {x, y + 1};
		  						break;
				  		case 3: coords = new int[] {x + 1, y + 1};
		  						break;
				  		case 4: coords = new int[] {x + 1, y};
		  						break;
				  		case 5: coords = new int[] {x + 1, y - 1};
		  						break;
				  		case 6: coords = new int[] {x, y - 1};
		  						break;
				  		case 7: coords = new int[] {x - 1, y - 1};
		  						break;
				  }
				  //check whether current neighbour has already been added and whether it is within distance of the minutia
				  if (!contains(coordConnectedPixels, coords) && (coords[0] <= row + distance && coords[1] <= col + distance  && coords[0] >= row - distance && coords[1] >= col - distance)) {
					  
					  //adding current neighbour to connectedPixels and adding its coordinates to the list
					  coordConnectedPixels.add(coords);
					  connectedPixels[coords[0]][coords[1]] = true;
				  }
			  }
		  }
		  //increment j
		  ++j;
	  }
	  return connectedPixels;
  }
	
	
  /**
   * Checks if an ArrayList of int[] contains a certain int[] element
   * 
   * @param list		a list
   * @param element		an element we are looking for in the list
   */
  public static boolean contains(ArrayList<int[]> list, int[] element) {
	  for (int[] ints : list) {
		  if (Arrays.equals(ints, element)) {
			  return true;
		  }

	  }
	  return false;
  }

  /**
   * Computes the slope of a minutia using linear regression.
   *
   * @param connectedPixels the result of
   *                        {@link #connectedPixels(boolean[][], int, int, int)}.
   * @param row             the row of the minutia.
   * @param col             the col of the minutia.
   * @return the slope.
   */
  public static double computeSlope(boolean[][] connectedPixels, int row, int col) {
	  
	  //initialise all 3 sum variables needed
	  double sumSquareX = 0;
	  double sumSquareY = 0;
	  double sumProductXY = 0;
	  
	  //iterate over image and calculate the 3 sums
	  //for each pixel, x = j - col and y = row - i
	  for (int i = 0; i < connectedPixels.length; ++i) {
		  for (int j = 0; j < connectedPixels[i].length; ++j) {
			  if (connectedPixels[i][j]) {
				  sumSquareX += Math.pow(j - col, 2);
				  sumSquareY += Math.pow(row - i, 2);
				  sumProductXY += (j - col) * (row - i);
			  }
		  }
	  }
	  
	  //Particular case of vertical line
	  if (sumSquareX == 0) {
		  return Double.POSITIVE_INFINITY;
	  }
	  //Conditions for the two possible formulas of the slope
	  else if (sumSquareX >= sumSquareY) {
		  return (sumProductXY / sumSquareX);
	  }
	  else {
		  return (sumSquareY / sumProductXY);
	  }
  }

  /**
   * Computes the orientation of a minutia in radians.
   * 
   * @param connectedPixels the result of
   *                        {@link #connectedPixels(boolean[][], int, int, int)}.
   * @param row             the row of the minutia.
   * @param col             the col of the minutia.
   * @param slope           the slope as returned by
   *                        {@link #computeSlope(boolean[][], int, int)}.
   * @return the orientation of the minutia in radians.
   */
  public static double computeAngle(boolean[][] connectedPixels, int row, int col, double slope) {
	  
	  int pixelsAbove = 0;
	  int pixelsBelow = 0;

	  //****Particular case of vertical line (slope == Infinity)**** 
	  if (slope == Double.POSITIVE_INFINITY) {
		  //counting pixels above and below minutia
		  for (int i = 0; i < connectedPixels.length; ++i) {
			  for ( int j = 0 ; j < connectedPixels[i].length; ++j) {
				 if (connectedPixels[i][j]) {
					  if (i >= row) {
						  pixelsAbove += 1;
					  }
					  else {
						  pixelsBelow += 1;
					  }
				 }
			  }
		  }
		  //returning pi/2 or -pi/2 depending if the minutia point upwards or downwards
		  if (pixelsBelow <= pixelsAbove) {
			  return Math.PI/2 ;
		  }
		  else {
			  return -Math.PI/2 ;
		  }
	  }
	  
	  //initialise angle (used in *General Case* and *Particular case slope == 0*
	  double angle = Math.atan(slope);
	  
	  
	  //****Particular case of horizontal line (slope == 0)****
	  if (Double.compare(slope, 0.0) == 0) {
		  for (int i = 0; i < connectedPixels.length; ++i) {
			  for ( int j = 0 ; j < connectedPixels[i].length; ++j) {
				  if (connectedPixels[i][j]) {
					  //pixels above are pixels to the left of the minutia, pixels below are to the right
					  if ((j - col) < 0) {
						  pixelsAbove += 1;
					  }
					  else {
						  pixelsBelow += 1;
					  }
				  }
			  }
		  }
		  //add pi when necessary
		  if (pixelsAbove > pixelsBelow) {
			  angle += Math.PI;
		  }
		  //returning angle ( 0 or pi )
		  return angle;
	  }
	  
	  
	  //****General Case****

	  //Counting pixels above and below the perpendicular to the slope (going through the minutia)
	  for (int i = 0; i < connectedPixels.length; ++i) {
		  for ( int j = 0 ; j < connectedPixels[i].length; ++j) {
			  if (connectedPixels[i][j]) {
				  if ((row-i) >= (-1/slope) * (j-col)) {
					  pixelsAbove += 1;
				  }
				  else {
					  pixelsBelow += 1;
				  }
			  }
		  }
	  }
	  //adding pi to angle in the cases where it is necessary (angle = 0 is excluded, dealt with in particular case above)
	  if ((angle > 0 && pixelsBelow > pixelsAbove)
		||(angle < 0 && pixelsBelow < pixelsAbove)) {
		  angle += Math.PI;
	  }
	  //returning angle
	  return angle;
  }

  /**
   * Computes the orientation of the minutia that the coordinate <code>(row,
   * col)</code>.
   *
   * @param image    array containing each pixel's boolean value.
   * @param row      the first coordinate of the pixel of interest.
   * @param col      the second coordinate of the pixel of interest.
   * @param distance the distance to be considered in each direction to compute
   *                 the orientation.
   * @return The orientation in degrees.
   */
  public static int computeOrientation(boolean[][] image, int row, int col, int distance) {
	  
	  //using above methods to get the connected pixels, the slope and the angle
	  boolean[][] connectedPixels = connectedPixels(image, row, col, distance);
	  double slope = computeSlope(connectedPixels, row, col);
	  double angle = computeAngle(connectedPixels, row, col, slope);
	  
	  //converting the angle to degrees and rounding
	  angle = Math.round(Math.toDegrees(angle));
	  
	  //making the angle positive if necessary
	  if (angle < 0) {
		  angle += 360;
	  }
	  //returning the angle as an int
	  return (int) angle;
  }

  /**
   * Extracts the minutiae from a thinned image.
   *
   * @param image array containing each pixel's boolean value.
   * @return The list of all minutiae. A minutia is represented by an array where
   *         the first element is the row, the second is column, and the third is
   *         the angle in degrees.
   * @see #thin(boolean[][])
   */
  public static List<int[]> extract(boolean[][] image) {
	  
	  //initialise List and other variables used in the loop
	  List<int[]> minutiae = new ArrayList<>();
	  int transitions;
	  int orientation;
	  
	  //loop over every pixel except those on the borders
	  for (int i = 1; i < image.length - 1; ++i) {
		  for (int j = 1; j < image[i].length - 1; ++j) {
			  
			  //find number of transitions
			  transitions = transitions(getNeighbours(image, i, j));
			  
			  //if the pixel is a minutia (1 or 3 transitions), add it's row, col and orientation in the list
			  if (image[i][j] && (transitions==1 || transitions==3)) {
				  
				  orientation = computeOrientation(image, i, j, ORIENTATION_DISTANCE);
				  minutiae.add(new int[]{i, j, orientation});
			  }
		  }
	  }
	  //return the list
	  return minutiae;
  }

  /**
   * Applies the specified rotation to the minutia.
   *
   * @param minutia   the original minutia.
   * @param centerRow the row of the center of rotation.
   * @param centerCol the col of the center of rotation.
   * @param rotation  the rotation in degrees.
   * @return the minutia rotated around the given center.
   */
  public static int[] applyRotation(int[] minutia, int centerRow, int centerCol, int rotation) {
	  
	  //calculate new values (using temporary variables for clarity)
	  double x = minutia[1] - centerCol;
	  double y = centerRow - minutia[0];
	  double radianRotation= Math.toRadians(rotation);
	  double newX = x * Math.cos(radianRotation) - y * Math.sin(radianRotation);
	  double newY = x * Math.sin(radianRotation) + y * Math.cos(radianRotation);
	  int newRow= (int) Math.round(centerRow - newY);
	  int newCol= (int) Math.round( newX + centerCol);
	  int newOrientation = minutia[2] + rotation;  //we do not use the modulo (see README)
	  
	  return new int[] {newRow, newCol, newOrientation};
  }

  /**
   * Applies the specified translation to the minutia.
   *
   * @param minutia        the original minutia.
   * @param rowTranslation the translation along the rows.
   * @param colTranslation the translation along the columns.
   * @return the translated minutia.
   */
  public static int[] applyTranslation(int[] minutia, int rowTranslation, int colTranslation) {
	  
	  //initialise new minutia and calculate values
	  int[] newMinutia = new int[3];
	  
	  newMinutia[0] = minutia[0] - rowTranslation;
	  newMinutia[1] = minutia[1] - colTranslation;
	  newMinutia[2] = minutia[2];
	  
	  return newMinutia;
  } 
  
  /**
   * Computes the row, column, and angle after applying a transformation
   * (translation and rotation).
   *
   * @param minutia        the original minutia.
   * @param centerCol      the column around which the point is rotated.
   * @param centerRow      the row around which the point is rotated.
   * @param rowTranslation the vertical translation.
   * @param colTranslation the horizontal translation.
   * @param rotation       the rotation.
   * @return the transformed minutia.
   */
  public static int[] applyTransformation(int[] minutia, int centerRow, int centerCol, int rowTranslation,
      int colTranslation, int rotation) {
	  
	  //apply rotation and translation in turn on the minutiae
	  int[] transformedMinutia = applyRotation(minutia, centerRow, centerCol, rotation);
	  transformedMinutia = applyTranslation(transformedMinutia, rowTranslation, colTranslation);
	  
	  return transformedMinutia;
  }

  /**
   * Computes the row, column, and angle after applying a transformation
   * (translation and rotation) for each minutia in the given list.
   *
   * @param minutiae       the list of minutiae.
   * @param centerCol      the column around which the point is rotated.
   * @param centerRow      the row around which the point is rotated.
   * @param rowTranslation the vertical translation.
   * @param colTranslation the horizontal translation.
   * @param rotation       the rotation.
   * @return the list of transformed minutiae.
   */
  public static List<int[]> applyTransformation(List<int[]> minutiae, int centerRow, int centerCol, int rowTranslation, int colTranslation, int rotation) {

	  //initialise list of transformed minutiae
	  List<int[]> transformedMinutiae = new ArrayList<>();

	  //iterate over minutiae and transform
	  for (int[] minutia : minutiae) {
		  transformedMinutiae.add(applyTransformation(minutia, centerRow, centerCol, rowTranslation, colTranslation, rotation));
	  }

	  return transformedMinutiae;
  }
  /**
   * Counts the number of overlapping minutiae.
   *
   * @param minutiae1      the first set of minutiae.
   * @param minutiae2      the second set of minutiae.
   * @param maxDistance    the maximum distance between two minutiae to consider
   *                       them as overlapping.
   * @param maxOrientation the maximum difference of orientation between two
   *                       minutiae to consider them as overlapping.
   * @return the number of overlapping minutiae.
   */
  public static int matchingMinutiaeCount(List<int[]> minutiae1, List<int[]> minutiae2, int maxDistance, int maxOrientation) {
	  
	  //initialise number of matching minutiae
	  int minutiaeCount = 0;
	  
	  //Test Code
	  //ArrayList<int[][]> pairs = new ArrayList<int[][]>();

	  //iterate over all minutia from minutiae1 and minutiae2
	  for (int[] m1 : minutiae1) {
		  for (int[] m2 : minutiae2) {
			  
			  //calculate Euclidian distance and difference in orientation
			  double distanceEuclidienne = Math.sqrt(Math.pow(m1[0] - m2[0] , 2) + Math.pow(m1[1] - m2[1], 2));
			  int diffOrientation = Math.abs(m1[2] - m2[2]);
	
			  if (distanceEuclidienne <= maxDistance && diffOrientation <= maxOrientation) {
				  
				  //Test Code
				  //pairs.add(new int[][] {minutiae1.get(i), minutiae2.get(j)});
				  
				  //increment count when matching minutiae are found
				  ++minutiaeCount;
				  
				  //we do not want to match one minutiae m1 to several minutiae m2 (the opposite is fine)
				  break;
			  }
	
	
		  }
	
	  }
	/* Test Code
	if (maxMatchingMinutiae < minutiaeCount) {
	System.out.println("\n\n********Test******* ("+minutiaeCount+")");
	for (int[][] pair:pairs) {
		System.out.print("\n\nPair: ");
		for (int[] minutia:pair) {
			System.out.println("\n");
			for (int element:minutia) {
				System.out.print(element + "  ");
			}
		}
	}
	//maxMatchingMinutiae = minutiaeCount;
	}*/
	  
	return minutiaeCount;
  }
  
  //Test Code
  //public static int maxMatchingMinutiae = 19;

  /**
   * Compares the minutiae from two fingerprint.
   *
   * @param minutiae1 the list of minutiae of the first fingerprint.
   * @param minutiae2 the list of minutiae of the second fingerprint.
   * @return Returns <code>true</code> if they match and <code>false</code>
   *         otherwise.
   */
  public static boolean match(List<int[]> minutiae1, List<int[]> minutiae2) {
	  
	  //Test Code
	  int maxMatchings =0;

	  //iterate over all minutia from minutiae1 and minutiae2
	  for (int [] m1 : minutiae1) {
		  for (int [] m2 : minutiae2) {
			  
			  //calculate parameters for applyTransformation
			  int rowTranslation = m2[0] - m1[0];
			  int colTranslation = m2[1] - m1[1];
			  int rotation = m2[2] - m1[2];
			  
			  //try to find matching minutiae for rotation + or - MATCH_ANGLE_OFFSET
			  for (int k = rotation - MATCH_ANGLE_OFFSET; k <= rotation + MATCH_ANGLE_OFFSET ; ++k) {

				  List<int[]> newMinutiae2 = applyTransformation(minutiae2,m1[0],m1[1],rowTranslation,colTranslation, k);
				  
				  int matchingMinutiaeCount = matchingMinutiaeCount(minutiae1, newMinutiae2, DISTANCE_THRESHOLD, ORIENTATION_THRESHOLD);

				  if (matchingMinutiaeCount >= FOUND_THRESHOLD) {

					  /*Test Code to print data about the Matching*/
					  System.out.println("\nMatching: " + matchingMinutiaeCount);
					  System.out.println("Rotation = " + k);
					  System.out.println("Minutia: [" + m1[0] + ", " + m1[1]+ ", " + m1[2]+ "]");
					  System.out.println("Translation: " + rowTranslation + ", " + colTranslation);
					  
					  
					  return true;
				  }

				  /*Test Code to count the max number of matching*/
				  if (matchingMinutiaeCount > maxMatchings) {
					  maxMatchings = matchingMinutiaeCount;
				  }
			  }
		  }
	  }
	  
	  /*Test Code to print data about the Matching (when fingerprint are not the same)*/
	  System.out.println("\nMatching: " + maxMatchings + "  ");
	  
	  
	  return false;

  }
}
