package artcreator.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import artcreator.creator.CreatorFacade;
import artcreator.creator.impl.CreatorImpl;
import artcreator.creator.port.Creator;
import artcreator.statemachine.port.Observer;
import artcreator.statemachine.port.State;
import artcreator.statemachine.port.Subject;

public class Controller implements ActionListener, Observer {

	  private CreatorFrame myView;
	  private Creator myModel;
	  private Subject subject;


	  public Controller(CreatorFrame view, Subject subject, Creator model) {
	    this.myView = view;
	    this.myModel = model;
	    this.subject = subject;
	    this.subject.attach(this); 
	  }

	public File chooseExportPath() throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to save");

		// Set the file filter to only allow png files
		FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG Images", "png");
		FileNameExtensionFilter jpegFilter = new FileNameExtensionFilter("JPEG Images", "jpg");
		fileChooser.addChoosableFileFilter(pngFilter);
		fileChooser.addChoosableFileFilter(jpegFilter);
		fileChooser.setFileFilter(pngFilter);

		int userSelection = fileChooser.showSaveDialog(myView);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			// Ensure the file has a .png extension
			FileNameExtensionFilter selectedFilter = (FileNameExtensionFilter) fileChooser.getFileFilter();
			String extension;

			if(selectedFilter==pngFilter){
				extension = ".png";
			} else if (selectedFilter==jpegFilter) {
				extension = ".jpg";
			}else{
				extension = ".png";
			}

			if (!fileToSave.getAbsolutePath().endsWith(extension)) {
				fileToSave = new File(fileToSave.getAbsolutePath() + extension);
			}
			return fileToSave;
		}
		return null;
	  }


	public void handleImport(){
		  System.out.printf("handling import");
		  JFileChooser fileChooser = new JFileChooser();
		  FileNameExtensionFilter filter = new FileNameExtensionFilter("Bilder", "jpg", "png");
		  fileChooser.setFileFilter(filter);

		  Component parentComponent = null;
		  int returnValue = fileChooser.showOpenDialog(myView);
		  if (returnValue == JFileChooser.APPROVE_OPTION) {
			  File selectedFile = fileChooser.getSelectedFile();
			  System.out.printf(((File) selectedFile).getAbsolutePath());
			  BufferedImage image = myModel.importImage(selectedFile.getAbsolutePath());
			  myView.setImage(image);
		  }
		  return;
	  }

	  public void handleConversion(){

		  System.out.printf("handling conversion");
		  myView.setImage(myModel.processImage(myView.getPixelSize()));
		  StringBuilder info = myModel.getPixelInfo();
		  myView.updatePixelInfo(info.toString());
	  }

	  public void handleExport(){

		  System.out.printf("handling export");
		  try {
			  File file = chooseExportPath();
			  myModel.exportFile(file);

			  JOptionPane.showMessageDialog(myView, "Bild gespeichert als " + file.getAbsolutePath());
		  }catch(IOException e){
			  e.printStackTrace();
			  JOptionPane.showMessageDialog(myView, "Fehler beim speichern des Bilds" + e.getMessage());
		  }

	  }


	  public void testState(){
		  System.out.println("state has changed");
	  }

	  public synchronized void actionPerformed(ActionEvent e) {

	   /* read input */
		String str = (((JButton)  e.getSource()).getText());
		CompletableFuture.runAsync(() -> this.myModel.sysop(str));
		switch (subject.getState()){
			case State.S.CREATE_TEMPLATE:
				handleImport();
				break;

			case State.S.START_PROCESS:
				handleConversion();
				break;

			case State.S.EXPORT_IMAGE:
				handleExport();
				break;

			case State.S.TEMPLATE_CHANGED:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + subject.getState());
        }

	  }
	  
	  public void update(State newState) {
		  System.out.printf("Controller state has changed: " + newState.toString());
		  myView.update(newState);
	  }
	}
	
