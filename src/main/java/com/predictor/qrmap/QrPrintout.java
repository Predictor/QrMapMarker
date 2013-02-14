package com.predictor.qrmap;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: predictor
 * Date: 12.02.13
 * Time: 14:08
 * To change this template use File | Settings | File Templates.
 */
public class QrPrintout {
    private static final Logger log =  LoggerFactory.getLogger(QrPrintout.class);

    private final MarkedMap markedMap;
    private final static int QR_SIZE = 150;
    private final static int FONT_SIZE = 15;
    public QrPrintout(MarkedMap markedMap) {
        this.markedMap = markedMap;
    }

    public BufferedImage getPrintout(MarkedMap.Marker marker){
        BufferedImage printout = new BufferedImage(QR_SIZE, QR_SIZE+FONT_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D printoutGraphics = printout.createGraphics();
        printoutGraphics.setBackground(Color.WHITE);
        printoutGraphics.setColor(Color.BLACK);
        printoutGraphics.clearRect(0,0,printout.getWidth(),printout.getHeight());
        BufferedImage markerQr = encodeMarker(marker);
        printoutGraphics.setFont(printoutGraphics.getFont().deriveFont(Float.valueOf(FONT_SIZE)));
        printoutGraphics.drawString(marker.name,0,FONT_SIZE);
        printoutGraphics.drawImage(markerQr,0,FONT_SIZE+3,null);
        return printout;
    }
    private BufferedImage encodeMarker(MarkedMap.Marker marker){
        BitMatrix matrix;
        com.google.zxing.Writer writer = new QRCodeWriter();
        String content = markedMap.getName()+":"+marker.x+";"+marker.y;
        try {
            matrix = writer.encode(content, com.google.zxing.BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
        } catch (WriterException e) {
            log.error("Failed to generate QR code for marker "+marker.name+". "+e.getMessage());
            return null;
        }
        int matrixWidth = matrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth,
                BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        graphics.setColor(Color.BLACK);
        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (matrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        return image;
    }

}
