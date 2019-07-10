import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
*/
public class GLDM_2 implements PlugIn {

    ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;
	
	
    public static void main(String args[]) {
		//new ImageJ();
    	IJ.open("C:\\Users\\tommy\\eclipse-workspace\\GLDM_2\\orchid.jpg");
    	//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");
		
		GLDM_2 pw = new GLDM_2();
		pw.imp = IJ.getImage();
		pw.run("");
	}
    public static void checkConversion() {
    	for (int r = 0; r < 256; r++) {
    		for (int g = 0; g < 256; g++) {
    			for (int b = 0; b < 256; b++) {
    				
    				double[] YCbCrResult = FromRGBToYCbCr(r, g, b);
    				int[] reconversion = FromYCbCrToRGB(YCbCrResult[0], YCbCrResult[1], YCbCrResult[2]);
    				
    				if (r == YCbCrResult[0] && g == YCbCrResult[1] && b == YCbCrResult[2]) {
    					System.out.println("Conversion was successful!");
    				}
    			}
    		}
    	}
    }
    
    public static double[] FromRGBToYCbCr (int r, int g, int b) {
    	double Y = (0.299 * r) + (0.587 * g) + (0.114 * b);
    	double Cb = (-0.168736 * r) - (0.331264 * g) + (0.5 * b);
    	double Cr = (0.5 * r) - (0.418688 * g) - (0.081312 * b);
    	double YCbCr[] = {Y, Cb, Cr};
    	return YCbCr;
    }
    
    public static int[] FromYCbCrToRGB(double Y, double Cb, double Cr) {
		int r = (int) (Y + 1.402 * Cr);
		int g = (int) (Y - 0.344 * Cb - 0.714 * Cr);
		int b = (int) (Y + 1.772 * Cb);
		int[] RGB = { r, g, b };
		return RGB;
    }
    
    public void run(String arg) {
    	if (imp==null) 
    		imp = WindowManager.getCurrentImage();
        if (imp==null) {
            return;
        }
        CustomCanvas cc = new CustomCanvas(imp);
        
        storePixelValues(imp.getProcessor());
        
        new CustomWindow(imp, cc);
    }


    private void storePixelValues(ImageProcessor ip) {
    	width = ip.getWidth();
		height = ip.getHeight();
		
		origPixels = ((int []) ip.getPixels()).clone();
	}


	class CustomCanvas extends ImageCanvas {
    
        CustomCanvas(ImagePlus imp) {
            super(imp);
        }
    
    } // CustomCanvas inner class
    
    
    class CustomWindow extends ImageWindow implements ChangeListener {
         
        private JSlider jSliderBrightness;
		private JSlider jSliderKontrast;
		private JSlider jSliderSaettigung;
		private JSlider jSliderHue;
		
		private double brightness = 0.0;
		private double kontrast = 1.0;
		private double saettigung = 1.0;
		private double hue = 0.0;

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }
    
        void addPanel() {
        	//JPanel panel = new JPanel();
        	Panel panel = new Panel();

            panel.setLayout(new GridLayout(4, 1));
            jSliderBrightness = makeTitledSilder("Helligkeit", -128, 127, 0);
            jSliderKontrast = makeTitledSilder("Kontrast", 0, 100, 0);
            jSliderSaettigung = makeTitledSilder("Sättigung", 0, 50, 0);
            jSliderHue = makeTitledSilder("Farbton", 0, 360, 0);
            
            panel.add(jSliderBrightness);
            panel.add(jSliderKontrast);
            panel.add(jSliderSaettigung);
            panel.add(jSliderHue);
            
            add(panel);
            
            pack();
         }
      
        private JSlider makeTitledSilder(String string, int minVal, int maxVal, int val) {
		
        	JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val );
        	Dimension preferredSize = new Dimension(width, 50);
        	slider.setPreferredSize(preferredSize);
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(), 
					string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
			slider.setMajorTickSpacing((maxVal - minVal)/10 );
			slider.setPaintTicks(true);
			slider.addChangeListener(this);
			
			return slider;
		}
        
        private void setSliderTitle(JSlider slider, String str) {
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
				str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
		}

		public void stateChanged( ChangeEvent e ){
			JSlider slider = (JSlider)e.getSource();

			if (slider == jSliderBrightness) {
				brightness = slider.getValue();
				String str = "Helligkeit " + brightness; 
				setSliderTitle(jSliderBrightness, str); 
			}
			
			if (slider == jSliderKontrast) {
				kontrast = slider.getValue() / 10;
				String str = "Kontrast " + kontrast/10; 
				setSliderTitle(jSliderKontrast, str); 
			}
			
			if (slider == jSliderSaettigung) {
				saettigung = slider.getValue() / 10;
				String str = "Sättigung " + saettigung/10; 
				setSliderTitle(jSliderSaettigung, str); 
			}
			
			if (slider == jSliderHue) {
				hue = slider.getValue();
				String str = "Farbton " + hue; 
				setSliderTitle(jSliderHue, str); 
			}
			
			changePixelValues(imp.getProcessor());
			
			imp.updateAndDraw();
		}

		private int borderValues(int val) {
			val = Math.max(val, 0);
			val = Math.min(val, 255);
			return val;
		}
		
		private void changePixelValues(ImageProcessor ip) {
			
			// Array fuer den Zugriff auf die Pixelwerte
			int[] pixels = (int[])ip.getPixels();
			
			// Die Gradzahl hue wird uebergeben und der cosinus berechnet
			double cosHue = Math.cos(Math.toRadians(hue));
			double sinHue = Math.sin(Math.toRadians(hue));

			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
					
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					
					
					// anstelle dieser drei Zeilen später hier die Farbtransformation durchführen,
					// die Y Cb Cr -Werte verändern und dann wieder zurücktransformieren
					double[] YCbCr = FromRGBToYCbCr(r, g, b);
					double Y = YCbCr[0];
					double Cb = YCbCr[1];
					double Cr = YCbCr[2];

					if (brightness != 0.0) {
						Y = Y + brightness;
					}

					if (saettigung != 1.0) {
						Cb = Cb * saettigung;
						Cr = Cr * saettigung;
					}

					if (kontrast != 1.0) {
						Y = (Y - 128) * (kontrast) + 128;
					}

					if (hue % 360 != 0) {
						double CbOld = Cb;

						Cb = cosHue * CbOld - sinHue * Cr;
						Cr = sinHue * CbOld + cosHue * Cr;
					}

					int rn = (int) (Y + 1.402 * Cr);
					int gn = (int) (Y - 0.3441 * Cb - 0.7141 * Cr);
					int bn = (int) (Y + 1.772 * Cb);
					
					rn = borderValues(rn);
					gn = borderValues(gn);
					bn = borderValues(bn);
					
					
					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
		
    } // CustomWindow inner class
} 