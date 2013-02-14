package com.predictor.qrmap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: predictor
 * Date: 07.02.13
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */
public class UploadPanel extends JPanel implements ActionListener , MouseListener {
    private static final Logger log =  LoggerFactory.getLogger(UploadPanel.class);
    private static String UPLOAD_COMMAND = "Upload";
    private static String ADD_COMMAND = "Add";
    private static String REMOVE_COMMAND = "Remove";
    private static String VOID_COMMAND = "Void";
    private static String GENERATE_COMMAND = "Generate";
    private static String SAVE_COMMAND = "Save";
    private MarkedMap markedMap=null;
    private Image mapImage;
    private ImagePanel imagePanel;
    private boolean mapInitialized=false;
    private String operation = VOID_COMMAND;

    class ImagePanel extends JPanel{
        @Override
        public void update(Graphics g){
            g.drawImage(mapImage, 0, 0, ImagePanel.this);
            if(markedMap==null){
                return;
            }
            for(MarkedMap.Marker marker: markedMap.getMarkers()){
                g.setColor(Color.RED);
                float fontSize = Float.valueOf(markedMap.markerRadius * 1.4f);
                g.setFont(g.getFont().deriveFont(fontSize));
                g.drawOval(
                        marker.x-markedMap.markerRadius,
                        marker.y-markedMap.markerRadius,
                        markedMap.markerRadius*2,
                        markedMap.markerRadius*2);
                g.setColor(Color.BLACK);
                g.drawString(
                        marker.name,
                        marker.x-markedMap.markerRadius+(2-marker.name.length())*(int)fontSize/2+2*(marker.name.length()-1),
                        marker.y+markedMap.markerRadius/2);
            }
        }
        @Override
        public void paint(Graphics g) {
            update(g);
        }
    }
    public UploadPanel(){
        super(new BorderLayout());
        Component parent = this.getTopLevelAncestor();
        if(parent instanceof JFrame){
            ((JFrame)parent).setResizable(true);
        }
        BottomPanel bottomPanel = new BottomPanel(this);
        imagePanel = new ImagePanel();
        add(imagePanel, BorderLayout.CENTER);
        add(bottomPanel,BorderLayout.SOUTH);
    }
    /**
     * Invoked when an action occurs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(UPLOAD_COMMAND)){
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(UploadPanel.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                log.info("Opening: " + file.getAbsolutePath() + ".");
                try {
                    File resizedFile = ImageResizer.resize(file,imagePanel.getWidth(),imagePanel.getHeight());
                    file = resizedFile;
                } catch (IOException e1) {
                    log.error("Failed to resize file "+file.getAbsolutePath()+". "+e1.getMessage());

                }
                HttpUploader httpUploader = new HttpUploader();
                try {
                    httpUploader.upload("http://predictor-asus.dyndns.org/upload.php",file);
                } catch (Exception e1) {
                    log.error("Failed to upload file. "+e1.getMessage());
                    return;
                }
                try {
                    markedMap= new MarkedMap(file);
                } catch (IOException e1) {
                    log.error("Failed to parse image. " + e1.getMessage());
                    return;
                }
                mapImage = markedMap.getMap();
                imagePanel.setSize(markedMap.width,markedMap.height);
                imagePanel.update(mapImage.getGraphics());
                imagePanel.addMouseListener(this);
                mapInitialized=true;
            } else {
                log.info("Open command cancelled by user.");
            }
        }
        else if(e.getActionCommand().equals(ADD_COMMAND)){
            operation = (operation.equals(ADD_COMMAND))?VOID_COMMAND:ADD_COMMAND;
        }
        else if(e.getActionCommand().equals(REMOVE_COMMAND)){
            operation = (operation.equals(REMOVE_COMMAND))?VOID_COMMAND:REMOVE_COMMAND;
        }
        else if(e.getActionCommand().equals(GENERATE_COMMAND) && mapInitialized){
            final JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal=fc.showOpenDialog(UploadPanel.this);
            if (returnVal != JFileChooser.APPROVE_OPTION)
                return;
            QrPrintout qrPrintout = new QrPrintout(markedMap);
            for(MarkedMap.Marker marker:markedMap.getMarkers()){
                BufferedImage printout = qrPrintout.getPrintout(marker);
                String fileName = fc.getSelectedFile().getAbsolutePath()+File.separator+markedMap.getName()+"."+String.format("%03d",Integer.parseInt(marker.name))+".gif";
                File outputfile = new File(fileName);
                try {
                    ImageIO.write(printout, "gif", outputfile);
                } catch (IOException e1) {
                    log.error("Failed to save printout "+fileName+". "+e1.getMessage());
                    return;
                }
            }

        }
        else if(e.getActionCommand().equals(SAVE_COMMAND)&& mapInitialized){
            final JFileChooser fc = new JFileChooser();
            FileFilter filter = new FileNameExtensionFilter("PNG file", "png");
            fc.addChoosableFileFilter(filter);
            fc.setSelectedFile(new File(markedMap.getName()));
            int returnVal = fc.showSaveDialog(UploadPanel.this);
            if (returnVal != JFileChooser.APPROVE_OPTION)
                return;
            File outputFile = fc.getSelectedFile();
            if(!outputFile.getName().toLowerCase().endsWith(".png")){
                outputFile = new File(outputFile.getAbsolutePath()+".png");
            }
            try {
                outputFile.createNewFile();
                BufferedImage image = new BufferedImage(markedMap.width,markedMap.height,BufferedImage.TYPE_INT_RGB);
                imagePanel.print(image.createGraphics());
                ImageIO.write(image,"png",outputFile);
            } catch (IOException e1) {
                log.error("Failed to save marked map. "+e1.getMessage());
            }
        }
    }

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if(!mapInitialized)
            return;
        if(e.getX()>markedMap.width-markedMap.markerRadius||e.getY()>markedMap.height-markedMap.markerRadius)
            return;
        Point clickedMarker=null;
        for(Point marker:markedMap.getMarkers()){
            if(markedMap.pointInMarker(e.getX(),e.getY(),marker.x,marker.y)){
                clickedMarker = marker;
                break;
            }
        }
        if(operation.equals(ADD_COMMAND)){
            if(clickedMarker!=null){
                return;
            }
            markedMap.addMarker(new MarkedMap.Marker(e.getX(),e.getY(),markedMap.getIndex()));
        }
        else if(operation.equals(REMOVE_COMMAND)){
            if(clickedMarker==null){
                return;
            }
            markedMap.removeMarker(clickedMarker);
        }
        BufferedImage mapImage = new BufferedImage(markedMap.getMap().getWidth(),markedMap.getMap().getHeight(),markedMap.getMap().getType());
        mapImage.setData(markedMap.getMap().getData());
        Graphics mapGraphics = mapImage.createGraphics();
        imagePanel.update(mapGraphics);
        imagePanel.repaint();
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Invoked when the mouse enters a component.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Invoked when the mouse exits a component.
     */
    @Override
    public void mouseExited(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
