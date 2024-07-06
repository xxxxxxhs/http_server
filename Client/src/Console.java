import java.util.Scanner;

public class Console {
    private Scanner scanner;
    public Console(){
        scanner = new Scanner(System.in);
    }
    public String readLine() {
        return scanner.nextLine();
    }
    public String readLineWN() {
        String line = scanner.nextLine();
        String[] lines = line.split("\n");
        for (int i = 0; lines.length > i; i++) {
            if (i < lines.length - 1) lines[i] += "\n";
        }
        System.out.println(String.join("", lines));
        return String.join("", lines);
    }
}
