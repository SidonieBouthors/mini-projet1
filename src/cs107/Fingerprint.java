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
	  
	  assert (neighbours.length == 8);
	  
	  int numberBlackNeighbours = 0;
	  
	  for (int i = 0; i<8; ++i) {
		  if (neighbours[i] == true){
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
	  
	  assert (neighbours.length == 8);
	  
	  int numberTransitions = 0;
	  
	  // Dealing with case of transition 7-0
	  if (neighbours[7]==false && neighbours[0]==true) {
		  numberTransitions += 1;
	  }
	  
	  //Dealing with all other transition cases 0-1 --> 6-7
	  for (int i = 0; i<7; ++i) {
		  if (neighbours[i]==false && neighbours[i+1]==true) {
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
	  
	  //Dealing with images with different amount of rows
	  if (image1.length != image2.length) {
		  return false; //different amounts of rows = not identical
	  }
	  
	  for (int i = 0; i < image1.length; ++i) {
		  
		  //Dealing with images with different amounts of columns
		  if (image1[i].length != image2[i].length) {
			  return false; //different amount of sub-rows (columns) = not identical
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
	  //image assumed rectangular with at least 1 line
	  
	  //Array of pixel with no neighbours (to test with value of getNeighbours)
	  boolean[] noNeighbours = new boolean[8];
	  
	  //New array to store thinned version of image
	  boolean[][] imageCopy = new boolean[image.length][image[0].length];
	  
	  //double iteration to iterate over every pixel of image
	  for (int i = 0; i < image.length; ++i) {
		  for (int j = 0; j < image[i].length; ++j) {
			  
			  //Computing neighbours and amount of black neighbours for the current pixel
			  boolean[] neighbours = getNeighbours(image, i, j);
			  int blackNeighbours = blackNeighbours(neighbours);
			  
			  //Checking for conditions that are common to both steps
			  if (image[i][j]
				&& Arrays.equals(neighbours, noNeighbours)
				&& 2 <= blackNeighbours && blackNeighbours <= 6
				&& transitions(neighbours) == 1) {
				  
				  //conditions particular to step 0
				  if (step == 0
					&& ((!neighbours[2] || !neighbours[4]) 
						|| (!neighbours[0] && !neighbours[6]))) {
					  
					  imageCopy[i][j] = false; //black pixel not copied over
				  }
				  
				  //conditions particular to step 1
				  if (step == 1
					&& ((!neighbours[0] || !neighbours[6]) 
						|| (!neighbours[2] && !neighbours[4]))) {
							
					  imageCopy[i][j] = false; //black pixel not copied over
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
	  //image is assumed to always be a rectangle and contain at least 1 row
	  
	  //Copy of image created
	  boolean[][] imageCopy = new boolean[image.length][image[0].length];
	  for (int i = 0; i < image.length; ++i) {
		  for (int j = 0; j < image[0].length; ++j) {
			  imageCopy[i][j] = image[i][j];
		  }
	  }
	  
	  while ( !identical(imageCopy, thinningStep(thinningStep(imageCopy, 0),1))) {
		  imageCopy = thinningStep(thinningStep(imageCopy, 0),1);
	  }
	  
	  return imageCopy;
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
   *         <code>distance</code> and connected to the pixel at
   *         <code>(row, col)</code>.
   */
  public static boolean[][] connectedPixels(boolean[][] image, int row, int col, int distance) {

      // Tableau de fin de meme taille que le tableau initial comme précisé dans l'énoncé
      boolean[][] returningTab = new boolean[image.length][image[0].length];

      // Coordonnées potentiellement utilisable qui part de [ligne-distance][col-distance] jusqu'a [ligne+distance] [col+distance]
      int coordonneeXDepart=row-distance, coordonneeXFin=row+distance,coordonneeYDepart=col-distance,coordonneeYFin=col+distance;

      // Si on prend la distance tout autour de image [row] [col], il faut que toute les indices des cases dans ce carré appartiennent au tableau.
      // Ou si ca depasse les indices du tableau, on réajuste au maximum/minimum du tableau afin de ne  pas avoir index out of bound (on a le droit de le faire car on sait que tout ce qui n'est pas dans l'image est false/blanc).

      if (row - distance < 0) {
          coordonneeYDepart = 0;
      } else if (row + distance > image[0].length) {
          coordonneeYFin = image[0].length;         // Désigne la taille y du tableau mais selon les conditions de la boucle on doit faire -1 ou pas.
      }

      if (col - distance < 0) {
          coordonneeXDepart = 0;
      } else if (col + distance > image.length) {
          coordonneeXFin = image.length;            // Désigne la taille x du tableau mais selon les conditions de la boucle on doit faire -1 ou pas.
      }

	  return null;
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
	  //TODO implement
	  return 0;
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
	  //TODO implement
	  return 0;
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
	  //TODO implement
	  return 0;
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
	  //TODO implement
	  return null;
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
	  //TODO implement
	  return null;
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
	  //TODO implement
	  return null;
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
	  //TODO implement
	  return null;
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
  public static List<int[]> applyTransformation(List<int[]> minutiae, int centerRow, int centerCol, int rowTranslation,
      int colTranslation, int rotation) {
	  //TODO implement
	  return null;
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
  public static int matchingMinutiaeCount(List<int[]> minutiae1, List<int[]> minutiae2, int maxDistance,
      int maxOrientation) {
	  //TODO implement
	  return 0;
  }

  /**
   * Compares the minutiae from two fingerprints.
   *
   * @param minutiae1 the list of minutiae of the first fingerprint.
   * @param minutiae2 the list of minutiae of the second fingerprint.
   * @return Returns <code>true</code> if they match and <code>false</code>
   *         otherwise.
   */
  public static boolean match(List<int[]> minutiae1, List<int[]> minutiae2) {
	  //TODO implement
	  return false;
  }
}
