package com.predictor.qrmap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: predictor
 * Date: 11.02.13
 * Time: 17:14
 * To change this template use File | Settings | File Templates.
 */
public class MapEditorApplet extends JApplet {
    private static final Logger log =  LoggerFactory.getLogger(MapEditorApplet.class);
    //Called when this applet is loaded into the browser.
    public void init() {
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) {
            log.error("createGUI didn't complete successfully. " + e.getMessage());
        }
    }

    private void createGUI() {
        //Create and set up the content pane.
        this.setSize(new Dimension(1000,600));
        UploadPanel newContentPane = new UploadPanel();
        newContentPane.setOpaque(true);
        setContentPane(newContentPane);
    }
}
