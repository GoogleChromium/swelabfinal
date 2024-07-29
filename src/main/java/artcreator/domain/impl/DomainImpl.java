package artcreator.domain.impl;


/* Factory for creating domain objects */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DomainImpl {

	private static BufferedImage startImage = null;

	public static BufferedImage setImage(String path) {
		try {
			if (path != null) {
				startImage = ImageIO.read(new File(path));
				return startImage;
			}

			return null;
		}
		catch (IOException e)
		{
		e.printStackTrace();
		}
        return null;
    }
	
	
	public Object mkObject() {
		// TODO Auto-generated method stub
		return null;
	}	
	

}
