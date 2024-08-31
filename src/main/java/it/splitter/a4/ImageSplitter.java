package it.splitter.a4;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSplitter {

    public static void main(String[] args) {
        try {
            // Print working directory
            System.out.println("Working Directory = " + System.getProperty("user.dir"));

            // Load the image
            File inputFile = new File("YourImageToConvertName.png");
            if (!inputFile.exists()) {
                System.err.println("File not found: " + inputFile.getAbsolutePath());
                return;
            }

            BufferedImage image = ImageIO.read(inputFile);

            // A4 size at 300 DPI
            int dpi = 300;
            double inch = 25.4;
            int a4WidthPx = (int) (210 * dpi / inch);
            int a4HeightPx = (int) (297 * dpi / inch);

            System.out.println("A4 Dimensions in Pixels: " + a4WidthPx + "x" + a4HeightPx);

            // Image dimensions
            int imgWidth = image.getWidth();
            int imgHeight = image.getHeight();

            System.out.println("Image Dimensions: " + imgWidth + "x" + imgHeight);

            // Calculate the number of pages
            int numPagesWidth = (imgWidth + a4WidthPx - 1) / a4WidthPx;
            int numPagesHeight = (imgHeight + a4HeightPx - 1) / a4HeightPx;

            System.out.println("Number of Pages (Width x Height): " + numPagesWidth + " x " + numPagesHeight);

            // Split the image into sections and store them temporarily
            BufferedImage[] pages = new BufferedImage[numPagesWidth * numPagesHeight];
            int pageNumber = 0;
            for (int i = 0; i < numPagesHeight; i++) {
                for (int j = 0; j < numPagesWidth; j++) {
                    int left = j * a4WidthPx;
                    int upper = i * a4HeightPx;
                    int right = Math.min((j + 1) * a4WidthPx, imgWidth);
                    int lower = Math.min((i + 1) * a4HeightPx, imgHeight);

                    int width = right - left;
                    int height = lower - upper;

                    System.out.println("Cropping (Page " + (pageNumber + 1) + "): left=" + left + ", upper=" + upper + ", width=" + width + ", height=" + height);

                    // Crop the section
                    pages[pageNumber] = image.getSubimage(left, upper, width, height);
                    pageNumber++;
                }
            }

            // Combine pages into pairs
            for (int i = 0; i < pages.length; i += 2) {
                BufferedImage page1 = pages[i];
                BufferedImage page2 = i + 1 < pages.length ? pages[i + 1] : null;

                int combinedWidth = page1.getWidth() + (page2 != null ? page2.getWidth() : 0);
                int combinedHeight = Math.max(page1.getHeight(), (page2 != null ? page2.getHeight() : 0));

                BufferedImage combinedImage = new BufferedImage(combinedWidth, combinedHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = combinedImage.createGraphics();
                g.drawImage(page1, 0, 0, null);
                if (page2 != null) {
                    g.drawImage(page2, page1.getWidth(), 0, null);
                }
                g.dispose();

                // Save the combined image
                File outputfile = new File("YourImageToConvertName" + (i / 2 + 1) + ".png");
                ImageIO.write(combinedImage, "png", outputfile);
            }

            System.out.println("Image successfully split into multiple pages and combined.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
