import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

/** *************************************************************
 *		file: Shape.java
 *		author: G. Ortega-Gingrich
 *		class: CS 445 - Computer Graphics
 *
 *		assignment: program 2
 *		date last modified: 4/17/2017
 *
 *		purpose: This class describes an object that contains the
 *		information needed to draw a given polygon pixel with the
 *	   given 2d tranformations.
 *************************************************************** */
public class Polygon {
   private ArrayList<float[]> vertices;
   private float[] color;

   public Polygon(float[] color) {
      vertices = new ArrayList<>();
      this.color = new float[] {color[0], color[1], color[2]};
   }

   public void draw() {
      GL11.glBegin(GL11.GL_POINTS);

      // currently just draws the vertices
      GL11.glColor3f(color[0], color[1], color[2]);
      vertices.forEach(point -> {
         GL11.glVertex2f(point[0], point[1]);
      });

      GL11.glEnd();
   }

   public void addPoint(float x, float y) {
      vertices.add(new float[] {x,y});
   }

   public void rotate(float r, float cX, float cY) {

   }

   public void translate(float x, float y) {
      vertices.forEach((point) -> {
         point[0] += x;
         point[1] += y;
      });
   }

   public void scale(float xScale, float yScale, float centerX, float centerY) {

   }
}
