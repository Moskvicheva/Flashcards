package flashcards;
import java.io.*;
import java.util.*;


public class Main {
    public static Set<Card> cards = new HashSet<>();
    public static final Scanner sc = new Scanner(System.in);
    public static List<String> logs = new ArrayList<>();
    public static File exportFile;

    public static void main(String[] args) {
        boolean export = false;
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String command = args[i];
                switch (command) {
                    case "-import":
                        String fileImport = args[i + 1];
                        File file1 = new File(fileImport);
                        importCard(file1);
                        break;
                    case "-export":
                        String fileExport = args[i + 1];
                        exportFile = new File(fileExport);
                        export = true;
                        break;
                    default:
                        break;
                }
            }
        }
        String[] out = new String[1];
        while (!Objects.equals(out[0], "exit")) {
            start(out, export);
        }
    }

    public static void start (String[] out, boolean export) {
        logging("Input the action (add, remove, import, export, ask, exit):", false);
        String command = sc.nextLine();
        logging(command, true);
        out[0] = command;

            switch (command) {
                case "add":
                    addCard();
                    break;
                case "remove":
                    removeCard();
                    break;
                case "import":
                    importCard(importFile());
                    break;
                case "export":
                    exportCard(exportFile());
                    break;
                case "ask":
                    askCard();
                    break;
                case "log":
                    log();
                    break;
                case "hardest card":
                    hardestCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                default:
                    exit(exportFile, export);
                    break;
            }

    }

    public static void addCard() {
        logging("The card: ", false);
        String term = sc.nextLine();
        logging(term, true);
        boolean okTerm = true;
        for (Card card : cards) {
            if (Objects.equals(card.getTerm(), term)) {
                logging("The card \"" + term + "\" already exists. \n", false);
                okTerm = false;
            }
        }
        if (okTerm) {
            logging("The definition of the card:", false);
            String def = sc.nextLine();
            logging(def, true);
            boolean okDef = true;
            for (Card card : cards) {
                if (Objects.equals(card.getDef(), def)) {
                    logging("The definition \"" + def + "\" already exists. \n", false);
                    okDef = false;
                }
            }
            if (okTerm && okDef) {
                    Card newCard = new Card();
                    newCard.setTerm(term);
                    newCard.setDef(def);
                    newCard.setMistakes(0);
                    cards.add(newCard);
                    logging("The pair (\"" + term + "\":\"" + def + "\") has been added. \n",
                            false);
            }
        }
    }

    public static void removeCard() {
        logging("Which card?", false);
        String remCard = sc.nextLine();
        logging(remCard, true);
        boolean noCard = true;
        for (Card card : cards) {
            if (Objects.equals(card.getTerm(), remCard)) {
                cards.remove(card);
                logging("The card has been removed. \n", false);
                noCard = false;
                break;
            }
        }
        if (noCard) {
            logging("Can't remove \"" + remCard + "\": there is no such card. \n",
                    false);
        }
    }

    public static File importFile() {
        logging("File name: ", false);
        String path = sc.nextLine();
        logging(path, true);
        return new File(path);
    }

    public static void importCard (File file) {
        try (Scanner scanner = new Scanner(file)) {
            int numOfTerms = 0;
            while (scanner.hasNext()) {
                String[] line = scanner.nextLine().split(":");
                String term = line[0];
                String def = line[1];
                int mistakes = Integer.parseInt(line[2]);
                for (Card card : cards) {
                    if (Objects.equals(card.getTerm(), term)) {
                        cards.remove(card);
                        break;
                    }
                }
                Card newCard = new Card();
                newCard.setTerm(term);
                newCard.setDef(def);
                newCard.setMistakes(mistakes);
                cards.add(newCard);
                numOfTerms += 1;
            }
            logging(numOfTerms + " cards have been loaded. \n",
                    false);
        } catch(FileNotFoundException e){
            logging("File not found. \n", false);
        }
    }

    public static File exportFile() {
        logging("File name: ", false);
        String filePath = sc.nextLine();
        logging(filePath, true);
        return new File(filePath);
    }

    public static void exportCard (File file) {

        try (PrintWriter wr = new PrintWriter(file)) {
            for (Card card : cards) {
                wr.printf("%s:%s:%s\n", card.getTerm(),
                        card.getDef(), card.getMistakes());
            }
            logging(cards.size() + " cards have been saved. \n",
                    false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void askCard() {
        logging("How many times to ask?", false);
        int k = Integer.parseInt(sc.nextLine());
        logging(String.valueOf(k), true);
        Iterator<Card> i = cards.iterator();
        for (int j = 0; j < Math.min(k, cards.size()); j++) {
            Card curCard = i.next();
            String curTerm = curCard.getTerm();
            logging("Print the definition of \"" + curTerm +
                    "\": \n", false);
            String ans = sc.nextLine();
            logging(ans, true);
            String curDef = curCard.getDef();
            if (Objects.equals(ans, curDef)) {
                logging("Correct!", false);
            } else {
                boolean globallyWrong = true;
                for (Card card : cards) {
                    if (Objects.equals(ans, card.getDef())) {
                        logging("Wrong. The right answer is \"" +
                                curCard.getDef() + "\", " +
                                "but your definition is correct for \"" +
                                card.getTerm() + "\" card. \n",
                                false);
                        curCard.mistakes += 1;
                        globallyWrong = false;
                        break;
                    }
                }
                if (globallyWrong) {
                    logging("Wrong. The right answer is \"" +
                                    curCard.getDef() + "\". \n",
                            false);
                    curCard.mistakes += 1;
                }
            }
        }
    }

    public static void log() {
        logging("File name: ", false);
        String fileName = sc.nextLine();
        logging(fileName, true);
        File file = new File(fileName);
        try (PrintWriter logPrint = new PrintWriter(file)) {
            for (String str : logs) {
                logPrint.println(str);
            }
            logging("The log has been saved.", false);
        } catch (IOException e) {
            System.out.printf("An exception occurred %s", e.getMessage());
        }

    }

    public static void hardestCard() {
        int max = 1;
        List<Card> hardestCards = new ArrayList<>();
        for (Card card : cards) {
            if (card.getMistakes() >= max) {
                if (hardestCards.size() != 0 && hardestCards.get(0).getMistakes() < card.getMistakes()) {
                    hardestCards.clear();
                    max = card.getMistakes();
                }
                hardestCards.add(card);
            }

        }
        if (hardestCards.size() == 1) {
            logging("The hardest card is \"" +
                            hardestCards.get(0).getTerm() +
                    "\". You have " +  hardestCards.get(0).getMistakes() +
                            " errors answering it.",
                    false);
        } else if (hardestCards.size() > 1) {
            String output = "The hardest cards are ";
            for (int i = 0; i < hardestCards.size() - 1; i++) {
                output = output.concat("\"" + hardestCards.get(i).getTerm() +
                        "\"" + ", ");
            }
            output = output.concat( "\"" +
                     hardestCards.get(hardestCards.size() - 1).getTerm() + "\"." +
                            " You have " +  hardestCards.get(0).getMistakes() +
                    " errors answering them.");
            logging(output, false);
        } else {
            logging("There are no cards with errors.", false);
        }
    }

    public static void resetStats() {
        for (Card card : cards) {
            card.setMistakes(0);
        }
        logging("Card statistics have been reset.", false);
    }

    public static void exit (File file, boolean export) {
        logging("Bye bye!", false);
        if (export) {
            exportCard(file);
        }

    }

    public static void logging (String str, boolean isInput) {
        if (!isInput) {System.out.println(str); }
        logs.add(str);
    }

}
