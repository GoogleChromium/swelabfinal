package artcreator.creator.port;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface Creator {
	
	void sysop(String str);

	abstract BufferedImage importImage(String absolutePath);

	abstract BufferedImage processImage(int i);

	abstract void exportFile(File file) throws IOException;

	abstract StringBuilder getPixelInfo();
}
