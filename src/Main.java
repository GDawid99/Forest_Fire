//Pożar lasu

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Main extends Application {

    public static final int N_WIDTH = 600;
    public static final int N_HEIGHT = 330;
    public static final Wind wind = Wind.NO_WIND; //kierunek wiatru

    public static int[][] matrix;

    public static GridPane gridPane;
    public static Scene scene;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(N_WIDTH,N_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        matrix = new int[N_HEIGHT][N_WIDTH]; // 0 - żywe drzewo, 1 - płonące drzewo, 2 - spalone drzewo, 3 - woda

        matrix = readFromFile(matrix);

        matrix = modifyMap(matrix);
        drawCells(matrix,gc);

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                try {
                    Thread.sleep(50);
                    matrix = forestFire(matrix);
                    drawCells(matrix,gc);
                } catch (InterruptedException  | IOException e) {
                    e.printStackTrace();
                }
            }
        };

        animationTimer.start();

        gridPane = new GridPane();
        gridPane.setPrefSize(N_WIDTH,N_HEIGHT);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(canvas, 0, 0);

        scene = new Scene(gridPane, N_WIDTH, N_HEIGHT);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Pożar lasu, Kierunek wiatru: " + wind.toString());
        stage.show();
    }

    public static void drawCells(int [][] m, GraphicsContext gc) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                if (m[i][j] == 0) {
                    Color color = Color.GREEN;
                    gc.getPixelWriter().setColor(j, i, color);
                }
                else if (m[i][j] == 1) {
                    Color color = Color.RED;
                    gc.getPixelWriter().setColor(j, i, color);
                }
                else if (m[i][j] == 2) {
                    Color color = Color.BLACK;
                    gc.getPixelWriter().setColor(j, i, color);
                }
                else {
                    Color color = Color.AQUA;
                    gc.getPixelWriter().setColor(j, i, color);
                }
            }
        }

    }

    public static int[][] forestFire(int[][] m) throws InterruptedException, IOException {
        int[][] tempMatrix = new int[N_HEIGHT][N_WIDTH];
        int[] neighbours = new int[8];
        final int iMAX = m.length-1;
        final int jMAX = m[0].length-1;

        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                if (i == 0) {
                    if (j == 0) {
                        int countTrue = 0;
                        neighbours[4] = m[i][j+1];
                        neighbours[6] = m[i + 1][j];
                        neighbours[7] = m[i + 1][j + 1];
                        for (int k = 0; k < 8; k++) {
                            if (neighbours[k] == 1) countTrue++;
                        }
                        checkConditions(m,tempMatrix,countTrue,i,j,wind);
                    }
                    else if (j == jMAX) {
                        int countTrue = 0;
                        neighbours[3] = m[i][j-1];
                        neighbours[5] = m[i+1][j-1];
                        neighbours[6] = m[i+1][j];
                        for (int k = 0; k < 8; k++) {
                            if (neighbours[k] == 1) countTrue++;
                        }
                        checkConditions(m,tempMatrix,countTrue,i,j,wind);
                    }
                    else {
                        int countTrue = 0;
                        neighbours[3] = m[i][j-1];
                        neighbours[4] = m[i][j+1];
                        neighbours[5] = m[i + 1][j-1];
                        neighbours[6] = m[i + 1][j];
                        neighbours[7] = m[i + 1][j+1];
                        for (int k = 0; k < 8; k++) {
                            if (neighbours[k] == 1) countTrue++;
                        }
                        checkConditions(m,tempMatrix,countTrue,i,j,wind);
                    }
                }
                else if (i == iMAX) {
                    if (j == 0) {
                        int countTrue = 0;
                        neighbours[1] = m[i-1][j];
                        neighbours[2] = m[i-1][j+1];
                        neighbours[4] = m[i][j+1];
                        for (int k = 0; k < 8; k++) {
                            if (neighbours[k] == 1) countTrue++;
                        }
                        checkConditions(m,tempMatrix,countTrue,i,j,wind);
                    }
                    else if (j == jMAX) {
                        int countTrue = 0;
                        neighbours[0] = m[i-1][j-1];
                        neighbours[1] = m[i-1][j];
                        neighbours[3] = m[i][j-1];
                        for (int k = 0; k < 8; k++) {
                            if (neighbours[k] == 1) countTrue++;
                        }
                        checkConditions(m,tempMatrix,countTrue,i,j,wind);
                    }
                    else {
                        int countTrue = 0;
                        neighbours[0] = m[i-1][j-1];
                        neighbours[1] = m[i-1][j];
                        neighbours[2] = m[i-1][j+1];
                        neighbours[3] = m[i][j-1];
                        neighbours[4] = m[i][j+1];
                        for (int k = 0; k < 8; k++) {
                            if (neighbours[k] == 1) countTrue++;
                        }
                        checkConditions(m,tempMatrix,countTrue,i,j,wind);
                    }
                }
                else if (j == 0) {
                    int countTrue = 0;
                    neighbours[1] = m[i-1][j];
                    neighbours[2] = m[i-1][j+1];
                    neighbours[4] = m[i][j+1];
                    neighbours[6] = m[i+1][j];
                    neighbours[7] = m[i+1][j+1];
                    for (int k = 0; k < 8; k++) {
                        if (neighbours[k] == 1) countTrue++;
                    }
                    checkConditions(m,tempMatrix,countTrue,i,j,wind);
                }
                else if (j == jMAX) {
                    int countTrue = 0;
                    neighbours[0] = m[i-1][j-1];
                    neighbours[1] = m[i-1][j];
                    neighbours[3] = m[i][j-1];
                    neighbours[5] = m[i+1][j-1];
                    neighbours[6] = m[i+1][j];
                    for (int k = 0; k < 8; k++) {
                        if (neighbours[k] == 1) countTrue++;
                    }
                    checkConditions(m,tempMatrix,countTrue,i,j,wind);
                }
                else {
                    int countTrue = 0;
                    neighbours[0] = m[i - 1][j - 1];
                    neighbours[1] = m[i - 1][j];
                    neighbours[2] = m[i - 1][j + 1];
                    neighbours[3] = m[i][j - 1];
                    neighbours[4] = m[i][j + 1];
                    neighbours[5] = m[i + 1][j - 1];
                    neighbours[6] = m[i + 1][j];
                    neighbours[7] = m[i + 1][j + 1];
                    for (int k = 0; k < 8; k++) {
                        if (neighbours[k] == 1) countTrue++; //jeśli którykolwiek sąsiad się pali

                    }
                    checkConditions(m,tempMatrix,countTrue,i,j,wind);
                }
            }
        }
        return tempMatrix;
    }

    public static int[][] readFromFile(int[][] m) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("resources/mapa.txt"));
            String line;
            int j = 0;
            while((line = br.readLine()) != null){
                String[] values = line.split("\t");
                for (int i = 0; i < values.length; i++) {
                    m[j][i] = Integer.parseInt(values[i]);
                }
                j++;
            }
            return m;
        } catch( IOException e) {
            System.err.println("THROW EXCEPTION: file not found");
            return m;
        }
    }

    public static int[][] modifyMap(int [][] m) {
        int [] neighbours = new int[8];
        int [][] tempM = new int[N_HEIGHT][N_WIDTH];
        for (int i = 1; i < m.length-1; i++) {
            for (int j = 1; j < m[i].length-1; j++) {
                int countTrue = 0;
                neighbours[0] = m[i - 1][j - 1];
                neighbours[1] = m[i - 1][j];
                neighbours[2] = m[i - 1][j + 1];
                neighbours[3] = m[i][j - 1];
                neighbours[4] = m[i][j + 1];
                neighbours[5] = m[i + 1][j - 1];
                neighbours[6] = m[i + 1][j];
                neighbours[7] = m[i + 1][j + 1];
                for (int k = 0; k < 8; k++) {
                    if (neighbours[k] == 216) countTrue++; //jeśli którykolwiek sąsiad jest wodą
                }
                if (m[i][j] == 216 && countTrue > 3) tempM[i][j] = 3; //woda
                else tempM[i][j] = 2; //drzewo
            }
        }
        return tempM;
    }

    public static void checkConditions(int[][] m, int[][] tempMatrix, int countTrue, int i, int j, Wind wind) {
        int change;
        if (i != 0 && i != m.length-1 && j != 0 && j != m[i].length-1) change = setChance(wind, m, i, j);
        else {
            if (wind.equals(Wind.NO_WIND)) change = 5000;
            else change = 2000;
        }
        Random r = new Random();
        if (countTrue > 0 && m[i][j] == 0) {                                   //jeśli sąsiad się pali i drzewo żyje to prawdopodobnie
            if (r.nextInt(10000) < change) tempMatrix[i][j] = 1;          //zapali się
            else tempMatrix[i][j] = m[i][j];                                    //nie zapali się
        }
        else if (countTrue == 0 && m[i][j] == 0) {                              //jeśli sąsiad się nie pali i drzewo żyje to prawdopodobnie
            if (r.nextInt(100000) < 1) tempMatrix[i][j] = 1;                //dokona się samozapłon
            else tempMatrix[i][j] = m[i][j];                                    //nie dokona się samozapłon
        }
        else if (countTrue >= 0 && m[i][j] == 1) {                              //jeśli drzewo się pali
            tempMatrix[i][j] = 2;                                               //drzewo staje się spalone
        }
        else if (countTrue >= 0 && m[i][j] == 2) {                              //jeśli drzewo jest spalone to prawdopodobnie
            if (r.nextInt(10000) < 40) tempMatrix[i][j] = 0;                //drzewo odrośnie
            else tempMatrix[i][j] = m[i][j];                                    //drzewo nie odrośnie
        }
        else {
            tempMatrix[i][j] = m[i][j];                                         //nic się nie zmienia
        }
    }

    public static int setChance(Wind wind, int[][] m, int i, int j) {
        final int changeTrue = 7000;
        final int changeFalse = 3000;

        if (wind.equals(Wind.NORTH)) {
            if (m[i-1][j-1] == 1 || m[i-1][j] == 1 || m[i-1][j+1] == 1) {
                return changeTrue;
            }
            else return changeFalse;
        }
        else if (wind.equals(Wind.SOUTH)) {
            if (m[i+1][j-1] == 1 || m[i+1][j] == 1 || m[i+1][j+1] == 1) {
                return changeTrue;
            }
            else return changeFalse;
        }
        else if (wind.equals(Wind.WEST)) {
            if (m[i-1][j-1] == 1 || m[i][j-1] == 1 || m[i+1][j-1] == 1) {
                return changeTrue;
            }
            else return changeFalse;
        }
        else if (wind.equals(Wind.EAST)) {
            if (m[i-1][j+1] == 1 || m[i][j+1] == 1 || m[i+1][j+1] == 1) {
                return changeTrue;
            }
            else return changeFalse;
        }
        else if (wind.equals(Wind.NORTH_WEST)) {
            if (m[i-1][j-1] == 1 || m[i][j-1] == 1 || m[i-1][j] == 1) {
                return changeTrue;
            }
            else return changeFalse;
        }
        else if (wind.equals(Wind.NORTH_EAST)) {
            if (m[i-1][j] == 1 || m[i-1][j+1] == 1 || m[i][j+1] == 1) {
                return changeTrue;
            }
            else return changeFalse;
        }
        if (wind.equals(Wind.SOUTH_WEST)) {
            if (m[i][j-1] == 1 || m[i+1][j-1] == 1 || m[i+1][j] == 1) {
                return changeTrue;
            }
            else return changeFalse;
        }
        else if (wind.equals(Wind.SOUTH_EAST)) {
            if (m[i+1][j+1] == 1 || m[i][j+1] == 1 || m[i+1][j] == 1) {
                return changeTrue;
            }
            else return changeFalse;
        }
        else {
            return 5000;
        }
    }

}


