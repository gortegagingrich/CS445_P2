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

   private static boolean printed = false;

   public Polygon(float[] color) {
      vertices = new ArrayList<>();
      this.color = new float[] {color[0], color[1], color[2]};
   }

   public void draw() {
      GL11.glBegin(GL11.GL_POINTS);

      float xMin, scanLine, yMin, yMax, xMax, mInverse;
      float[] point0, point1;
      boolean parity;
      int i;
      ArrayList<float[]> allEdges;
      ArrayList<float[]> activeEdgeTable;
      ArrayList<float[]> globalEdgeTable;

      scanLine = Float.MAX_VALUE;
      yMax = Float.MAX_VALUE;
      allEdges = new ArrayList<>();
      activeEdgeTable = new ArrayList<>();

      // initialize allEdges
      // {yMin, yMax, xOfYMin, 1/m}
      for (i = 0; i < vertices.size(); i++) {
         point0 = vertices.get(i);
         point1 = vertices.get((i + 1)%vertices.size());

         // if slope != 0
         if (point0[1] < point1[1]) {
            yMin = point0[1];
            yMax = point1[1];
            xMin = point0[0];
         } else {
            yMin = point1[1];
            yMax = point0[1];
            xMin = point1[0];
         }

         if (point0[1] != point1[1]) {
            mInverse = (point1[0] - point0[0]) / (point1[1] - point1[0]);
         } else {
            mInverse = Float.MAX_VALUE;
         }

         allEdges.add(new float[] {yMin, yMax, xMin, mInverse});
      }

      // initialize globalEdgeTable
      globalEdgeTable = new ArrayList<>();
      allEdges.forEach(p -> {
         if (p[3] < Float.MAX_VALUE) {
            globalEdgeTable.add(p);
         }
      });
      globalEdgeTable.sort( (edge0, edge1) -> {
         float diff;

         // will break with very large polygons, but those most likely won't be used
         diff = (edge0[0] - edge1[0]) * 100000 + (edge0[2] - edge1[2]);

         return (int)diff;
      });

      // initialize parity, scan line, and active edge
      parity = false; // false = even, true = odd
      scanLine = globalEdgeTable.get(0)[0];
      activeEdgeTable.clear();

      for (float[] pos: globalEdgeTable) {
         if (pos[0] == scanLine) {
            activeEdgeTable.add(pos);
         }
      }

      if (!printed) {
         allEdges.forEach(point -> {
            System.out.printf("{%f, %f, %f, %f}\n", point[0], point[1], point[2], point[3]);
         });

         System.out.println();

         globalEdgeTable.forEach(point -> {
            System.out.printf("{%f, %f, %f, %f}\n", point[0], point[1], point[2], point[3]);
         });

         System.out.println();

         activeEdgeTable.forEach( point -> {
            System.out.printf("{%f, %f, %f, %f}\n", point[0], point[1], point[2], point[3]);
         });

         printed = true;
      }

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

   public void rotate(double theta, float centerX, float centerY) {
      double x2, y2;

      translate(-centerX, -centerY);

      for (float[] pos: vertices) {
         x2 = (pos[0]) * Math.cos(theta) - (pos[1]) * Math.sin(theta);
         y2 = (pos[0]) * Math.sin(theta) + (pos[1]) * Math.cos(theta);

         pos[0] = (float)x2;
         pos[1] = (float)y2;
      }

      translate(centerX, centerY);
   }

   public void translate(float x, float y) {
      vertices.forEach(point -> {
         point[0] += x;
         point[1] += y;
      });
   }

   public void scale(float xScale, float yScale, float centerX, float centerY) {

      translate(-centerX, -centerY);

      vertices.forEach(pos -> {
         pos[0] *= xScale;
         pos[1] *= yScale;
      });

      translate(centerX, centerY);
   }
}
