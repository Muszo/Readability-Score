package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Readability {
    private Scanner scanner = new Scanner(System.in);
    private final String text;
    private final int sentences;
    private final int characters;
    private final String[] words;
    private final int syllables;
    private final int polySyllables;


    public Readability(String filePath) {
        text = fileContentToString(filePath);
        characters = text.replaceAll("\\s+", "").length();
        words = text.replaceAll("e[.,?!]?\\b", "x").toLowerCase().split("\\s+");
        sentences = sentenceCount(words);
        syllables = countSyllables(words);
        polySyllables = countPolysyllables(words);
    }

    private int countPolysyllables(String[] arrayOfWords) {
        int count = 0;
        for (String word : arrayOfWords) {
            if (syllableCountInWord(word) > 2) {
                count++;
            }
        }
        return count;
    }


    private int countSyllables(String[] arrayOfWords) {
        int count = 0;
        for (String word : arrayOfWords) {
            count += syllableCountInWord(word);

        }
        return count;
    }

    private int syllableCountInWord(String word) {
        char[] letters = word.replaceAll("[aeiouy][aeiouy][aeiouy]", "y").toCharArray();
        boolean isPreviousConsonant = true;
        int count = 0;
        if (containsVowel(word)) {
            for (char c : letters) {
                if (isVowel(c) && isPreviousConsonant) {
                    isPreviousConsonant = false;
                    count++;
                } else {
                    isPreviousConsonant = true;
                }
            }
        } else {
            return 1;
        }
        return count;
    }

    private boolean containsVowel(String word) {
        return word.matches("(.*)[aeiouy](.*)");
    }

    private boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' ||
                c == 'o' || c == 'u' || c == 'y';
    }

    //this one is ugly
    private int sentenceCount(String[] words) {
        int count = 0;
        for (String word : words) {
            if (word.contains(".")
                    || word.contains("!")
                    || word.contains("?")) {
                count++;
            }
        }
        if (!(words[words.length - 1].contains(".")
                || words[words.length - 1].contains("!")
                || words[words.length - 1].contains("?"))) {
            count++;
        }
        return count;
    }

    private String fileContentToString(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(readFile(filePath))) {
            while (scanner.hasNext())
                stringBuilder.append(scanner.nextLine()).append(" ");
        } catch (FileNotFoundException e) {
            System.err.println("Ex: " + e);
        }

        return stringBuilder.toString();
    }

    public void displayMenu() {

        System.out.printf("The text is:%n%s%n%n" +
                "Words: %d%n" +
                "Sentences: %d%n" +
                "Characters: %d%n" +
                "Syllables: %d%n" +
                "Polysyllables: %d%n" +
                "Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ", text, words.length, sentences, characters, syllables, polySyllables);
        String input = scanner.nextLine();
        System.out.println();
        menu(input);
    }

    private void menu(String option) {
        switch (option) {
            case "ARI":
                System.out.printf("Automated Readability Index: %.2f (about " + getAge(ARIScore()) + " year olds).%n", ARIScore());
                break;
            case "FK":
                System.out.printf("Flesch-Kincaid readability tests: %.2f (about " + getAge(FKScore()) + " year olds).%n", ARIScore());
                break;
            case "SMOG":
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about " + getAge(SMOGScore()) + " year olds).%n", SMOGScore());
                break;
            case "CL":
                System.out.printf("Coleman-Liau: %.2f (about" + getAge(CLIScore()) + " year olds).%n", CLIScore());
                break;
            case "all":
                System.out.printf("Automated Readability Index: %.2f (about " + getAge(ARIScore()) + " year olds).%n", ARIScore());
                System.out.printf("Flesch-Kincaid readability tests: %.2f (about " + getAge(FKScore()) + " year olds).%n", FKScore());
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about " + getAge(SMOGScore()) + " year olds).%n", SMOGScore());
                System.out.printf("Coleman-Liau: %.2f (about " + getAge(CLIScore()) + " year olds).%n", CLIScore());
                break;
        }
        double average = (getAge(ARIScore()) + getAge(ARIScore()) + getAge(SMOGScore()) + getAge(CLIScore())) / 4.0;
        System.out.printf("%nThis text should be understood in average by %.2f year olds.", average);
    }

    //formulas
    private double ARIScore() {
        return 4.71 * ((double) characters / (double) words.length) + 0.5 * ((double) words.length / (double) sentences) - 21.43;
    }

    private double FKScore() {
        return 0.39 * ((double) words.length / (double) sentences) + 11.8 * ((double) syllables / (double) words.length) - 15.59;
    }

    private double SMOGScore() {
        return 1.043 * Math.sqrt((double) polySyllables * (30.0 / (double) sentences)) + 3.1291;
    }

    private double CLIScore() {
        double L = ((double) characters / (double) words.length) * 100.0;
        double S = ((double) sentences / (double) words.length) * 100.0;
        return (0.0588 * L) - (0.296 * S) - 15.8;
    }

    private int getAge(double index) {
        int[] age = {6, 7, 9, 10, 11, 12, 13, 14,
                15, 16, 17, 18, 24, 24};

        int roundedScore = (int) Math.round(index);
        if (roundedScore > 14) {
            roundedScore = 14;
        }
        return age[roundedScore - 1];
    }

    private File readFile(String filePath) {
        return new File(filePath);
    }
}
