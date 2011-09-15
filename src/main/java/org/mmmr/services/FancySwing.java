package org.mmmr.services;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * http://java.sun.com/developer/technicalArticles/GUI/translucent_shaped_windows/
 * 
 */
public class FancySwing {
    public static class MoveMouseListener implements MouseListener, MouseMotionListener {
        Point start_drag;

        Point start_loc;

        JComponent target;

        public MoveMouseListener(JComponent target) {
            this.target = target;
            target.addMouseListener(this);
            target.addMouseMotionListener(this);
        }

        public Window getFrame(Container container) {
            if (container == null) {
                return null;
            }
            if (container instanceof Window) {
                return (Window) container;
            }
            return this.getFrame(container.getParent());
        }

        Point getScreenLocation(MouseEvent e) {
            Point cursor = e.getPoint();
            Point target_location = this.target.getLocationOnScreen();
            return new Point((int) (target_location.getX() + cursor.getX()), (int) (target_location.getY() + cursor.getY()));
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point current = this.getScreenLocation(e);
            Point offset = new Point((int) current.getX() - (int) this.start_drag.getX(), (int) current.getY() - (int) this.start_drag.getY());
            Window frame = this.getFrame(this.target);
            Point new_location = new Point((int) (this.start_loc.getX() + offset.getX()), (int) (this.start_loc.getY() + offset.getY()));
            frame.setLocation(new_location);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            //
        }

        @Override
        public void mousePressed(MouseEvent e) {
            this.start_drag = this.getScreenLocation(e);
            Window frame = this.getFrame(this.target);
            if (frame == null) {
                return;
            }
            this.start_loc = frame.getLocation();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //
        }
    }

    public static JFrame getCurrentFrame() {
        for (Frame frame : Frame.getFrames()) {
            if ((frame instanceof JFrame) && frame.isDisplayable()) {
                return JFrame.class.cast(frame);
            }
        }
        return null;
    }

    public static void lookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ExceptionAndLogHandler.log(ex);
        }
    }

    public static void rounded(Window w) {
        if (AWTUtilitiesWrapper.isTranslucencySupported(AWTUtilitiesWrapper.PERPIXEL_TRANSPARENT)) {
            try {
                Shape shape = new RoundRectangle2D.Float(0, 0, w.getWidth(), w.getHeight(), 20, 20);
                AWTUtilitiesWrapper.setWindowShape(w, shape);
            } catch (Exception ex) {
                ExceptionAndLogHandler.log(ex);
            }
        }
    }

    public static void translucent(Window w) {
        FancySwing.translucent(w, .93f);
    }

    @SuppressWarnings("restriction")
    public static void translucent(Window w, Float f) {
        if (AWTUtilitiesWrapper.isTranslucencySupported(com.sun.awt.AWTUtilities.Translucency.TRANSLUCENT)) {
            try {
                AWTUtilitiesWrapper.setWindowOpacity(w, f);
            } catch (Exception ex) {
                ExceptionAndLogHandler.log(ex);
            }
        }
    }
}
