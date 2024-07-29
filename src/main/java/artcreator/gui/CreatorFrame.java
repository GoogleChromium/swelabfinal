package artcreator.gui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import artcreator.creator.CreatorFacade;
import artcreator.creator.CreatorFactory;
import artcreator.creator.impl.CreatorImpl;
import artcreator.creator.port.Creator;
import artcreator.statemachine.StateMachineFactory;
import artcreator.statemachine.port.Observer;
import artcreator.statemachine.port.State;
import artcreator.statemachine.port.Subject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CreatorFrame extends JFrame implements Observer{


	   private Creator creator = CreatorFactory.FACTORY.creator();
	   private Subject subject = StateMachineFactory.FACTORY.subject();
	   private Controller controller;

	   private static final int WIDTH = 1280;
	   private static final int HEIGHT = 720;

	public static JLabel imageLabel = new JLabel();
	private static JButton btn = new JButton("Datei Auswählen");
	private JPanel panel = new JPanel(new GridBagLayout());
	private JLabel instructionsLabel = new JLabel("Wähle dein Bild aus oder lege sie per Drag-and-drop hier ab", SwingConstants.CENTER);
	private JLabel placeholderIcon = new JLabel();
	private JSlider slider = new JSlider(10, 200, 70); // Slider for pixel size
	private JTextArea pixelInfoTextArea = new JTextArea(10, 20); // Text area for pixel information
	private JScrollPane scrollPane = new JScrollPane(pixelInfoTextArea);
	private int pixelSize = 50;

	public int getPixelSize() {
		return pixelSize;
	}

	   public CreatorFrame()  {
	     super("ArtCreator");
	     initUI();
		 this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	     this.setSize(WIDTH, HEIGHT);
	     this.setLocationRelativeTo(null);
	     this.subject.attach(this);
	     this.controller = new Controller(this, subject, creator);

	     /* build view */
	     this.btn.addActionListener(this.controller);
         this.getContentPane().add(this.panel);
	   }

	private void initUI() {
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);

		// Placeholder settings icon
		placeholderIcon.setIcon(UIManager.getIcon("OptionPane.informationIcon"));

		// Set layout and add components
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10); // Margin

		this.imageLabel.setHorizontalAlignment(JLabel.CENTER);

		// Load the placeholder image
		try {
			this.imageLabel.setIcon(new ImageIcon(
					new ImageIcon("src/main/resources/image.png")
							.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH)
			));
		} catch (Exception e) {
			System.out.println("Image not found");
			e.printStackTrace();
		}

		//this.imageLabel.setTransferHandler(new CreatorImpl.SetCurrentImage());

		// Set font and alignment for instructions label
		this.instructionsLabel.setFont(new Font("Arial", Font.BOLD, 14));
		this.instructionsLabel.setHorizontalAlignment(JLabel.CENTER);

		// Customize the button
		btn.setPreferredSize(new Dimension(200, 50)); // Set preferred size
		btn.setFont(new Font("Arial", Font.BOLD, 16)); // Set font
		btn.setBackground(Color.LIGHT_GRAY); // Set background color
		btn.setForeground(Color.BLACK); // Set text color

		// Configure the slider
		slider.setMajorTickSpacing(50);
		slider.setMinorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				pixelSize = slider.getValue();
			}
		});

		// Configure the text area
		pixelInfoTextArea.setEditable(false);
		pixelInfoTextArea.setLineWrap(true);
		pixelInfoTextArea.setWrapStyleWord(true);

		// Add components to panel using GridBagLayout
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		panel.add(placeholderIcon, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		panel.add(this.imageLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridheight = 1;
		panel.add(this.instructionsLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.gridheight = 1;
		panel.add(this.btn, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.gridheight = 1;
		panel.add(this.slider, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(scrollPane, gbc);

		// Add panel to frame
		this.getContentPane().add(this.panel);
	}

	public void setImage(BufferedImage image){
		imageLabel.setIcon(new ImageIcon(image.getScaledInstance(300, 300, Image.SCALE_SMOOTH)));
	}

	public void updatePixelInfo(String info) {
		pixelInfoTextArea.setText(info);
	}

	   public void update(State newState) {
		   switch (newState){
			   case State.S.CREATE_TEMPLATE:
				   btn.setText("Dateien auswählen");
				   break;

			   case State.S.START_PROCESS:
				   btn.setText("Vorlage generieren");
				   break;

			   case State.S.EXPORT_IMAGE:
				   btn.setText("Export");
				   break;

			   case State.S.TEMPLATE_CHANGED:
				   btn.setText("Neue Vorlage generieren");
				   break;
			   default:
				   throw new IllegalStateException("Unexpected value: " + subject.getState());
		   }
	   }

	}
