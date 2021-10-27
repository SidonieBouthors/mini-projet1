public class enCours {
    public static void main(String[] args) {
        
    }


    public static boolean[] getNeighbours(boolean[][] image, int row, int col) {
        assert (image != null);
        boolean[] returningTab = new boolean[8]; //Le tableau final retourné qui getNeighbours

        if (col + 1 <= 99 && row + 1 <= 99 && col - 1 >= 0 && row - 1 >= 0) {
            returningTab[0] = image[row - 1][col];
            returningTab[6] = image[row][col - 1];
            returningTab[7] = image[row - 1][col-1];
            returningTab[4] = image[row + 1][col];      //si le coin superieur gauche et le coin inferieur droit sont libres alors tout est libre
            returningTab[3] = image[row + 1][col + 1];  //donc on run seuleument lui. Ce qui devrait fonctionner dans la plupart des cas.Cela me semble un peu plus opti
            returningTab[1] = image[row - 1][col+1];
            returningTab[2] = image[row][col+1];
            returningTab[5] = image[row - 1][col-1];
            return returningTab;
        }

        if (row - 1 >= 0) {                         // Check d'indice Au milieu en haut / tab indice 0
            returningTab[0] = image[row - 1][col];
        }
        if (col - 1 >= 0) {                         // Check d'indice Au milieu, a gauche / tab indice 6
            returningTab[6] = image[row][col - 1];
        }
        if (col - 1 >= 0 && row - 1 >= 0) {         // Check d'indice En haut a gauche / tab indice 7
            returningTab[7] = image[row - 1][col-1];
        }
        if (row + 1 <= 99) {                        // Check d'indice Au milieu en bas / tab indice 4
            returningTab[4] = image[row + 1][col];
        }
        if (col + 1 <= 99 && row + 1 <= 99) {       // Check d'indice En bas a droite / tab indice 3
            returningTab[3] = image[row + 1][col + 1];
        }
        if (row - 1 >= 0 && col + 1 <= 99) {        // Check d'indice En haut a droite / tab indice 1
            returningTab[1] = image[row - 1][col+1];
        }
        if (col + 1 <= 99) {                        // Check d'indice Au milieu a droite / tab indice 2
            returningTab[2] = image[row][col+1];
        }
        if (col - 1 >= 0 && row + 1 <= 99) {        // Check d'indice En bas a gauche / tab indice 5
            returningTab[5] = image[row - 1][col-1];
        }
        return returningTab;                        // Return le tableau avec toutes les exceptions possibles
    }

}


