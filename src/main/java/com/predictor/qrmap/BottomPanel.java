package com.predictor.qrmap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: predictor
 * Date: 12.02.13
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
public class BottomPanel extends JPanel implements ActionListener {
    private static final Logger log =  LoggerFactory.getLogger(BottomPanel.class);
    private static String UPLOAD_COMMAND = "Upload";
    private static String ADD_COMMAND = "Add";
    private static String REMOVE_COMMAND = "Remove";
    private static String GENERATE_COMMAND = "Generate";
    private static String SAVE_COMMAND = "Save";
    private JButton uploadButton;
    private JButton qrCodeButton;
    private JButton saveButton;
    private JButton addButton;
    private JButton removeButton;
    public BottomPanel(ActionListener parent){
        super(new GridLayout(0,5));

        uploadButton= new JButton("Upload");
        uploadButton.setActionCommand(UPLOAD_COMMAND);
        uploadButton.addActionListener(parent);

        addButton = new JButton("Add marker");
        addButton.setActionCommand(ADD_COMMAND);
        addButton.addActionListener(this);
        addButton.addActionListener(parent);
        addButton.setFont(addButton.getFont().deriveFont(Font.PLAIN));

        removeButton = new JButton("Remove marker");
        removeButton.setActionCommand(REMOVE_COMMAND);
        removeButton.addActionListener(this);
        removeButton.addActionListener(parent);
        removeButton.setFont(removeButton.getFont().deriveFont(Font.PLAIN));

        qrCodeButton= new JButton("Generate QR codes");
        qrCodeButton.setActionCommand(GENERATE_COMMAND);
        qrCodeButton.addActionListener(parent);

        saveButton= new JButton("Save marked map");
        saveButton.setActionCommand(SAVE_COMMAND);
        saveButton.addActionListener(parent);

        add(uploadButton);
        add(addButton);
        add(removeButton);
        add(qrCodeButton);
        add(saveButton);
    }

    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(ADD_COMMAND)){
            if(addButton.getFont().getStyle()==Font.BOLD)
                addButton.setFont(addButton.getFont().deriveFont(Font.PLAIN));
            else{
                removeButton.setFont(addButton.getFont());
                addButton.setFont(addButton.getFont().deriveFont(Font.BOLD));
            }
        }
        else if(e.getActionCommand().equals(REMOVE_COMMAND)){
            if(removeButton.getFont().getStyle()==Font.BOLD)
                removeButton.setFont(removeButton.getFont().deriveFont(Font.PLAIN));
            else{
                addButton.setFont(removeButton.getFont());
                removeButton.setFont(removeButton.getFont().deriveFont(Font.BOLD));
            }
        }
    }
}
