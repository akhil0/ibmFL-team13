

package classify;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;



public class DemoFrame extends JFrame{
	
	private JButton submitButton;
	private JButton createAccount;
	private JTextField employeeName;
	private JTextField purchaseName;
	private JTextField moneyAmount;
	private JLabel alertLabel;
	private JLabel output;

	public DemoFrame() {
		
		initializeComponents();
		createGUI();
		addListeners();
	}
	
	private void initializeComponents(){
		
		submitButton = new JButton("Submit");
		employeeName = new JTextField("employeeName");
		purchaseName = new JTextField("purchaseName");
		moneyAmount = new JTextField("");
		output = new JLabel("", SwingConstants.CENTER);
		
		alertLabel = new JLabel();
	}
	
	private void createGUI(){
		
		JFrame main = new JFrame("Category Extraction");
		main.setLayout(new BorderLayout());
        // Don't use this nonsense!
        main.setBounds(700,300,1000,1000);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        employeeName = new JTextField(10);
        purchaseName = new JTextField(10);
        main.setLayout(new GridLayout(0,1));
        JPanel pane = new JPanel(new GridLayout(0,2));
        main.add(pane, BorderLayout.CENTER);
        pane.add(new JLabel("Employee Name: "));
        pane.add(employeeName);
        pane.add(new JLabel("Purchase Name: "));
        pane.add(purchaseName);
        pane.add(new JLabel("Money Amount: "));
        pane.add(moneyAmount);
        
        JPanel pane2 = new JPanel(new GridLayout(0,1));
        main.add(pane2, BorderLayout.LINE_END);
        submitButton = new JButton("Submit");
        submitButton.setSize(100, 80);
        pane2.add(submitButton);
        pane2.add(output);
        
        //submit.addActionListener(new Handler());
        main.pack();
        main.setVisible(true);
        main.setSize(700, 200);
	}
	
	//returns whether the buttons should be enabled
	private boolean canPressButtons(){
		return (!employeeName.getText().isEmpty() && !employeeName.getText().equals("employeeName") && 
				!purchaseName.getText().equals("purchaseName") && !purchaseName.getText().isEmpty());
	}
	
	private void addListeners(){
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//document listeners
		employeeName.getDocument().addDocumentListener(new MyDocumentListener());
		purchaseName.getDocument().addDocumentListener(new MyDocumentListener());
		
		//action listeners
		submitButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				String category = findCategory(purchaseName.getText() + " $" + moneyAmount.getText());
				output.setText("Category: " + category);
				if(category.contains("Other") || (category.contains("Air") && !purchaseName.getText().contains("Big Blue"))) {
					output.setForeground(Color.RED);
				}
				else {
					output.setForeground(Color.BLACK);
				}
			}
			
		});
		
	
	}
	
private static String findCategory(String pname) {
	NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
			  NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
			  "2ed3c700-1791-4def-a625-def4963456c7",
			  "rVXFfkywnb2D"
			);

	//String text = "Hilton Grand Vacation 2 nights stay $";
	
	CategoriesOptions categories = new CategoriesOptions();

	Features features = new Features.Builder()
	  .categories(categories)
	  .build();

	AnalyzeOptions parameters = new AnalyzeOptions.Builder()
	  .text(pname)
	  .features(features)
	  .build();

	AnalysisResults response = service
	  .analyze(parameters)
	  .execute();
	
	//List<String> labels = new ArrayList<String>();
	//Get all categories
	List<CategoriesResult> types = response.getCategories();
	
	/*
		labels.add(k.getLabel());
	});
	*/
	
	String label = types.get(0).getLabel();
	//System.out.println(label);
	
	//String type = findCategory(label);
		if(label.contains("airline")) 
			return "Air Travel";
		else if(label.contains("hotel"))
			return "Hotel Service";
		else if(label.contains("food") || label.contains("drink"))
			return "Restaurant";
		else if(label.contains("automative") || label.contains("car") || label.contains("vehicle"))
			return "Taxi Service";
		else
			return "Other Purchases";
 	}

	//sets the buttons enabled or disabled
	private class MyDocumentListener implements DocumentListener{
		
		public void insertUpdate(DocumentEvent e) {
			submitButton.setEnabled(canPressButtons());
		}
		
		public void removeUpdate(DocumentEvent e) {
			submitButton.setEnabled(canPressButtons());
		}
		
		public void changedUpdate(DocumentEvent e) {
			submitButton.setEnabled(canPressButtons());
		}
	}
}