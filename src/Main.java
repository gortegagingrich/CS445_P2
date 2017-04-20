import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private int screenWidth, screenHeight, frameRate;
    private float originX, originY;
    private ArrayList<Polygon> polygons;
    private volatile boolean shouldExit;

    private static final String CAPTION = "Program 2";

    public Main(int width, int height, float oX, float oY, int frameRate) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.frameRate = frameRate;
        this.originX = oX;
        this.originY = oY;
        this.shouldExit = false;
        this.polygons = new ArrayList<>();

        readCoordinateFile();
    }

    public void setExit() {
       shouldExit = true;
    }

    public boolean getExit() {
       return shouldExit;
    }

    public void start() throws LWJGLException {
       Display.setFullscreen(false);

       Display.setDisplayMode(new DisplayMode(screenWidth, screenHeight));
       Display.setTitle(CAPTION);
       Display.create();

       inputInit();
       glInit();
       render();
    }

    private void glInit() {
       GL11.glClearColor(0,0,0,0);
       GL11.glMatrixMode(GL11.GL_PROJECTION);
       GL11.glLoadIdentity();
       GL11.glOrtho(originX,originX+screenWidth, originY,originY+screenHeight,1,-1);
       GL11.glMatrixMode(GL11.GL_MODELVIEW);
       GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
    }

    private void inputInit() {
      Thread thread = new Thread(new InputReader(this));
      thread.start();
    }

    private void render() {

       while (!shouldExit && !Display.isCloseRequested()) {
          GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
          GL11.glLoadIdentity();

          // insert render stuff
          polygons.forEach(polygon -> {
             polygon.draw();
          });

          Display.update();
          Display.sync(frameRate);
       }

       shouldExit = true;
       Display.destroy();
    }

    private void readCoordinateFile() {
       String line;
       String[] lineParts;
       Polygon poly;
       File file;
       Scanner scan;

       scan = null;
       poly = null;

       try {
          file = new File("coordinates.txt");
          scan = new Scanner(file);

          while (scan.hasNext()) {
             line = scan.nextLine();

             if (line.length() > 0) {
                switch (line.charAt(0)) {
                   case 'T':
                      // just to make sure it doesn't try to parse this as a point to be added
                      break;
                   case 'P': // poly = new polygon
                      if (poly != null) {
                         polygons.add(poly);
                      }

                      lineParts = line.split(" ");
                      poly = new Polygon(new float[] {
                              Float.parseFloat(lineParts[1]),
                              Float.parseFloat(lineParts[2]),
                              Float.parseFloat(lineParts[3])});
                      break;
                   case 't': // translate polygon
                      lineParts = line.split(" ");
                      poly.translate(Float.parseFloat(lineParts[1]),
                              Float.parseFloat(lineParts[2]));
                      break;
                   case 'r': // rotate polygon
                      lineParts = line.split(" ");
                      poly.rotate(Math.toRadians(Double.parseDouble(lineParts[1])),
                             Float.parseFloat(lineParts[2]),
                             Float.parseFloat(lineParts[3]));
                      break;
                   case 's': // scale polygon
                      lineParts = line.split(" ");
                      poly.scale(Float.parseFloat(lineParts[1]),
                              Float.parseFloat(lineParts[2]),
                              Float.parseFloat(lineParts[3]),
                              Float.parseFloat(lineParts[4]));
                      break;
                   default:  // add point to polygon
                      lineParts = line.split(" ");
                      poly.addPoint(Float.parseFloat(lineParts[0]),
                              Float.parseFloat(lineParts[1]));
                }
             }
          }

          if (poly != null) {
             polygons.add(poly);
          }
       } catch (FileNotFoundException e) {
          e.printStackTrace();
       } finally {
          if (scan != null) {
             scan.close();
          }
       }
    }

    public static void main(String[] args) {
	// write your code here
      Main main = new Main(640,480,-320,-240,30);
       try {
          main.start();
       } catch (LWJGLException e) {
          e.printStackTrace();
       }
    }
}
