import org.lwjgl.opengl.GL11;
import java.util.ArrayList;

/** *************************************************************
 *		file: Shape.java
 *		author: G. Ortega-Gingrich
 *		class: CS 445 - Computer Graphics
 *
 *		assignment: program 2
 *		date last modified: 4/20/2017
 *
 *		purpose: This class describes an object that contains the
 *		information needed to draw a given polygon pixel with the
 *	   given 2d tranformations.
 *	   These store a set of vertices and only perform the rotations
 *	   once.
 *************************************************************** */
public class Polygon {
   private ArrayList<float[]> vertices;
   private float[] color;

   // constructor: Polygon(float[])
   // purpose: initizlize ArrayList for vertices and set color
   public Polygon(float[] color) {
      vertices = new ArrayList<>();
      this.color = new float[] {color[0], color[1], color[2]};
   }

   // method: draw
   // purpose: draws the filled polygon using a scanline algorithm
   public void draw() {
      GL11.glBegin(GL11.GL_POINTS);
      GL11.glColor3f(color[0], color[1], color[2]);

      float xMin, yMin, yMax, xCurrent, mInverse, scanLine;
      float[] point0, point1;
      boolean parity;
      int i;
      ArrayList<float[]> allEdges;
      ArrayList<float[]> activeEdgeTable;
      ArrayList<float[]> globalEdgeTable;
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
            mInverse = (point1[0] - point0[0]) / (point1[1] - point0[1]);
         } else {
            mInverse = Float.MAX_VALUE;
         }

         allEdges.add(new float[] {yMin, yMax, xMin, mInverse});
      }

      // initialize globalEdgeTable
      globalEdgeTable = new ArrayList<>();

      for (float[] p : allEdges) {
         if (Math.abs(p[3]) < 196608) {
            globalEdgeTable.add(p);
         }
      }

      globalEdgeTable.sort( (edge0, edge1) -> {
         if (edge0[0] > edge1[0]) {
            return 1;
         } else if (edge0[0] < edge1[0]) {
            return -1;
         } else if (edge0[2] > edge1[2]) {
            return 1;
         } else {
            return -1;
         }
      });

      // initialize parity, scan line, and active edge
      scanLine = globalEdgeTable.get(0)[0];
      activeEdgeTable.clear();

      for (float[] pos: globalEdgeTable) {
         if (pos[0] <= scanLine) {
            activeEdgeTable.add(pos);
         }
      }

      while (!activeEdgeTable.isEmpty()) {
         activeEdgeTable.sort((edge0, edge1) -> {
            if (edge0[2] >= edge1[2]) {
               return 1;
            } else {
               return -1;
            }
         });

         xCurrent = activeEdgeTable.get(0)[2];
         parity = false;

         while (!activeEdgeTable.isEmpty()) {
            if (parity) {
               GL11.glVertex2f(xCurrent, scanLine);
            }

            xCurrent += 1;

            if (xCurrent > activeEdgeTable.get(0)[2]) {
               activeEdgeTable.remove(0);
               parity = !parity;
            }
         }

         scanLine += 1;
         activeEdgeTable.clear();

         for (float[] pos: globalEdgeTable) {
            if (pos[0] <= scanLine && pos[1] >= scanLine) {
               pos[2] += pos[3];
               activeEdgeTable.add(pos);
            }
         }
      }

      GL11.glEnd();
   }

   // method: addPoint
   // purpose: adds a vertex to the polygon
   public void addPoint(float x, float y) {
      vertices.add(new float[] {x,y});
   }

   // method: rotate
   // purpose: rotates all vertices a given amount of radians about a given point
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

   // method: translate
   // purpose: moves the polygon horizontally and vertically by the given amounts
   public void translate(float x, float y) {
      vertices.forEach(point -> {
         point[0] += x;
         point[1] += y;
      });
   }

   // method: scale
   // purpose: scales the polygon with the given center point
   public void scale(float xScale, float yScale, float centerX, float centerY) {
      translate(-centerX, -centerY);

      vertices.forEach(pos -> {
         pos[0] *= xScale;
         pos[1] *= yScale;
      });

      translate(centerX, centerY);
   }
}
