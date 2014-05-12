package view.components;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class FontUI {
    /** Adds desired fonts to program's library of fonts. */
    public static void addUIFonts() {
        try {
            ArrayList<Font> fonts = new ArrayList<Font>();
            fonts.add(Font.createFont(Font.TRUETYPE_FONT, new File("src/view/resources/OpenSans.ttf")));
            fonts.add(Font.createFont(Font.TRUETYPE_FONT, new File("src/view/resources/Chunk.ttf")));
            fonts.add(Font.createFont(Font.TRUETYPE_FONT, new File("src/view/resources/Vibur.ttf")));
            
            GraphicsEnvironment ge = 
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (Font f: fonts)
                ge.registerFont(f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Sets default program font to given font f.
     * @param f The font to set default font to.
     */
    public static void setUIFont (javax.swing.plaf.FontUIResource f){
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
          Object key = keys.nextElement();
          Object value = UIManager.get (key);
          if (value != null && value instanceof FontUIResource)
            UIManager.put (key, f);
        }
    }
}
