package com.predictor.qrmap;

import sun.awt.image.ToolkitImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: predictor
 * Date: 13.02.13
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
public class ImageResizer {
    public static File resize (File image, int width, int height) throws IOException {
        BufferedImage before = ImageIO.read(image);
        float w0 = width;
        float h0 = height;
        float w1 = before.getWidth();
        float h1 = before.getHeight();
        float scale = Math.min(w0/w1,h0/h1);
        BufferedImage after = new BufferedImage(Math.round(w1*scale), Math.round(h1*scale), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp =
                new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        after = scaleOp.filter(before, after);
        File out = File.createTempFile(image.getName()+".",".png");
        ImageIO.write(after,"png",out);
        return out;
    }
}
