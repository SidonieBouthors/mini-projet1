# Mini Projet 1
Mini Projet 1 - BA1 - Sidonie & François

Welcome to our code. 

As you can see some methods have been added to the code :
- The method `contains(ArrayList<int[]> list, int[] element)` aims to search an int[] in an ArrayList of int[]
- Several test methods have been added (main.java) :
  - `testCompareAllToFirst()` that compares each first image of each fingerprints to the others (1_1 with all other 1_n, 2_1 to all other 2_n ...)
  - `testCompareAllTo(String image)` that compares the image (name passed in argument) with all other images (usefull for testing 1_1, 1_2 and 1_5 with all others as adviced)

The method "connectedPixels" has been made in a different way than the one proposed in the notice. 
We iterate over neighbours of the current pixel (starting with the minutia) and if they correspond to the criteria, add them to both the final image and a list of coordinates. 
We repeat the same process with the elements of the list of coordinates, each time adding their neighbours. This way we continue finding neighbors of pixels until we reach the end of the list (no more neighbour pixels within distance to check).
This method is more efficient because we do not iterate over every pixel in the image, or even every pixel within distance, but only the pixels we are actually interested in.

Our boolean results for the comparisons of 1_1, 1_2 and 1_5 with all others correspond exactly to the expected results as given on Moodle.

We have chosen not to correct the minutia used as center of rotation issue, therefore keeping m1 as center of rotation.
