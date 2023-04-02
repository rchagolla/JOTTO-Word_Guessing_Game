public class Driver {
  public static void main(String[] args) {
    String filepath = "oiled.txt";
//    String filepath = "debug.txt";
//    String filepath = "wordList.txt";
    Jotto game = new Jotto(filepath);
    game.play();
  }
}