package view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.BabyLink;
import model.MasterLink;
import model.MasterLinkedList;

public class App extends Application {
	MasterLinkedList masterList;
	TextArea paragraphArea = new TextArea();
	File mainFile;
	int wordCountTextFile = 0;
	int sentenceCountTextFile = 0;
	int syllableCountTextFile = 0;
	final int arraySize = 100500;
	HashMap<String, String> dictionaryHashMap = new HashMap();
	String wordForSpellCheck = "";
	Clipboard clipboard = Clipboard.getSystemClipboard();
	ClipboardContent content = new ClipboardContent();

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		dictionary();
		BorderPane bPane = new BorderPane();
		GridPane topPane = new GridPane();
		paragraphArea.setWrapText(true);
		paragraphArea.setStyle("-fx-background-color: derive(orange, +50%);" + "-fx-border-style: solid inside;"
				+ "-fx-border-width: 2;" + "-fx-border-insets: 5;" + "-fx-border-radius: 7;"
				+ "-fx-border-color: derive(orange, +50%);");

		topPane.setPadding(new Insets(30));
		topPane.setHgap(10);
		topPane.setVgap(30);

		bPane.setPrefSize(840, 680);

		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem openFile = new MenuItem("Open");
		MenuItem closeFile = new MenuItem("Close");
		MenuItem saveFile = new MenuItem("Save");
		MenuItem saveAsFile = new MenuItem("Save as");
		MenuItem exitFile = new MenuItem("Exit");

		fileMenu.getItems().addAll(openFile, closeFile, saveFile, saveAsFile, exitFile);

		Menu viewMenu = new Menu("View");
		MenuItem wordCount = new MenuItem("Word Count");
		MenuItem sentenceCount = new MenuItem("Sentence Count");
		MenuItem fleschScore = new MenuItem("Flesch Score");

		viewMenu.getItems().addAll(wordCount, sentenceCount, fleschScore);

		Menu editMenu = new Menu("Edit");
		MenuItem copy = new MenuItem("Copy");
		MenuItem cut = new MenuItem("Cut");
		MenuItem delete = new MenuItem("Delete");
		MenuItem paste = new MenuItem("Paste");
		MenuItem markov = new MenuItem("Markov");
		MenuItem spellCheck = new MenuItem("Spell Check");

		editMenu.getItems().addAll(copy, cut, delete, paste, markov, spellCheck);

		menuBar.getMenus().addAll(fileMenu, viewMenu, editMenu);
		menuBar.setStyle("-fx-background-color: derive(yellow, +50%);" + "-fx-selection-bar: orange;");

		Label label = new Label();
		label.setStyle("-fx-background-color: derive(yellow, +50%);");

		paragraphArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				String written = paragraphArea.getText();
				int wordCount = 0;
				int sentences = 0;
				int syllables = 0;
				double fleschScores = 0.0;

				syllables = syllableCheckerTyped(written);
				sentences = sentenceCheckerTyping(written);
				wordCount = wordCount(written);

				wordCountTextFile = wordCount;
				sentenceCountTextFile = sentences;
				syllableCountTextFile = syllables;

				if (sentences != 0 && wordCount != 0) {
					fleschScores = 206.835 - 1.015 * (wordCount / sentences) - 84.6 * (syllables / wordCount);
				}

				label.setText("Syllables: " + syllables + " 	Sentences: " + sentences + " 	Word Count: "
						+ wordCount + " 	Flesch Score: " + fleschScores);
			}
		});

		copy.setOnAction(e -> {
			copy();
		});

		paste.setOnAction(e -> {
			paste();
		});

		cut.setOnAction(e -> {
			cut();
		});

		delete.setOnAction(e -> {
			delete();
		});

		spellCheck.setOnAction(e -> {
			Stage stage = new Stage();
			BorderPane spellCheckPane = new BorderPane();
			GridPane spellCheckGrid = new GridPane();
			GridPane spellCheckGrid2 = new GridPane();
			Text spellCheckText = new Text("Spell Check");
			spellCheckText.setFill(Color.BLACK);
			spellCheckText.setScaleX(1);
			spellCheckText.setScaleY(1);
			spellCheckGrid.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
					+ "-fx-border-insets: 5;" + "-fx-border-radius: 7;" + "-fx-border-color: derive(orange, +50%);"
					+ "-fx-background-color: yellow;" + "-fx-background-color: derive(yellow, +50%);");
			spellCheckGrid.setPadding(new Insets(80));
			spellCheckGrid.add(spellCheckText, 2, 4);

			spellCheckGrid2.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
					+ "-fx-border-insets: 5;" + "-fx-border-radius: 7;" + "-fx-border-color: derive(orange, +50%);"
					+ "-fx-background-color: yellow;" + "-fx-background-color: derive(yellow, +50%);");
			spellCheckGrid2.setPadding(new Insets(80));

			spellCheckPane.setCenter(spellCheckGrid2);
			spellCheckPane.setTop(spellCheckGrid);

			String text = paragraphArea.getText();
			String[] tokens = text.split(" ");
			String checkWord;
			ObservableList<String> list = FXCollections.observableArrayList();
			ListView wrongWordsList = new ListView();

			for (int i = 0; i < tokens.length; i++) {
				checkWord = spellCheck(tokens[i]);
				if (checkWord == null) {
					list.add(tokens[i]);
				}

				wrongWordsList = new ListView(list);
			}

			spellCheckGrid2.add(wrongWordsList, 2, 2);
			stage.setTitle("Spell Check");
			stage.setScene(new Scene(spellCheckPane, 250, 200));
			stage.show();

		});
		saveFile.setOnAction(e -> {
			fileSave(mainFile);
		});

		saveAsFile.setOnAction(e -> {
			File file = fileChooserSave(primaryStage);
			if (file != null) {
				mainFile = file;
				fileSave(file);
				Alert confirmationAlert = new Alert(AlertType.CONFIRMATION, "Save Completed!", ButtonType.OK);
				confirmationAlert.showAndWait();
				if (confirmationAlert.getResult() == ButtonType.OK) {
					confirmationAlert.close();
				}
			}
		});

		openFile.setOnAction(k -> {
			File file = fileChooserOpen(primaryStage);

			fileOpener(file);
			mainFile = file;
		});

		exitFile.setOnAction(e -> {
			Alert saveAlert = new Alert(AlertType.WARNING, "Exit without saving?", ButtonType.YES, ButtonType.CANCEL);
			saveAlert.showAndWait();
			if (saveAlert.getResult() == ButtonType.YES) {
				primaryStage.close();
			} else if (saveAlert.getResult() == ButtonType.CANCEL) {
				saveAlert.close();
			}
		});

		closeFile.setOnAction(e -> {

			Alert saveAlert = new Alert(AlertType.WARNING, "Close file without saving?", ButtonType.YES,
					ButtonType.CANCEL);
			saveAlert.showAndWait();
			if (saveAlert.getResult() == ButtonType.YES) {
				Alert closeAlert = new Alert(AlertType.CONFIRMATION, "File closed", ButtonType.OK);
				closeAlert.showAndWait();
				if (closeAlert.getResult() == ButtonType.OK) {
					mainFile = null;
					paragraphArea.clear();
				}
			} else if (saveAlert.getResult() == ButtonType.CANCEL) {
				saveAlert.close();
			}

		});

		wordCount.setOnAction(e -> {
			String paragraph = paragraphArea.getText();
			int wordCounted = wordCount(paragraph);
			Stage stage = new Stage();
			BorderPane wordCountPane = new BorderPane();
			GridPane wordCountGrid = new GridPane();
			Text wordCountText = new Text("Word Count: " + wordCounted);
			wordCountText.setFill(Color.BLACK);
			wordCountText.setFont(Font.font("Helvetica", 20));
			wordCountGrid.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
					+ "-fx-border-insets: 5;" + "-fx-border-radius: 7;" + "-fx-border-color: derive(orange, +50%);"
					+ "-fx-background-color: yellow;" + "-fx-background-color: derive(yellow, +50%);");
			wordCountGrid.setPadding(new Insets(80));
			wordCountGrid.add(wordCountText, 2, 4);

			wordCountPane.setCenter(wordCountGrid);

			stage.setTitle("Word Count");
			stage.setScene(new Scene(wordCountPane, 250, 70));
			stage.show();
		});

		sentenceCount.setOnAction(e -> {
			Stage stage = new Stage();
			BorderPane sentenceCountPane = new BorderPane();
			GridPane sentenceCountGrid = new GridPane();
			Text sentenceCountText = new Text("Sentence Count: " + sentenceCountTextFile);
			sentenceCountText.setFont(Font.font("Helvetica", 20));
			sentenceCountGrid.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
					+ "-fx-border-insets: 5;" + "-fx-border-radius: 7;" + "-fx-border-color: derive(orange, +50%);"
					+ "-fx-background-color: yellow;" + "-fx-background-color: derive(yellow, +50%);");
			sentenceCountGrid.setPadding(new Insets(80));
			sentenceCountGrid.add(sentenceCountText, 2, 4);

			sentenceCountPane.setCenter(sentenceCountGrid);

			stage.setTitle("Sentence Count");
			stage.setScene(new Scene(sentenceCountPane, 250, 70));
			stage.show();
		});

		fleschScore.setOnAction(e -> {
			Stage stage = new Stage();
			BorderPane fleschScorePane = new BorderPane();
			GridPane fleschScoreGrid = new GridPane();
			double fleschScoreTextFile = fleschScore(wordCountTextFile, sentenceCountTextFile, syllableCountTextFile);
			fleschScoreTextFile = Math.round(fleschScoreTextFile * 100) / 100;
			Text fleschScoreText = new Text("Flesch Score: " + fleschScoreTextFile);
			fleschScoreText.setFont(Font.font("Helvetica", 20));
			fleschScoreGrid.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
					+ "-fx-border-insets: 5;" + "-fx-border-radius: 7;" + "-fx-border-color: derive(orange, +50%);"
					+ "-fx-background-color: yellow;" + "-fx-background-color: derive(yellow, +50%);");
			fleschScoreGrid.setPadding(new Insets(80));
			fleschScoreGrid.add(fleschScoreText, 3, 7);
			fleschScorePane.setCenter(fleschScoreGrid);

			stage.setTitle("Flesch Score");
			stage.setScene(new Scene(fleschScorePane, 250, 70));
			stage.show();
		});

		markov.setOnAction(e -> {
			Stage stage = new Stage();
			BorderPane markovPane = new BorderPane();
			GridPane markovGrid = new GridPane();
			Button generateParagraph = new Button("Generate Paragraph");
			TextField wordField = new TextField();
			TextField numberField = new TextField();
			markovGrid.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 2;"
					+ "-fx-border-insets: 5;" + "-fx-border-radius: 7;" + "-fx-border-color: derive(orange, +50%);"
					+ "-fx-background-color: yellow;" + "-fx-background-color: derive(yellow, +50%);");
			wordField.setPromptText("Word");
			numberField.setPromptText("Length of Sentence");
			markovGenerator(paragraphArea.getText());
			generateParagraph.setOnAction(c -> {

				String word = wordField.getText();
				int number = Integer.parseInt(numberField.getText());
				String randomWord = word;
				if (masterList.find(randomWord) != null) {
					paragraphArea.setText("");
					paragraphArea.appendText(randomWord + " ");
					for (int i = 0; i < number - 1; i++) {

						int value = masterList.find(randomWord).counter;
						int index = (int) (Math.random() * value);

						MasterLink link = masterList.find(randomWord);
						BabyLink babyLink = link.babyList.getFirst();
						for (int j = 0; j < index; j++) {
							babyLink = babyLink.getNext();
						}
						try {
							randomWord = babyLink.getWord();
						} catch (NullPointerException g) {
							break;
						}
						paragraphArea.appendText(randomWord + " ");

					}
				} else {
					Alert cantFindWordAlert = new Alert(AlertType.WARNING, "Can't find word in document.",
							ButtonType.OK);
					cantFindWordAlert.showAndWait();
					if (cantFindWordAlert.getResult() == ButtonType.OK) {
						cantFindWordAlert.close();
					}
				}
				stage.close();
			});

			markovGrid.setPadding(new Insets(30));
			markovGrid.add(wordField, 5, 4);
			markovGrid.add(numberField, 5, 6);
			markovGrid.add(generateParagraph, 5, 8);
			markovPane.setCenter(markovGrid);

			stage.setTitle("Markov");
			stage.setScene(new Scene(markovPane, 220, 110));
			stage.show();
		});
		bPane.setTop(menuBar);
		bPane.setCenter(paragraphArea);
		bPane.setBottom(label);
		bPane.setAlignment(paragraphArea, Pos.CENTER);
		Scene scene = new Scene(bPane);
		primaryStage.setTitle("Word Processor");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public void dictionary() {
		String word;
		try {
			File dictionaryFile = new File("inputData/dictionary.txt");

			Scanner in = new Scanner(dictionaryFile);
			while (in.hasNextLine()) {
				word = in.nextLine();
				Pattern wordPattern = Pattern.compile("[!@#$%^&*,']");
				Matcher match = wordPattern.matcher(word);
				if (match.find()) {
					String s = match.group();
					word = word.replaceAll("\\" + s, "");
				}
				dictionaryHashMap.put(word, word);
			}

			in.close();
		} catch (FileNotFoundException g) {
			System.out.println("File not found!");
		} catch (Exception g) {
			g.printStackTrace();
		}
	}

	public String spellCheck(String word) {
		word = word.toLowerCase();
		String checkWord;
		Pattern wordPattern = Pattern.compile("[!@#$%^&*,'.?:;/\"... ]");
		Matcher match = wordPattern.matcher(word);
		if (match.find()) {
			String s = match.group();
			word = word.replaceAll("\\" + s, "");
		}
		checkWord = dictionaryHashMap.get(word);
		if (checkWord != null) {
			return checkWord;
		} else {
			return null;
		}
	}

	public File fileChooserOpen(Stage primaryStage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open File");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(primaryStage);
		return file;
	}

	public File fileChooserSave(Stage primaryStage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog(primaryStage);
		return file;
	}

	public void fileOpener(File file) {
		String text = "";
		if (file != null) {
			try {
				Scanner in = new Scanner(file).useDelimiter("\\A");
				text = in.next();
				paragraphArea.appendText(text);
				in.close();
			} catch (FileNotFoundException g) {
				System.out.println("File not found!");
			} catch (Exception g) {
			}
			markovGenerator(text);
		}
	}

	public void markovGenerator(String text) {
		masterList = new MasterLinkedList();
		String[] tokens = text.split("\\s");
		String nextWord = null;
		for (int i = 0; i < tokens.length; i++) {
			String word = tokens[i];
			if (masterList.isEmpty()) {
				masterList.insertFirst(word);
			} else if (masterList.find(word) == null) {
				masterList.insertFirst(word);
			}

			if (masterList.getFirst().hasNext()) {
				masterList.find(nextWord).setBabyLink(word);
			}
			nextWord = word;
		}
	}

	public void fileSave(File file) {
		String text = paragraphArea.getText();

		try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(text);
			fileWriter.close();
		} catch (FileNotFoundException | NullPointerException e) {
			Alert alert = new Alert(AlertType.ERROR, "File not found!", ButtonType.OK);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.OK) {
				alert.close();
			}
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR, "Could not save", ButtonType.OK);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.OK) {
				alert.close();
			}
		}
	}

	public int syllableCheckerTyped(String paragraph) {
		String patt = "[AEIOUYaeiouy]+";
		int syllable = 0;
		Pattern pattern = Pattern.compile(patt);
		Matcher match = pattern.matcher(paragraph);
		int counter = 0;
		while (match.find()) {
			counter++;
		}
		if (paragraph.matches("[AEIOUaeiou]+[A-z]*(e )")) {
			counter--;
		}
		syllable = syllable + counter;
		return syllable;
	}

	public int sentenceCheckerTyping(String paragraph) {
		Pattern pattern = Pattern.compile("\\w([.]|[?]|[!])");
		Matcher matcher = pattern.matcher(paragraph);

		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}

	public int wordCount(String paragraph) {
		Pattern pattern = Pattern.compile("\\w+");
		Matcher matcher = pattern.matcher(paragraph);

		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;

	}

	public double fleschScore(int wordTotal, int sentenceTotal, int syllableTotal) {
		double fleschScore = 0;
		if (sentenceTotal != 0 && wordTotal != 0) {
			fleschScore = 206.835 - 1.015 * ((double)wordTotal / sentenceTotal) - 84.6 * ((double)syllableTotal / wordTotal);
		}
		return fleschScore;
	}

	public void copy() {
		String text = paragraphArea.getSelectedText();
		content.putString(text);
		clipboard.setContent(content);
	}

	public void paste() {
		String pastedText = clipboard.getString();
		String wholeText = paragraphArea.getText();
		IndexRange range = paragraphArea.getSelection();
		String firstHalf = wholeText.substring(0, range.getEnd());
		String secondHalf = wholeText.substring(range.getEnd(), wholeText.length());
		paragraphArea.setText(firstHalf + pastedText + secondHalf);
	}

	public void cut() {
		copy();
		IndexRange range = paragraphArea.getSelection();
		String wholeText = paragraphArea.getText();
		String firstHalf = wholeText.substring(0, range.getStart());
		String secondHalf = wholeText.substring(range.getEnd(), wholeText.length());
		paragraphArea.setText(firstHalf + secondHalf);
	}

	public void delete() {
		IndexRange range = paragraphArea.getSelection();
		String wholeText = paragraphArea.getText();
		String firstHalf = wholeText.substring(0, range.getStart());
		String secondHalf = wholeText.substring(range.getEnd(), wholeText.length());
		paragraphArea.setText(firstHalf + secondHalf);
	}
}
