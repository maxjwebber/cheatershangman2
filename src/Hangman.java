package src;

import java.io.*;
import java.util.*;



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
            System.out.println("Ok. I'm thinking of a word with "+size+" letters. You have "+chances+"chances.");
            Set<String> dictionary = buildDictionary("words.txt",size);
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


    public static Set<String> buildDictionary(String filename,int wordSize) throws FileNotFoundException
    {
        Scanner s = new Scanner(new File(filename));
        Set<String> output = new HashSet<>() {
        };
        String current;
        while (s.hasNext())
        {
            current = s.nextLine();
            if (current.length()==wordSize)
            output.add(current);
        }
        s.close();
        return output;
    }

    public static boolean playGame(Set<String> dictionary, Scanner scan, int size,int chancesLeft)
    {
        //TODO: array of sets? orrrrrrrr?

        String guess;
        Random rando = new Random();
        String chosenWord = null;
        int chosenIndex;
        Set<Character> chosenLetters = new HashSet<>();
        boolean wordNotGuessed = true;
        drawHangman(chancesLeft,chosenWord,chosenLetters,size);
        do {
            System.out.println("Type a single letter or the whole word to guess:");
            guess = scan.next().toLowerCase();
            if (chosenWord == null) { //CHEAT MODE the program would not have chosen a word unless absolutely forced to.
                if (guess.length() == 1) // letter guess
                {
                    if (wouldBeEmpty(dictionary, guess)) { // only if the list would otherwise be empty will a word be chosen.

                        //TODO: pick a damn word
                        chosenLetters.add(guess.charAt(0));
                    }
                    else {
                        if (guess.length() == 1)
                        {
                            //TODO: remove all words that have the letter.
                        }
                        chancesLeft--;
                    }
                }
                else if (guess.length() == size) // whole word guess
                {
                    eliminate(dictionary, guess); // the program eliminates the word from the list, if it exists.
                    chancesLeft--;
                }
                else // invalid guess. won't count against the player :)
                {
                    System.out.println("Your guess is the wrong size. Please enter a single letter or a " + size + "-letter word.");
                }
            }
            else // NON-CHEAT MODE. only if a word is actually chosen, which is rare.
                {
                    if (guess.length() == 1) // letter guess. check to see if our word contains the letter.
                    {
                        if (chosenWord.indexOf(guess.charAt(0)) == -1)
                            chancesLeft--;
                        else
                        {
                            chosenLetters.add(guess.charAt(0));
                            if (chosenLetters.size() == chosenWord.length())
                                wordNotGuessed = false;
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
                        else
                            chancesLeft--;
                    }
                    else // invalid guess. won't count against the player :)
                    {
                        System.out.println("Your guess is the wrong size. Please enter a single letter or a " + size + "-letter word.");
                    }
                }
            drawHangman(chancesLeft,chosenWord,chosenLetters,size);
        }while (chancesLeft != 0 && wordNotGuessed);
        if (wordNotGuessed)
        {
            if (chosenWord==null)
            {
                chosenIndex = rando.nextInt(dictionary.size() + 1);
                chosenWord = dictionary.get(chosenIndex);
            }
            System.out.println("The word was "+chosenWord+".");
            return false;
        }
        return true;
    }

    public static Set<String> makeLetterSet (Set<String> universe,char letter){}

    public static boolean wouldBeEmpty(Set<String> dictionary, String guess)
    {
        for (String word: dictionary) // if a word in the list doesn't contain the letter, then the list won't be empty
        {
            if (!word.contains(guess))
            {
                return false;
            }
        }
        return true;
    }



    public static void drawHangman(int chancesLeft,String chosenWord,Set<Character> chosenLetters, int size)
    {
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
}

