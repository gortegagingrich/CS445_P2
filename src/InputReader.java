import org.lwjgl.input.Keyboard;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/** *************************************************************
 *		file: InputReader.java
 *		author: G. Ortega-Gingrich
 *		class: CS 445 - Computer Graphics
 *
 *		assignment: program 2
 *		date last modified: 4/17/2017
 *
 *		purpose: This is a runnable class that allows for keyboard
 *		input checking to be handled in a separate thread.
 *
 *		Overview of key bindings:
 *		escape:	quit program
 *************************************************************** */
public class InputReader implements Runnable {
   private Main parent;
   private HashMap<Integer, boolean[]> keyStates;

   // constructor: InputReader()
   // purpose: sets parent object and initializes keyStates
   public InputReader(Main parent) {
      this.parent = parent;
      this.keyStates = new HashMap<>();

      // key id gets mapped to {current state, consumed}
      keyStates.put(Keyboard.KEY_ESCAPE, new boolean[] {false, false});
   }

   // method: run
   // purpose: contains loop that updates the states in keyStates and performs actions associated with those keys
   // when necessary using keyEvents() and consumeKeyEvents()
   public void run() {
      while (!parent.getExit()) {
         // update key states
         updateKeyStates();

         // perform associated events
         keyPressedEvents();
         consumeKeyEvents();
         keyReleasedEvents();

         // this should refresh fast enough not to drop any inputs with a normal keyboard
         try {
            TimeUnit.MICROSECONDS.sleep(100);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

   // method: updateKeyStates
   // purpose: updates the state in keyStates associated with each key ID
   private void updateKeyStates() {
      keyStates.forEach((keyID, state) -> {
         if (!state[0] && Keyboard.isKeyDown(keyID)) {
            state[0] = true;
            state[1] = false;
         } else if (state[0]) {
            state[0] = false;
         }
      });
   }

   // method: keyPressedEvents
   // purpose: performs actions associated with key presses that can continue to happen as long as the key is pressed
   // An example would be exiting on escape.
   private void keyPressedEvents() {
      if (keyStates.get(Keyboard.KEY_ESCAPE)[0]) {
         parent.setExit();
      }
   }

   // method: consumeKeyEvents
   // purpose: performs actions associated with key presses that should only happen once per key press
   // An example would be toggling a setting on a key press.
   private void consumeKeyEvents() {
      // none of these events yet
   }

   private void keyReleasedEvents() {
      // none of these yet
   }
}
