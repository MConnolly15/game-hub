package com.example.gamehub.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.ModelAndView;

class HangmanControllerTest {

  private static final String WORD_KEY = "hangmanWord";
  private static final String GUESSED_KEY = "hangmanGuessedLetters";
  private static final String WRONG_COUNT_KEY = "hangmanWrongGuesses";
  private static final int MAX_WRONG_GUESSES = 6;

  private UserRepository userRepository;
  private HangmanController controller;
  private FakeHttpSession session;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    controller = new HangmanController(userRepository);
    session = new FakeHttpSession();
  }

  @Nested
  @DisplayName("GET /game/hangman")
  class LoadPageTests {

    @Test
    @DisplayName("creates a new word and empty state on first load")
    void firstLoadInitializesGameState() {
      ModelAndView mv = controller.loadPage(session, null);

      assertEquals("hangman", mv.getViewName());
      assertNotNull(session.getAttribute(WORD_KEY), "A word should be stored in the session");
      assertEquals("", mv.getModel().get("guessedLetters"));
      assertEquals(0, mv.getModel().get("wrongGuesses"));
      assertEquals(Boolean.FALSE, mv.getModel().get("won"));
      assertEquals(Boolean.FALSE, mv.getModel().get("lost"));
      assertNull(mv.getModel().get("error"));

      String displayWord = (String) mv.getModel().get("displayWord");
      String storedWord = (String) session.getAttribute(WORD_KEY);
      assertEquals(storedWord.length(), displayWord.replace(" ", "").length());
      assertTrue(displayWord.chars().allMatch(c -> c == '_' || c == ' '));
    }

    @Test
    @DisplayName("reuses the same word across repeated loads within a session")
    void repeatedLoadKeepsSameWord() {
      controller.loadPage(session, null);
      String firstWord = (String) session.getAttribute(WORD_KEY);

      controller.loadPage(session, null);
      String secondWord = (String) session.getAttribute(WORD_KEY);

      assertEquals(firstWord, secondWord);
    }

    @Test
    @DisplayName("does not add a username when Authentication is null")
    void noUsernameWhenAuthenticationNull() {
      ModelAndView mv = controller.loadPage(session, null);
      assertFalse(mv.getModel().containsKey("username"));
      verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("does not add a username when Authentication is not authenticated")
    void noUsernameWhenNotAuthenticated() {
      Authentication auth = mock(Authentication.class);
      when(auth.isAuthenticated()).thenReturn(false);

      ModelAndView mv = controller.loadPage(session, auth);

      assertFalse(mv.getModel().containsKey("username"));
      verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("adds username to model when user is authenticated and found")
    void addsUsernameWhenAuthenticatedAndFound() {
      Authentication auth = mock(Authentication.class);
      when(auth.isAuthenticated()).thenReturn(true);
      when(auth.getName()).thenReturn("jane@example.com");

      User user = mock(User.class);
      when(user.getUsername()).thenReturn("janedoe");
      when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

      ModelAndView mv = controller.loadPage(session, auth);

      assertEquals("janedoe", mv.getModel().get("username"));
    }

    @Test
    @DisplayName("omits username when authenticated user is not found in repository")
    void noUsernameWhenAuthenticatedButNotFound() {
      Authentication auth = mock(Authentication.class);
      when(auth.isAuthenticated()).thenReturn(true);
      when(auth.getName()).thenReturn("ghost@example.com");
      when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

      ModelAndView mv = controller.loadPage(session, auth);

      assertFalse(mv.getModel().containsKey("username"));
    }
  }

  @Nested
  @DisplayName("POST /game/hangman/guess")
  class GuessLetterTests {

    @Test
    @DisplayName("rejects null guess with an error and does not mutate game state")
    void nullGuessProducesError() {
      controller.loadPage(session, null);

      ModelAndView mv = controller.guessLetter(null, session, null);

      assertEquals("Guess must be a single letter", mv.getModel().get("error"));
      assertEquals("", mv.getModel().get("guessedLetters"));
      assertEquals(0, mv.getModel().get("wrongGuesses"));
    }

    @Test
    @DisplayName("rejects multi-character guess with an error")
    void multiCharGuessProducesError() {
      controller.loadPage(session, null);

      ModelAndView mv = controller.guessLetter("AB", session, null);

      assertEquals("Guess must be a single letter", mv.getModel().get("error"));
    }

    @Test
    @DisplayName("rejects non-letter guess with an error")
    void nonLetterGuessProducesError() {
      controller.loadPage(session, null);

      ModelAndView mv = controller.guessLetter("5", session, null);

      assertEquals("Guess must be a single letter", mv.getModel().get("error"));
    }

    @Test
    @DisplayName("rejects empty-string guess with an error")
    void emptyGuessProducesError() {
      controller.loadPage(session, null);

      ModelAndView mv = controller.guessLetter("", session, null);

      assertEquals("Guess must be a single letter", mv.getModel().get("error"));
    }

    @Test
    @DisplayName("a correct letter guess is recorded and does not add a wrong guess")
    void correctGuessIsRecorded() {
      controller.loadPage(session, null);
      String word = (String) session.getAttribute(WORD_KEY);
      String correctLetter = String.valueOf(word.charAt(0));

      ModelAndView mv = controller.guessLetter(correctLetter, session, null);

      assertNull(mv.getModel().get("error"));
      assertTrue(((String) mv.getModel().get("guessedLetters")).contains(correctLetter));
      assertEquals(0, mv.getModel().get("wrongGuesses"));
    }

    @Test
    @DisplayName("guesses are normalized to uppercase")
    void guessIsCaseInsensitive() {
      controller.loadPage(session, null);
      String word = (String) session.getAttribute(WORD_KEY);
      String lowerCaseLetter = String.valueOf(word.charAt(0)).toLowerCase();

      ModelAndView mv = controller.guessLetter(lowerCaseLetter, session, null);

      assertTrue(
          ((String) mv.getModel().get("guessedLetters")).contains(lowerCaseLetter.toUpperCase()));
    }

    @Test
    @DisplayName("an incorrect letter guess increments the wrong-guess counter")
    void incorrectGuessIncrementsWrongCount() {
      controller.loadPage(session, null);
      String word = (String) session.getAttribute(WORD_KEY);
      String wrongLetter = firstLetterNotInWord(word);

      ModelAndView mv = controller.guessLetter(wrongLetter, session, null);

      assertEquals(1, mv.getModel().get("wrongGuesses"));
      assertTrue(((String) mv.getModel().get("guessedLetters")).contains(wrongLetter));
    }

    @Test
    @DisplayName("guessing the same letter twice does not double count a wrong guess")
    void duplicateWrongGuessIsIgnored() {
      controller.loadPage(session, null);
      String word = (String) session.getAttribute(WORD_KEY);
      String wrongLetter = firstLetterNotInWord(word);

      controller.guessLetter(wrongLetter, session, null);
      ModelAndView mv = controller.guessLetter(wrongLetter, session, null);

      assertEquals(1, mv.getModel().get("wrongGuesses"));
    }

    @Test
    @DisplayName("guessing the same correct letter twice keeps guessed-letters set stable")
    void duplicateCorrectGuessIsIgnored() {
      controller.loadPage(session, null);
      String word = (String) session.getAttribute(WORD_KEY);
      String correctLetter = String.valueOf(word.charAt(0));

      controller.guessLetter(correctLetter, session, null);

      @SuppressWarnings("unchecked")
      Set<String> guessed = (Set<String>) session.getAttribute(GUESSED_KEY);
      long occurrences = guessed.stream().filter(l -> l.equals(correctLetter)).count();
      assertEquals(1, occurrences);
    }

    @Test
    @DisplayName("reaching the max wrong guesses marks the game as lost")
    void reachingMaxWrongGuessesLosesGame() {
      controller.loadPage(session, null);
      String word = (String) session.getAttribute(WORD_KEY);

      ModelAndView mv = null;
      int wrongCount = 0;
      char letter = 'A';
      while (wrongCount < MAX_WRONG_GUESSES) {
        if (word.indexOf(letter) < 0) {
          mv = controller.guessLetter(String.valueOf(letter), session, null);
          wrongCount++;
        }
        letter++;
        if (letter > 'Z') {
          break;
        }
      }

      assertNotNull(mv);
      assertEquals(Boolean.TRUE, mv.getModel().get("lost"));
      assertEquals(MAX_WRONG_GUESSES, mv.getModel().get("wrongGuesses"));
      assertEquals("Game over! The word was " + word, mv.getModel().get("message"));
    }

    @Test
    @DisplayName("guessing all letters of the word marks the game as won")
    void guessingAllLettersWinsGame() {
      controller.loadPage(session, null);
      String word = (String) session.getAttribute(WORD_KEY);

      ModelAndView mv = null;
      for (char c : distinctChars(word)) {
        mv = controller.guessLetter(String.valueOf(c), session, null);
      }

      assertNotNull(mv);
      assertEquals(Boolean.TRUE, mv.getModel().get("won"));
      assertEquals("You won! The word was " + word, mv.getModel().get("message"));
      assertEquals(word.replaceAll("(?<=.)(?=.)", " "), mv.getModel().get("displayWord"));
    }

    @Test
    @DisplayName("further guesses are ignored once the game has been won")
    void guessesIgnoredAfterWin() {
      controller.loadPage(session, null);
      String word = (String) session.getAttribute(WORD_KEY);
      for (char c : distinctChars(word)) {
        controller.guessLetter(String.valueOf(c), session, null);
      }
      Integer wrongCountAttr = (Integer) session.getAttribute(WRONG_COUNT_KEY);
      int wrongBefore = wrongCountAttr == null ? 0 : wrongCountAttr;

      String someWrongLetter = firstLetterNotInWord(word);
      ModelAndView mv = controller.guessLetter(someWrongLetter, session, null);

      assertEquals(wrongBefore, mv.getModel().get("wrongGuesses"));
      assertEquals(Boolean.TRUE, mv.getModel().get("won"));
    }

    @Test
    @DisplayName("further guesses are ignored once the game has been lost")
    void guessesIgnoredAfterLoss() {
      controller.loadPage(session, null);
      String word = (String) session.getAttribute(WORD_KEY);

      int wrongCount = 0;
      char letter = 'A';
      while (wrongCount < MAX_WRONG_GUESSES) {
        if (word.indexOf(letter) < 0) {
          controller.guessLetter(String.valueOf(letter), session, null);
          wrongCount++;
        }
        letter++;
        if (letter > 'Z') {
          break;
        }
      }

      char unused = 'A';
      @SuppressWarnings("unchecked")
      Set<String> guessed = (Set<String>) session.getAttribute(GUESSED_KEY);
      while (guessed.contains(String.valueOf(unused)) && unused <= 'Z') {
        unused++;
      }

      ModelAndView mv = controller.guessLetter(String.valueOf(unused), session, null);

      assertEquals(MAX_WRONG_GUESSES, mv.getModel().get("wrongGuesses"));
      assertEquals(Boolean.TRUE, mv.getModel().get("lost"));
    }
  }

  @Nested
  @DisplayName("POST /game/hangman/reset")
  class ResetGameTests {

    @Test
    @DisplayName("clears session state and starts a fresh game")
    void resetClearsSessionAndStartsNewGame() {
      controller.loadPage(session, null);
      String word = (String) session.getAttribute(WORD_KEY);
      controller.guessLetter(String.valueOf(word.charAt(0)), session, null);

      ModelAndView mv = controller.resetGame(session, null);

      assertEquals("", mv.getModel().get("guessedLetters"));
      assertEquals(0, mv.getModel().get("wrongGuesses"));
      assertEquals(Boolean.FALSE, mv.getModel().get("won"));
      assertEquals(Boolean.FALSE, mv.getModel().get("lost"));
      assertNotNull(session.getAttribute(WORD_KEY), "reset should immediately pick a new word");
    }
  }

  @Nested
  @DisplayName("Hangman ASCII art")
  class HangmanArtTests {

    @Test
    @DisplayName("art has no dangling limbs at zero wrong guesses")
    void zeroWrongGuessesShowsBareGallows() {
      ModelAndView mv = controller.loadPage(session, null);
      String art = (String) mv.getModel().get("hangmanArt");
      assertFalse(art.contains("O"));
    }

    @Test
    @DisplayName("art shows full figure at max wrong guesses")
    void maxWrongGuessesShowsFullFigure() {
      controller.loadPage(session, null);
      String word = (String) session.getAttribute(WORD_KEY);

      int wrongCount = 0;
      char letter = 'A';
      ModelAndView mv = null;
      while (wrongCount < MAX_WRONG_GUESSES) {
        if (word.indexOf(letter) < 0) {
          mv = controller.guessLetter(String.valueOf(letter), session, null);
          wrongCount++;
        }
        letter++;
        if (letter > 'Z') {
          break;
        }
      }

      assertNotNull(mv);
      String art = (String) mv.getModel().get("hangmanArt");
      assertTrue(art.contains("/|\\"));
      assertTrue(art.contains("/ \\"));
    }
  }

  private static char[] distinctChars(String word) {
    LinkedHashSet<Character> chars = new LinkedHashSet<>();
    for (char c : word.toCharArray()) {
      chars.add(c);
    }
    char[] result = new char[chars.size()];
    int i = 0;
    for (char c : chars) {
      result[i++] = c;
    }
    return result;
  }

  private static String firstLetterNotInWord(String word) {
    for (char c = 'A'; c <= 'Z'; c++) {
      if (word.indexOf(c) < 0) {
        return String.valueOf(c);
      }
    }
    throw new IllegalStateException("Word unexpectedly contains every letter of the alphabet");
  }

  static class FakeHttpSession implements HttpSession {
    private final Map<String, Object> attributes = new HashMap<>();

    @Override
    public Object getAttribute(String name) {
      return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
      attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
      attributes.remove(name);
    }

    @Override
    public long getCreationTime() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getId() {
      throw new UnsupportedOperationException();
    }

    @Override
    public long getLastAccessedTime() {
      throw new UnsupportedOperationException();
    }

    @Override
    public jakarta.servlet.ServletContext getServletContext() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxInactiveInterval() {
      throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Enumeration<String> getAttributeNames() {
      return java.util.Collections.enumeration(attributes.keySet());
    }

    @Override
    public void invalidate() {
      attributes.clear();
    }

    @Override
    public boolean isNew() {
      throw new UnsupportedOperationException();
    }
  }
}
