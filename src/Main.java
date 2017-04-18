import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class Main {
    private int screenWidth, screenHeight, frameRate;
    private float originX, originY;
    private volatile boolean shouldExit;

    private static final String CAPTION = "Program 2";

    public Main(int width, int height, float oX, float oY, int frameRate) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.frameRate = frameRate;
        this.originX = oX;
        this.originY = oY;
        this.shouldExit = false;
    }

    public synchronized void setExit() {
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

          Display.update();
          Display.sync(frameRate);
       }

       shouldExit = true;
       Display.destroy();
    }

    public static void main(String[] args) {
	// write your code here
      Main main = new Main(640,480,0,0,60);
       try {
          main.start();
       } catch (LWJGLException e) {
          e.printStackTrace();
       }
    }
}
