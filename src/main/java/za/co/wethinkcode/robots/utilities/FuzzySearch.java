package za.co.wethinkcode.robots.utilities;

import java.util.ArrayList;
import java.util.List;

public class FuzzySearch {

    /**
     * Calculates the minimum number of single-character edits (insertions, deletions, or substitutions)
     * required to change one word into the other.
     * 
     * This implements the Levenshtein distance algorithm using dynamic programming.
     *
     * @param wordA the first word
     * @param wordB the second word
     * @return the minimum edit distance between wordA and wordB
     */
    private static int calculateMinimumEdits(String wordA, String wordB) {
        List<List<Integer>> matrix = new ArrayList<>();

        // Create an empty matrix
        for (int i = 0; i <= wordA.length(); i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j <= wordB.length(); j++) {
                if (i == 0) row.add(j);
                else if (j == 0) row.add(i);
                else row.add(0);
            }
            matrix.add(row);
        }

        // Fill the matrix
        for (int i = 1; i <= wordA.length(); i++) {
            for (int j = 1; j <= wordB.length(); j++) {
                int cost = wordA.charAt(i-1) == wordB.charAt(j-1) ? 0 : 1;

                int deletion = matrix.get(i-1).get(j) + 1;
                int insertion = matrix.get(i).get(j-1) + 1;
                int substitution = matrix.get(i-1).get(j-1) + cost;

                matrix.get(i).set(j, Math.min(
                    Math.min(deletion, insertion),
                    substitution
                ));
            }
        }

        return matrix.get(wordA.length()).get(wordB.length());
    }

    /**
     * Calculates a similarity score between two words based on their minimum edit distance.
     *
     * The score is between 0 and 1, where 1 means the words are identical,
     * and 0 means they are completely different.
     * 
     * @param wordA the first word
     * @param wordB the second word
     * @return similarity score between wordA and wordB
     */
    private static double calculateSimilarityScore(String wordA, String wordB) {
        int distance = calculateMinimumEdits(wordA, wordB);
        return 1 - ((double) distance / Math.max(wordA.length(), wordB.length()));
    }

    /**
     * A record holding a word and its similarity score.
     */
    private record WordScore(String word, double similarityScore) {}

    /**
     * Finds and returns a list of words from the input list that have a similarity score
     * greater than or equal to the given threshold compared to the match word.
     *
     * @param words     the list of words to search
     * @param match     the target word to compare against
     * @param threshold the similarity threshold (0-100) to include words
     * @return a list of words from 'words' similar to 'match' above the threshold
     */
    public static List<String> find(List<String> words, String match, double threshold) {
        List<String> results = new ArrayList<>();

        List<WordScore> similarity = new ArrayList<>();

        for (String word : words) {
            double score = calculateSimilarityScore(word, match);
            similarity.add(new WordScore(word, score));
        }

        for (WordScore wordScore : similarity) {
            if (wordScore.similarityScore >= (threshold / 100)) {
                results.add(wordScore.word);
            }
        }

        return results;
    }
}
