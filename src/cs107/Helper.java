package cs107;

import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Utility class providing simple image manipulation tools.
 * <p>
 * This class contains utilities to:
 * <ul>
 * <li>read images,</li>
 * <li>write images,</li>
 * <li>convert images,</li>
 * <li>modify images,</li>
 * <li>and, show images.</li>
 * </ul>
 */
public final class Helper {

  /**
   * The luma coefficient for red from Rec. 601 standard of ITU-R. This
   * coefficient is used to calculate a pixel's luminance from its RGB components
   */
  private final static double LUMA_COEFFICIENT_RED = 0.299;

  /**
   * The luma coefficient for green from Rec. 601 standard of ITU-R. This
   * coefficient is used to calculate a pixel's luminance from its RGB components
   */
  private final static double LUMA_COEFFICIENT_GREEN = 0.587;

  /**
   * The luma coefficient for blue from Rec. 601 standard of ITU-R. This
   * coefficient is used to calculate a pixel's luminance from its RGB components
   */
  private final static double LUMA_COEFFICIENT_BLUE = 0.114;

  /**
   * Reads specified image from the resource folder as ARGB.
   *
   * @param name Name of the image to read, or path relative to the resource
   *             folder.
   * @return HxW array of packed RGB colors, or <code>null</code> on failure
   * @see #write
   */
  public static int[][] readARGB(final String name) {
    try {
      // final BufferedImage image = ImageIO.read(Helper.class.getResource(name));
      final BufferedImage image = ImageIO.read(new File(name));
      return fromBufferedImage(image);
    } catch (final IOException e) {
      System.out.println(e + " Filename: " + name);
      return null;
    }
  }

  /**
   * Reads specified image from the resource folder as binary.
   *
   * @param name Name of the image to read, or path relative to the resource
   *             folder.
   * @return HxW array of packed binary colors, or <code>null</code> on failure
   * @see #write
   */
  public static boolean[][] readBinary(final String name) {
    final int[][] image = readARGB(name);
    return image == null ? null : toBinary(image);
  }

  /**
   * Writes specified binary image to disk.
   *
   * @param path  Output file path
   * @param array HxW array of packed binary colors
   * @return {@code true} if write operation was successful, {@code false}
   *         otherwise
   * @see #readBinary(String)
   */
  public static boolean writeBinary(final String path, final boolean[][] array) {
    return writeARGB(path, fromBinary(array));
  }

  /**
   * Writes specified image to disk.
   *
   * @param path  Output file path
   * @param array HxW array of packed RGB colors
   * @return {@code true} if write operation was successful, {@code false}
   *         otherwise
   * @see #read
   */
  public static boolean writeARGB(final String path, final int[][] array) {

    // Convert array to Java image
    final BufferedImage image = toBufferedImage(array);

    // Get desired file format
    final int index = path.lastIndexOf('.');
    if (index < 0)
      return false;
    final String extension = path.substring(index + 1);

    // Export image
    try {
      return ImageIO.write(image, extension, new File(path));
    } catch (final IOException e) {
      return false;
    }
  }

  /**
   * Convert specified BufferedImage into an array
   *
   * @param image Input image
   * @return Array
   * @see #toBufferedImage
   */
  private static int[][] fromBufferedImage(final BufferedImage image) {
    final int width = image.getWidth();
    final int height = image.getHeight();
    final int[][] array = new int[height][width];
    for (int row = 0; row < height; ++row) {
      for (int col = 0; col < width; ++col) {
        array[row][col] = image.getRGB(col, row) & 0xffffff;
      }
    }
    return array;
  }

  /**
   * Convert specified array int a BufferedImage
   *
   * @param array Input array
   * @return Buffered Image
   * @see #fromBufferedImage
   */
  private static BufferedImage toBufferedImage(final int[][] array) {
    final int width = array[0].length;
    final int height = array.length;
    final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int row = 0; row < height; ++row) {
      for (int col = 0; col < width; ++col) {
        image.setRGB(col, row, array[row][col] | 0xff000000);
      }
    }
    return image;
  }

  public static void show(final boolean[][] array, final String title) {
    int[][] image = Helper.fromBinary(array);
    show(image, title);
  }

  /**
   * Shows specified image in a window.
   *
   * @param array HxW array of packed RGB colors
   * @param title title to be displayed
   */
  public static void show(final int[][] array, final String title) {

    // Convert array to Java image
    final BufferedImage image = toBufferedImage(array);

    // Create a panel to render this image
    @SuppressWarnings("serial")
    final JPanel panel = new JPanel() {
      @Override
      protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null, null);
      }
    };

    // Create a frame to hold this panel
    final JFrame frame = new JFrame(title);
    frame.add(panel);
    frame.pack();
    frame.setSize(image.getWidth(), image.getHeight());

    // Register closing event
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(final WindowEvent e) {
        frame.setVisible(false);
        synchronized (frame) {
          frame.notifyAll();
        }
      }
    });

    // Show this frame
    frame.setVisible(true);

    // Wait for close operation
    try {
      synchronized (frame) {
        while (frame.isVisible())
          frame.wait();
      }
    } catch (final InterruptedException e) {
      // Empty on purpose
    }
    frame.dispose();
  }

  /**
   * Computes the luminance value (greyscale value) of a pixel.
   * <p>
   * This method follows the Rec. 601 standard of ITU-R and uses the following
   * formula: {@link #LUMA_COEFFICIENT_RED} * Red +
   * {@link #LUMA_COEFFICIENT_GREEN} * Green + {@link #LUMA_COEFFICIENT_BLUE} *
   * Blue.
   *
   * @param pixel the pixel's ARGB value as an int.
   * @return The luminance.
   */
  private static int pixelLuminance(final int pixel) {
    final int red = (pixel >> 16) & 0xFF;
    final int green = (pixel >> 8) & 0xFF;
    final int blue = pixel & 0xFF;
    return (int) Math.round(LUMA_COEFFICIENT_RED * red + LUMA_COEFFICIENT_GREEN * green + LUMA_COEFFICIENT_BLUE * blue);
  }

  /**
   * Converts an ARGB image to binary by:
   * <ul>
   * <li>computing each pixel's luminance value using
   * {@link #pixelLuminance(int[][])}</li>
   * <li>if the luminance is strictly below <code>128</code>, the pixel will be
   * set to <code>1</code>, that is, <code>true</code>, otherwise it will be set
   * to <code>false</code></li>
   * </ul>
   *
   * @param image array containing each pixel's ARGB value as an int.
   * @return Array containing each pixel's as a boolean value. Dark pixels are
   *         represented by <code>true</code> and white pixels are represented by
   *         <code>false</code>.
   */
  public static boolean[][] toBinary(final int[][] image) {
    final boolean[][] result = new boolean[image.length][image[0].length];
    for (int i = 0; i < result.length; i++) {
      for (int j = 0; j < result[i].length; j++) {
        result[i][j] = pixelLuminance(image[i][j]) < 128;
      }
    }
    return result;
  }

  /**
   * Converts a binary image to an ARBG image.
   *
   * @param image array containing each pixel's binary value as a boolean.
   * @return Array containing each pixel's ARGB value as an int. <code>True</code>
   *         is black while <code>false</code> is white.
   */
  public static int[][] fromBinary(final boolean[][] image) {
    final int[][] result = new int[image.length][image[0].length];
    for (int i = 0; i < result.length; i++) {
      for (int j = 0; j < result[i].length; j++) {
        result[i][j] = image[i][j] ? 0xFF000000 : 0xFFFFFFFF;
      }
    }
    return result;
  }

  /**
   * Draws a line on an image.
   *
   * @param image       array containing each pixel's ARGB value as an int.
   * @param rowStart    the starting coordinate of the line.
   * @param colStart    the starting coordinate of the line.
   * @param orientation the angle of the line.
   * @param length      the length of the line.
   * @param color       the color of the line.
   */
  public static void addLine(final int[][] image, final int rowStart, final int colStart, final double orientation,
      final int length, final int color) {
    final int rowEnd = (int) Math.round(rowStart - length * Math.sin(orientation));
    final int colEnd = (int) Math.round(colStart + length * Math.cos(orientation));

    final int dCol = colEnd - colStart;
    final int dRow = rowEnd - rowStart;

    final int sCol = dCol < 0 ? -1 : 1;
    final int sRow = dRow < 0 ? -1 : 1;

    int row = rowStart;
    int col = colStart;

    if (Math.abs(dRow) <= Math.abs(dCol)) {
      final double slope = (double) dRow / dCol;
      final double pitch = rowStart - slope * colStart;
      while (col != colEnd) {
        final int r = (int) Math.round(slope * col + pitch);
        if (0 <= r && r < image.length && 0 <= col && col < image[0].length) {
          image[r][col] = color;
        }
        col += sCol;
      }
    } else {
      final double slope = (double) dCol / dRow;
      final double pitch = colStart - slope * rowStart;
      while (row != rowEnd) {
        final int c = (int) Math.round(slope * row + pitch);
        if (0 <= row && row < image.length && 0 <= c && c < image[0].length) {
          image[row][c] = color;
        }
        row += sRow;
      }
    }

    if (0 <= rowEnd && rowEnd < image.length && 0 <= colEnd && colEnd < image[0].length) {
      image[rowEnd][colEnd] = color;
    }
  }

  /**
   * Draws a circle on the image.
   *
   * @param image     array containing each pixel's ARGB value as an int.
   * @param rowCenter the center coordinate of the circle.
   * @param colCenter the center coordinate of the circle.
   * @param radius    the radius of the circle.
   * @param color     the color of the line.
   */
  public static void addCircle(final int[][] image, final int rowCenter, final int colCenter, final int radius,
      final int color) {
    for (int i = 0; i < 360; i++) {
      final int row = rowCenter + (int) Math.round(radius * Math.sin(Math.toRadians(i)));
      final int col = colCenter + (int) Math.round(radius * Math.cos(Math.toRadians(i)));
      if (0 <= row && row < image.length && 0 <= col && col < image[row].length) {
        image[row][col] = color;
      }
    }
  }

  /**
   * Draws the minutae on the image.
   *
   * @param image   array containing each pixel's ARGB value.
   * @param minutia the list minutiae.
   */
  public static void drawMinutia(final int[][] image, final List<int[]> minutia) {
    for (final int[] minutiae : minutia) {
      addCircle(image, minutiae[0], minutiae[1], 5, 0xFFFF0000);
      addLine(image, minutiae[0], minutiae[1], Math.toRadians(minutiae[2]), 8, 0xFFFF0000);
    }
  }
}
