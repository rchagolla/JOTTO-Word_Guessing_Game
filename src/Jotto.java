import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/*
 * Name: Rolando Chagolla-Bonilla
 * Date: 7 - Feb - 2023
 * Description: This is a Jotto class that runs a word-guessing game with text-file that is input
 * and has a simple UI for player to use.
 */

public class Jotto {

  // used to keep track of the player score and to ensure words entered are
  // the correct length.
  private static final int WORD_SIZE = 5;

  // This is a String that holds the name of the file that stores the words
  // for the game.
  private final String filename;

  //  used to change the display when the program is being debugged.
  private static final boolean DEBUG = true;

  // It will be used to store the words that have
  // been selected from the wordList during the current game.
  private final ArrayList<String> playedWords = new ArrayList<>();

  // This is used to hold the words read from the text file.
  // Words will be chosen randomly from this list for the player to guess.
  private final ArrayList<String> wordList = new ArrayList<>();

  // It will hold the words that the player has guessed.
  private final ArrayList<String> playerGuesses = new ArrayList<>();

  // This is a String that stores the currently selected word that the
  // player is trying to guess.
  private String currentWord;

  // This is an int that is used to store the player's current score.
  private int score;

  // Constructor reads words from file input.
  public Jotto(String filename) {
    this.filename = filename;
    readWords();
  }

  // This method uses the field filename to open a file and read the contents.
  private void readWords() {
    File f = new File(filename);

    Scanner fileIn = null;

    // opens file
    try {
      fileIn = new Scanner(f);
    } catch (FileNotFoundException e) {
      System.out.println("couldn't open " + filename);
    }

    // reads contents
    while (fileIn != null && fileIn.hasNext()) {
      String input = fileIn.nextLine();
      if (!wordList.contains(input)) {
        wordList.add(input);
      }
    }
  }

  // This method represents the main menu loop for the game
  public void play() {
    Scanner scan = new Scanner(System.in);
    boolean loop = true;
    System.out.println("Welcome to the game.");
    System.out.println("Current Score: " + score());

    // UI loop
    while (loop) {
      System.out.println("=-=-=-=-=-=-=-=-=-=-=");
      System.out.println("Choose one of the following:");
      System.out.println("1.\tStart the game");
      System.out.println("2.\tSee the word list");
      System.out.println("3.\tSee the chosen words");
      System.out.println("4.\tShow Player guesses");
      System.out.println("zz to exit");
      System.out.println("=-=-=-=-=-=-=-=-=-=-=");
      System.out.print("What is your choice: ");
      //cin part
      String input = scan.nextLine();
      if (input.trim().equalsIgnoreCase("1") || input.trim().equalsIgnoreCase("one")) {
        if (pickWord()) {
          score += guess();
          System.out.println("Your Score is " + score);
          System.out.println("Press enter to continue");
          input = scan.nextLine();
        } else {
          showPlayerGuesses();
        }
      } else if (input.trim().equalsIgnoreCase("2") || input.trim().equalsIgnoreCase("two")) {
        showWordList();
      } else if (input.trim().equalsIgnoreCase("3") || input.trim().equalsIgnoreCase("three")) {
        showPlayedWords();
      } else if (input.trim().equalsIgnoreCase("4") || input.trim().equalsIgnoreCase("four")) {
        showPlayerGuesses();
      } else if (input.trim().equalsIgnoreCase("zz")) {
        loop = false;
      } else {
        System.out.println("I don't know what \"" + input + "\" is.");
        System.out.println("Press enter to continue");
        input = scan.nextLine();
      }
    }
  }

  // displays all the words that the computer may select for the player to guess.
  private void showWordList() {
    Scanner scan = new Scanner(System.in);
    System.out.println("Current word list:");
    for (String word : wordList) {
      System.out.println(word);
    }
    System.out.println("Press enter to continue");
    String input = scan.nextLine();
  }

  // Displays the words that have been chosen by the computer.
  private void showPlayedWords() {
    Scanner scan = new Scanner(System.in);
    if (playedWords.size() == 0) {
      System.out.println("No words have been played.");
    } else {
      System.out.println("Current list of played words:");
      for (String word : playedWords) {
        System.out.println(word);
      }
    }
    System.out.println("Press enter to continue");
    String input = scan.nextLine();
  }

  // This method prints all the words the player has guessed in the current game.
  private void showPlayerGuesses() {
    Scanner scan = new Scanner(System.in);
    String input;
    if (playerGuesses.size() == 0) {
      System.out.println("No guesses yet.");
    } else {
      System.out.println("Current player guesses:");
      for (String word : playerGuesses) {
        System.out.println(word);
      }
      System.out.println("Would you like to add the words to the word list? (y/n)");
      input = scan.nextLine();
      // updates wordList with guessed words
      if (input.trim().equalsIgnoreCase("y")) {
        updateWordList();
        showWordList();
      } else {
        System.out.println("Press enter to continue");
        input = scan.nextLine();
      }
    }
  }

  // This method is the main game loop where most of the processing is completed.
  private int guess() {
    // stores user's current guesses
    ArrayList<String> currentGuesses = new ArrayList<>();
    Scanner scan = new Scanner(System.in);
    // keeps letters that match count
    int letterCount = 0;
    int score = WORD_SIZE + 1;
    // user's guess
    String wordGuess;
    boolean loop = true;

    while (loop) {
      System.out.println("Current Score: " + score);
      System.out.print("What is your guess (q to quit): ");
      wordGuess = scan.nextLine();

      if (wordGuess.trim().equalsIgnoreCase("q")) {
        if (score > 0) {
          score = 0;
        }
        loop = false;
      } else if (wordGuess.length() != WORD_SIZE) {
        System.out.println(
            "Word must be 5 characters (" + wordGuess + " is " + wordGuess.length() + ")");
      } else {
        addPlayerGuess(wordGuess);
        if (wordGuess.equals(currentWord)) {
          // runs if guess is correct
          System.out.println("DINGDINGDING!!! the word was " + wordGuess);
          currentGuesses.add(wordGuess);
          playerGuessScores(currentGuesses);
          return score;
        }

        // checking if word has been played before
        if (currentGuesses.contains(wordGuess)) {
          System.out.println(wordGuess + " has already been guessed.");
          continue;
        }

        // if not repeat or correct word
        currentGuesses.add(wordGuess);
        letterCount = getLetterCount(wordGuess);
        if (letterCount != WORD_SIZE) {
          System.out.println(wordGuess + " has a Jotto score of " + letterCount);
        } else {
          System.out.println(wordGuess + " is an anagram.");
        }
        score--;
        playerGuessScores(currentGuesses);
      }
    }

    return score;
  }

  // This method checks each letter of wordGuess against each letter in currentWord
  // to see if there is a match.
  public int getLetterCount(String wordGuess) {
    // counts for each check
    int countA = 0;
    int countB = 0;

    // checks secret word against guess
    for (int i = 0; i < wordGuess.length(); i++) {
      // if dup is true then any letter is a duplicate
      boolean dup = false;
      // checks secret word against guess
      for (int k = 0; k < wordGuess.length(); k++) {
        if (currentWord.charAt(i) == wordGuess.charAt(k) && !dup) {
          countA++;
          dup = true;
        }
      }
    }

    // checks guess against secret word
    for (int i = 0; i < wordGuess.length(); i++) {
      // if dup is true then any letter is a duplicate
      boolean dup = false;
      // checks secret word against guess
      for (int k = 0; k < wordGuess.length(); k++) {
        if (wordGuess.charAt(i) == currentWord.charAt(k) && !dup) {
          countB++;
          dup = true;
        }
      }
    }

    // whichever for loop had fewer count is the one without duplicates
    if (countA < countB) {
      return countA;
    }
    return countB;
  }

  // This method will write the updated list of words to the file that is read at
  // the beginning of the application.
  private void updateWordList() {
    System.out.println("Updating word list.");
    /*
     * for the try-catch
     * Source: https://www.geeksforgeeks.org/java-program-to-write-into-a-file/
     * Author: geeksforgeeks
     * Date Published: 08 - Sep - 2022
     */
    try {
      /*
       * for append
       * Source: https://www.tutorialspoint.com/Java-Program-to-Append-Text-to-an-Existing-File
       * Author: karthikeya Boyini
       * Date Published: 13 - Mar - 2020
       */
      FileWriter fWriter = new FileWriter(filename, true);

      // adding playerGuess to wordList
      for (String guess : playerGuesses) {
        if (!wordList.contains(guess)) {
          wordList.add(guess);
          fWriter.write(guess + "\n");
        }
      }
      fWriter.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  // This method uses a Random object to select a word at random from the wordList.
  private boolean pickWord() {
    /*
     * for getting a Random number
     * Source: https://www.educative.io/answers/how-to-generate-random-numbers-in-java
     * Author: Educative
     * Date Published: N/A
     */
    Random rand = new Random();
    currentWord = wordList.get(rand.nextInt(wordList.size()));
    if (playedWords.contains(currentWord) && playedWords.size() == wordList.size()) {
      System.out.println("You've guessed them all!");
      return false;
    }
    if (playedWords.contains(currentWord)) {
      pickWord();
    }
    playedWords.add(currentWord);
    if (DEBUG) {
      System.out.println("The word is: " + currentWord);
    }
    return true;
  }

  int score() {
    return score;
  }

  // If playerGuesses does not already contain wordGuess, add wordGuess.
  private void addPlayerGuess(String wordGuess) {
    if (!playerGuesses.contains(wordGuess)) {
      playerGuesses.add(wordGuess);
    }
  }

  // Display all the words from guesses and the Jotto score associated with the word.
  private void playerGuessScores(ArrayList<String> guesses) {
    System.out.println("Guess\t\t\tScore");
    for (String guess : guesses) {
      System.out.println(guess + "\t\t\t" + getLetterCount(guess));
    }
    System.out.println("");
  }
}
