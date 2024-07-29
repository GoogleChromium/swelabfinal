package artcreator.creator;

import artcreator.creator.impl.CreatorImpl;
import artcreator.creator.port.Creator;
import artcreator.domain.DomainFactory;
import artcreator.statemachine.StateMachineFactory;
import artcreator.statemachine.port.StateMachine;
import artcreator.statemachine.port.State.S;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static artcreator.statemachine.port.State.S.*;

public class CreatorFacade implements CreatorFactory, Creator {

	private CreatorImpl creator;
	private StateMachine stateMachine;
	
	@Override
	public Creator creator() {
		if (this.creator == null) {
			this.stateMachine = StateMachineFactory.FACTORY.stateMachine();
			this.creator = new CreatorImpl(stateMachine, DomainFactory.FACTORY.domain());
		}
		return this;
	}

	@Override
	public synchronized void sysop(String str) {
		if (this.stateMachine.getState().isSubStateOf( S.CREATE_TEMPLATE /* choose right state*/ ))
			this.creator.sysop(str);
	}

	@Override
	public BufferedImage importImage (String path){
		try {
			BufferedImage image = ImageIO.read(new File(path));
			creator.setStartImage(image);
			stateMachine.setState(START_PROCESS);
			return image;
		}catch(IOException e){
			stateMachine.setState(CREATE_TEMPLATE);
			e.printStackTrace();
		}
		return null;
	}

	public BufferedImage processImage(int size){
		stateMachine.setState(EXPORT_IMAGE);
		return creator.processImage(size);
	}

	public void exportFile(File file) throws IOException{
			creator.exportImage(file);
			stateMachine.setState(CREATE_TEMPLATE);
	}

	public StringBuilder getPixelInfo() {
		return creator.getPixelInfo();
	}

}
