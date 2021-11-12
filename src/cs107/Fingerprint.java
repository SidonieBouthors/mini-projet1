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
   * The number of matching minutiae needed for two fingerprints to be considered
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
	  //image is supposed rectangular (all lines are the same length)
	  
	  // special case that is not expected (the image is supposed to have been checked earlier)
	  assert (image != null); 
	  
	  //If row and col refers to a pixel out of image bounds, return null
	  if(row >= image.length || col >= image[0].length) {
		  return null;
	  }
	  
	  //Array returned by getNeighbors
	  boolean[] returningTab = new boolean[8]; 
	  
	  //If the pixels in upper left and lower right corners of the pixel at (row, col) exist (are in bounds),
	  //then all neighbour pixels exist (as image is a rectangle)
	  //therefore we only run this piece of code (more efficient as this will be the case for most pixels of the image):
      if (row + 1 < image.length && row - 1 >= 0 && col + 1 < image[row].length && col - 1 >= 0 ) {
          returningTab[0] = image[row - 1][col];
          returningTab[1] = image[row - 1][col+1];
          returningTab[2] = image[row][col+1];
          returningTab[3] = image[row + 1][col + 1];
          returningTab[4] = image[row + 1][col];
          returningTab[5] = image[row + 1][col-1];
          returningTab[6] = image[row][col - 1];
          returningTab[7] = image[row - 1][col-1];
          return returningTab;
      }
      
      //Code for pixels in corners/borders that did not satisfy previous condition
      
      // Check d'indice Up Middle / tab indice 0
      if (row - 1 >= 0) { 
          returningTab[0] = image[row - 1][col];
      }
      // Check d'indice Up Right / tab indice 1
      if (row - 1 >= 0 && col + 1 < image[row].length) {        
          returningTab[1] = image[row - 1][col+1];
      }
      // Check d'indice Center Right / tab indice 2
      if (col + 1 < image[row].length) {                        
          returningTab[2] = image[row][col+1];
      }
      // Check d'indice Down Right / tab indice 3
      if (row + 1 < image.length && col + 1 < image[row + 1].length) {       
          returningTab[3] = image[row + 1][col + 1];
      }
      // Check d'indice Down Middle / tab indice 4
      if (row + 1 < image.length) {                        
          returningTab[4] = image[row + 1][col];
      }
      // Check d'indice Down Left / tab indice 5
      if (col - 1 >= 0 && row + 1 < image.length) {        
          returningTab[5] = image[row + 1][col-1];
      }
      // Check d'indice Center Left / tab indice 6
      if (col - 1 >= 0) {                         
          returningTab[6] = image[row][col - 1];
      }
      // Check d'indice Up Left / tab indice 7
      if (col - 1 >= 0 && row - 1 >= 0) {         
          returningTab[7] = image[row - 1][col-1];
      }
      
      return returningTab;
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
	  
	  //the input is necessarily in the correct format
	  assert (neighbours.length == 8);
	  
	  int numberBlackNeighbours = 0;
	  
	  //iterate through neighbours and count true (black) pixels
	  for (int i = 0; i<8; ++i) {
		  if (neighbours[i]){
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
	  
	  //New array to store thinned version of image
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
	  
	  //initialize variable used to break loop & keeping track of image to thin
	  boolean thin = false;
	  
	  //iterate while image is not thin 
	  while (!thin) {
		  
		  imageCopy1=thinningStep(imageCopy1,0);
		  imageCopy1=thinningStep(imageCopy1,1);
		  
		  if (identical(imageCopy1, imageCopy2)) {
			  thin = true;
		  }
		  else {
			  for (int i = 0; i < image.length; ++i) {
				  for (int j = 0; j < image[i].length; ++j) {
					  imageCopy2[i][j] = imageCopy1[i][j];
				  }
			  }
		  }
		  
		  
	  }
	  return imageCopy1;
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
	  // It is assumed that the minutia pixel is black
	  assert (image[row][col]);

	  // Ending tab
	  boolean[][] returningTab = new boolean[image.length][image[0].length];
	  
	  //initialize list to store coordinates of all connectedPixels
	  ArrayList<int[]> coordNeighbours = new ArrayList<int[]>();

	  coordNeighbours.add(new int[]{row, col});
	  returningTab [row][col]=true;

	  int j = 0;
	  int x = row;
	  int y = col;
	  int[] coords = new int[] {x , y};

	  // The goal of this algorithm is to iterate through the connected pixels with the method getneighbour. We begin from the minutia until we don't find another NEW connected pixel.

	  while (j < coordNeighbours.size()) {

		  //x and y will only be the coordinates of every connected pixels
		  x = coordNeighbours.get(j)[0];
		  y = coordNeighbours.get(j)[1];
		  
		  //Check if current pixel is within distance of minutia
		  
		  /*
		  if ((x >= row + distance || y >= col + distance || y <= col - distance)|| x <= row - distance) {
			  
			  //temp
			  System.out.println("Out of bounds : x = " + x + " and y = " + y);
			  
			  ++j;
			  continue;
		  }*/

		  //get the neighbours of current pixels
		  boolean[] neighbours = getNeighbours(image, x, y);

		  
		  for (int i = 0; i <= 7; ++i) {

			  if (neighbours[i]) {

				  //If one of the coordinates has already been registered before then we don't add this new coordinate. To not be stuck in an infinite loop.
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
				  
				  if (!contains(coordNeighbours, coords) && (coords[0] <= row + distance && coords[1] <= col + distance  && coords[0] >= row - distance && coords[1] >= col - distance)) {
					  coordNeighbours.add(coords);
					  returningTab[coords[0]][coords[1]] = true;
					  
					//temp
					  //System.out.println("\n Neighbour " + i + " of pixel " + j);
				  }
				  
				  /*
				  if (i == 0 && !contains(coordNeighbours, new int[]{x - 1, y})) {

					  coordNeighbours.add(new int[]{x - 1, y});
					  returningTab[x - 1][y] = true;
					  
					  //temp
					  System.out.println("\n Neighbour " + i + " of pixel " + j);

				  } else if (i == 1 && !contains(coordNeighbours, new int[]{x - 1, y + 1})) {

					  coordNeighbours.add(new int[]{x - 1, y + 1});
					  returningTab[x - 1][y + 1] = true;
					  
					//temp
					  System.out.println("\n Neighbour " + i + " of pixel " + j);

				  } else if ((i == 2) && ((j - 1 < 0) || (j - 1 >= 0 && !contains(coordNeighbours, new int[]{x, y + 1})))) {
					  
					  coordNeighbours.add(new int[]{x, y + 1});
					  returningTab[x][y + 1] = true;
					  
					//temp
					  System.out.println("\n Neighbour " + i + " of pixel " + j);
					  
				  } else if ((i==3) && ((j-1 < 0) || (j - 1 >= 0 && !contains(coordNeighbours,new int[]{x + 1, y + 1})))) {
					  
					  coordNeighbours.add(new int[]{x + 1, y + 1});
					  returningTab[x + 1][y + 1] = true;
					  
					//temp
					  System.out.println("\n Neighbour " + i + " of pixel " + j);
					  
				  } else if ((i==4) && ((j-1 < 0) || (j - 1 >= 0 && !contains(coordNeighbours, new int[]{x + 1, y})))) {
					  
					  coordNeighbours.add(new int[]{x + 1, y});
					  returningTab[x + 1][y] = true;
					  
					//temp
					  System.out.println("\n Neighbour " + i + " of pixel " + j);
					  
				  } else if ((i==5) && ((j-1 < 0) || (j - 1 >= 0 && !contains(coordNeighbours, new int[]{x + 1, y - 1})))) {
					  
					  coordNeighbours.add(new int[]{x + 1, y - 1});
					  returningTab[x + 1][y - 1] = true;
					  
					//temp
					  System.out.println("\n Neighbour " + i + " of pixel " + j);
					  
				  } else if ((i==6) && ((j-1 < 0) || (j - 1 >= 0 && !contains(coordNeighbours, new int[]{x, y - 1})))) {
					  
					  coordNeighbours.add(new int[]{x, y - 1});
					  returningTab[x][y - 1] = true;
					  
					//temp
					  System.out.println("\n Neighbour " + i + " of pixel " + j);
					  
				  } else if ((i == 7) && ((j - 1 < 0) || (j - 1 >= 0 && !contains(coordNeighbours, new int[]{x - 1, y - 1})))) {
					  
					  coordNeighbours.add(new int[]{x - 1, y - 1});
					  returningTab[x - 1][y - 1] = true;
					  
					//temp
					  System.out.println("\n Neighbour " + i + " of pixel " + j);
				  } */
			  }
		  }
		  ++j;
	  }
	  
	  //temp
	  /*
	  for ( int[] pixel:coordNeighbours) {
		  System.out.println("\nPixel: ");
		  for (int element:pixel) {
			  System.out.print(element + " ");
		  }
	  } */
	  return returningTab;
  }
	// The goal of this method is to chech if an arraylist of int [] contains a special int[]
  public static boolean contains(ArrayList<int[]> tab, int[] insidetab) {
	  for (int[] ints : tab) {
		  if (Arrays.equals(ints, insidetab)) {
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
	  
	  //initialize all 3 sum variables needed
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

	  //****Particular case of vertical line**** 
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
		  //returning pi/2 or -pi/2 depending if the line is upwards or downwards
		  if (pixelsBelow <= pixelsAbove) {
			  return Math.PI/2 ;
		  }
		  else {
			  return -Math.PI/2 ;
		  }
	  }
	  
	  //initialize angle (used in General Case and Particular case slope == 0
	  double angle = Math.atan(slope);
	  
	  
	  //****Particular case of slope == 0****
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
		  if (pixelsAbove > pixelsBelow) {
			  angle += Math.PI;
		  }
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
	  
	  //temp
	  //System.out.println("Angle brut: " + angle + " Slope: " + slope);
	  //System.out.println("Perpendicular slope: " + (-1/slope * col));
	  
	  //adding pi to angle in the cases where it is necessary (angle = 0 is excluded, dealt with in particular case above)
	  if ((angle > 0 && pixelsBelow > pixelsAbove)
		||(angle < 0 && pixelsBelow < pixelsAbove)) {
		  angle += Math.PI;
	  }
	  
	  //temp
	  //System.out.println("Angle ajusté: " + angle + " pixelsAbove: " + pixelsAbove + "  pixelsBelow: " + pixelsBelow);
	  
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
	  
	  //Use above methods to get the connected pixels, the slope and the angle
	  boolean[][] connectedPixels = connectedPixels(image, row, col, distance);
	  double slope = computeSlope(connectedPixels, row, col);
	  double angle = computeAngle(connectedPixels, row, col, slope);
	  
	  //converting the angle to degrees and rounding
	  angle = Math.round(Math.toDegrees(angle));
	  
	  //making the angle positive if necessary
	  if (angle < 0) {
		  angle += 360;
	  }
	  
	  //temp
	  /*if ((int) angle == 68) {
		  System.out.println("slope" + slope);
		  Helper.writeBinary("1_1minutia68connectedpixels.png", connectedPixels);
		  Helper.writeBinary("1_1minutia68image.png", image);
	  }*/
	  
	  //temp
	  //System.out.println("Angle in degrees: " + angle);
	  
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
	  
	  //initialize List and other variables used in the loop
	  List<int[]> minutiae = new ArrayList<int[]>();
	  int transitions;
	  int orientation;
	  
	  //loop over every pixel exept those on the borders
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
	  
	  double x = minutia[1] - centerCol;
	  double y = centerRow - minutia[0];
	  double radianRotation= Math.toRadians(rotation);
	  double newX = x * Math.cos(radianRotation) - y * Math.sin(radianRotation);
	  double newY = x * Math.sin(radianRotation) + y * Math.cos(radianRotation);
	  int newRow= (int) Math.round(centerRow - newY);
	  int newCol= (int) Math.round( newX + centerCol);
	  int newOrientation = minutia[2] + rotation;  // modulo????
	  
	  //temp
	  //System.out.println("\nCurrent Rotation:"+minutia[2]+"   Change " + rotation);
	  
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

	  List<int[]> transformedMinutiae = new ArrayList<>();


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
	  
	  int minutiaeCount = 0;
	  
	  //ArrayList<int[][]> pairs = new ArrayList<int[][]>();

	  for (int[] m1 : minutiae1) {
		  for (int[] m2 : minutiae2) {
			  
			  int row1 = m1[0];
			  int col1 = m1[1];
			  int orientation1 = m1[2];
			  int row2 = m2[0];
			  int col2 = m2[1];
			  int orientation2 = m2[2];
			  
			  double distanceEuclidienne = Math.sqrt(Math.pow(m1[0] - m2[0] , 2) + Math.pow(m1[1] - m2[1], 2));
	
			  int diffOrientation = Math.abs(m1[2] - m2[2]);
	
			  if (distanceEuclidienne <= maxDistance && diffOrientation <= maxOrientation) {
				  
				  //temp
				  //pairs.add(new int[][] {minutiae1.get(i), minutiae2.get(j)});
				  
				  ++minutiaeCount;
				  
				  //we do not want to match one minutiae m1 to several minutiae m2 (the opposite is fine)
				  break;
			  }
	
	
		  }
	
	  }
	//temp
	/*
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
	}
	*/
	  
	return minutiaeCount;
  }
  
  //temp
  //public static int maxMatchingMinutiae = 19;

  /**
   * Compares the minutiae from two fingerprints.
   *
   * @param minutiae1 the list of minutiae of the first fingerprint.
   * @param minutiae2 the list of minutiae of the second fingerprint.
   * @return Returns <code>true</code> if they match and <code>false</code>
   *         otherwise.
   */
  public static boolean match(List<int[]> minutiae1, List<int[]> minutiae2) {
	  
	  //temp
	  int maxMatchings =0;

	  for (int i = 0; i < minutiae1.size(); i++) {
		  for (int j = 0; j < minutiae2.size(); j++) {

			  
			  int rowTranslation = minutiae2.get(j)[0] - minutiae1.get(i)[0];
			  int colTranslation = minutiae2.get(j)[1] - minutiae1.get(i)[1];
			  int rotation = minutiae2.get(j)[2] - minutiae1.get(i)[2];
			  
			  //temp
			  //System.out.println("\nrotation = " + rotation);
			  
			  for (int k = rotation - MATCH_ANGLE_OFFSET; k <= rotation + MATCH_ANGLE_OFFSET ; ++k) {

				  List<int[]> newMinutiae2 = applyTransformation(minutiae2,minutiae1.get(i)[0],minutiae1.get(i)[1],rowTranslation,colTranslation, k);
				  
				  int matchingMinutiaeCount = matchingMinutiaeCount(minutiae1, newMinutiae2, DISTANCE_THRESHOLD, ORIENTATION_THRESHOLD);
				  
				  
				  if (matchingMinutiaeCount >= FOUND_THRESHOLD) {
					  
					  //temp
					  System.out.println("\nk = " + k);
					  System.out.println("Matching: " + matchingMinutiaeCount + "  ");
					  System.out.println(minutiae1.get(i)[0] + "  " + minutiae1.get(i)[1]+ "  " +rowTranslation+ "  " +colTranslation);
					  
					  return true;
				  }
				  
				  //temp
				  if (matchingMinutiaeCount > maxMatchings) {
					  maxMatchings = matchingMinutiaeCount;
				  }

			  }



		  }

	  }
	  
	  System.out.println("\nMatching: " + maxMatchings + "  ");
	  
	  return false;

  }
}
