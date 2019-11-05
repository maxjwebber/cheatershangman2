package src;

import java.io.*;
import java.util.*;

//Ben Blouin & Max Webber
//CIS 2168, Dr. Rosen

public class Hangman
{

    public static void main(String[] args) throws FileNotFoundException
    {
        boolean playAgain;
        int size;
        char choice;
        int chances;
        Scanner scan = new Scanner(System.in);
        do
        {
            playAgain = false;
            System.out.println("Welcome to a normal game of Hangman! Let's begin. :)");
            System.out.println("What length should the word be?");
            do {
                size = scan.nextInt();
                if (size>28)
                    System.out.println("I don't know any words that big...can you pick a smaller number?");
                if (size<2)
                    System.out.println("I don't know any words that small...can you pick a bigger number?");
            }while (size>28||size<2);
            System.out.println("How many chances should I give you to fail?");
            System.out.println("Uh, I mean, how many chances should I give you to win? :)");
            do {
                chances = scan.nextInt();
                if (chances<1)
                    System.out.println("Come on, give yourself at least one chance.");
            }while (chances<1);
            System.out.println("Ok. I'm thinking of a word with "+size+" letters. You have "+chances+" chances.");
            Set<String> dictionary = buildDictionary(size);
            if (playGame(dictionary,scan,size,chances)) // returns true if win, false if loss.
                System.out.println("Wow...you won. That was unexpected.");
            else
                System.out.println("I win. :) Don't feel bad. I'm really good at this.");

            System.out.println("Would you like to play again? Enter Y for Yes, N for No.");

            do {
                choice = Character.toUpperCase(scan.next().charAt(0));
                switch (choice)
                {
                    case 'Y': playAgain = true;
                        break;
                    case 'N': System.out.println("Goodbye!");
                        break;
                    default: System.out.println("Invalid entry. Enter Y to play again or N to quit.");
                        break;
                }
            }while (choice != 'Y' && choice != 'N');
        }while(playAgain);
        scan.close();
    }


    private static Set<String> buildDictionary(int wordSize) throws FileNotFoundException
    {
        Scanner s = new Scanner(new File("words.txt"));
        Set<String> output = new HashSet<>() {
        };
        String current;
        while (s.hasNext())
        {
            current = s.nextLine().toLowerCase();
            if (current.length()==wordSize) {
                output.add(current);
            }
        }
        s.close();
        return output;
    }

    private static boolean playGame(Set<String> dictionary, Scanner scan, int size, int chancesLeft)
    {
        String guess;
        Random rando = new Random();
        String chosenWord = null;
        Set<String> wrongGuesses = new HashSet<>();
        Set<Character> chosenLetters = new HashSet<>();
        boolean wordNotGuessed = true;

        drawHangman(chancesLeft, null,chosenLetters,size);
        do {
            System.out.println("Type a single letter or the whole word to guess:");
            guess = scan.next().toLowerCase();

            //////////////////////////////////////////////////////////////////////////////////
            //CHEAT MODE the program would not have chosen a word unless absolutely forced to.
            ///////////////////////////////////////////////////////////////////////////////////
            // chosenWord is null UNTIL the cheater is FORCED to pick a word

            if (chosenWord == null) { //CHEAT MODE the program would not have chosen a word unless absolutely forced to.
                if (guess.length() == 1) // User making letter guess
                {
                    // if the word-list-set would become empty with a set-difference, FORCED to choose word as answer
                    if (wouldBeEmpty(dictionary, guess)) {

                        chosenWord = forceAnswerChoice(dictionary);
                        chosenLetters.add(guess.charAt(0));
                    }
                    else {
                        //TODO:     SET DIFFERENCE
                        String finalGuess = guess; // this makes the variable "more final", aka less-volatile

                        // lambda predicate filter: if string x contains
                        // the single character varaible final guess, remove
                        dictionary.removeIf(x->x.contains(finalGuess));

                        // uncomment to test output
                        // dictionary.forEach(System.out::println);
                        chancesLeft--;
                        wrongGuesses.add(finalGuess);
                    }
                }
                else if (guess.length() == size) // whole word guess
                {
                    // Returns true if this set contained the element (or equivalently, if this set changed as a result of the call).
                    // (This set will not contain the element once the call returns.)
                    dictionary.remove(guess);
                    wrongGuesses.add(guess);
                    chancesLeft--;
                }
                else // invalid guess. won't count against the player :)
                {
                    System.out.println("Your guess is the wrong size. Please enter a single letter or a " + size + "-letter word.");
                }
            }

            /////////////////////////////////////////////////////////////////////////
            // NON-CHEAT MODE. only if a word is actually chosen, which is rare.
            /////////////////////////////////////////////////////////////////////////
            else
            {
                if (guess.length() == 1) // letter guess. check to see if our word contains the letter.
                {
                    if (chosenWord.indexOf(guess.charAt(0)) == -1)
                    {
                        chancesLeft--;
                        wrongGuesses.add(guess);
                    }
                    else
                    {
                        chosenLetters.add(guess.charAt(0));
                        for (int i = 0; i < chosenWord.length(); i++)
                        {
                            if(!chosenLetters.contains(chosenWord.charAt(i)))
                                break;
                            if (i==chosenWord.length()-1)
                                wordNotGuessed = false;
                        }
                    }
                }
                else if (guess.length() == size) // whole word guess. the player can win here (hopefully not)
                {
                    if (guess.equals(chosenWord))
                    {
                        wordNotGuessed = false;
                        chosenLetters.clear();
                        for (int i = 0; i < size; i++)
                        {
                            chosenLetters.add(chosenWord.charAt(i));
                        }
                    }
                    else {
                        chancesLeft--;
                        wrongGuesses.add(guess);
                    }
                }
                else // invalid guess. won't count against the player :)
                {
                    System.out.println("Your guess is the wrong size. Please enter a single letter or a " + size + "-letter word.");
                }
            }
            drawHangman(chancesLeft,chosenWord,chosenLetters,size);
            System.out.println("These guesses were wrong: "+wrongGuesses);
        }while (chancesLeft != 0 && wordNotGuessed);
        if (wordNotGuessed)
        {
            if (chosenWord==null)
            {
                // Force choose answer
                //
                chosenWord=forceAnswerChoice(dictionary);
            }
            System.out.println("The word was "+chosenWord+".");
            return false;
        }
        return true;
    }

    public static Set<String> makeLetterSet (Set<String> universe,char letter){
        return null;
    }

    private static boolean wouldBeEmpty(Set<String> dictionary, String guess) {
        for (String word: dictionary) // if a word in the list doesn't contain the letter, then the list won't be empty
        {
            if (!word.contains(guess))
            {
                return false;
            }
        }
        return true;
    }

    private static void drawHangman(int chancesLeft, String chosenWord, Set<Character> chosenLetters, int size) {
        System.out.print(" ___\n|   |\n|   ");
        if (chancesLeft < 6) // the head
        {
            System.out.print("0");
        }
        System.out.print("\n|  ");
        if (chancesLeft<4) // the left arm
            System.out.print("-");
        else
            System.out.print(" ");
        if (chancesLeft<5)
            System.out.print("|"); // the body
        else
            System.out.print(" ");
        if (chancesLeft<3)
            System.out.print("-"); // the right arm
        System.out.print("\n|  ");
        if (chancesLeft<2)
            System.out.print("/"); //
        else
            System.out.print(" ");
        if (chancesLeft<1)
            System.out.print(" \\");
        System.out.println("\n");
        if (chosenWord == null)
        {
            for (int i = 0; i < size; i++)
            {
                System.out.print("_ ");
            }
        }
        else
        {
            for (int i = 0; i < size; i++)
            {
                if (chosenLetters.contains(chosenWord.charAt(i)))
                    System.out.print(chosenWord.charAt(i)+" ");
                else
                    System.out.print("_ ");
            }
        }
    }

    private static String forceAnswerChoice(Set<String> dictionary){
        Random rando = new Random();
        Object[] chooseTheWord = dictionary.toArray();
        return chooseTheWord[rando.nextInt(dictionary.size())].toString(); // random string
    }
}

