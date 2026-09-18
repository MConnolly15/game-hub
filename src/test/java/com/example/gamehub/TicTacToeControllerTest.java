package com.example.gamehub.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
// NOTE: adjust this import to match wherever your User entity actually lives
// (e.g. com.example.gamehub.entity.User) if it differs in your project.
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.ModelAndView;

/**
 * Unit tests for {@link TicTacToeController}.
 *
 * <p>These tests exercise the controller in isolation (no Spring context is loaded). The {@link
 * HttpSession} is a Mockito mock backed by a real {@link HashMap} so that
 * getAttribute/setAttribute/removeAttribute behave like a real session, which lets us both observe
 * what the controller stores and pre-seed board/turn state for deterministic assertions (e.g.
 * forcing an almost-won or almost-full board).
 */
@ExtendWith(MockitoExtension.class)
class TicTacToeControllerTest {

  private static final String BOARD_KEY = "ticTacToeBoard";
  private static final String TURN_KEY = "ticTacToeTurn";
  private static final String EMPTY = "";

  @Mock private UserRepository userRepository;
  @Mock private HttpSession session;
  @Mock private Authentication authentication;

  private TicTacToeController controller;
  private final Map<String, Object> sessionAttributes = new HashMap<>();

  @BeforeEach
  void setUp() {
    sessionAttributes.clear();
    controller = new TicTacToeController(userRepository);

    lenient()
        .when(session.getAttribute(anyString()))
        .thenAnswer(inv -> sessionAttributes.get((String) inv.getArgument(0)));
    lenient()
        .doAnswer(
            inv -> {
              sessionAttributes.put(inv.getArgument(0), inv.getArgument(1));
              return null;
            })
        .when(session)
        .setAttribute(anyString(), any());
    lenient()
        .doAnswer(
            inv -> {
              sessionAttributes.remove((String) inv.getArgument(0));
              return null;
            })
        .when(session)
        .removeAttribute(anyString());
  }

  private List<String> emptyBoard() {
    return new ArrayList<>(Arrays.asList("", "", "", "", "", "", "", "", ""));
  }

  private void seedBoard(List<String> board) {
    sessionAttributes.put(BOARD_KEY, new ArrayList<>(board));
  }

  private void seedTurn(String turn) {
    sessionAttributes.put(TURN_KEY, turn);
  }

  @SuppressWarnings("unchecked")
  private List<String> storedBoard() {
    return (List<String>) sessionAttributes.get(BOARD_KEY);
  }

  private void mockAuthenticatedUser(String email, String username) {
    org.mockito.Mockito.when(authentication.isAuthenticated()).thenReturn(true);
    org.mockito.Mockito.when(authentication.getName()).thenReturn(email);
    User user = new User();
    user.setUsername(username);
    org.mockito.Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
  }

  // ---------------------------------------------------------------------
  // GET /game/tictactoe
  // ---------------------------------------------------------------------

  @Nested
  @DisplayName("showBoard")
  class ShowBoard {

    @Test
    @DisplayName("creates a fresh empty board and sets X to move when session is empty")
    void createsFreshBoardAndTurn() {
      ModelAndView mv = controller.showBoard(session, null);

      assertThat(mv.getViewName()).isEqualTo("tictactoe");
      @SuppressWarnings("unchecked")
      List<String> board = (List<String>) mv.getModel().get("board");
      assertThat(board).containsExactly("", "", "", "", "", "", "", "", "");
      assertThat(mv.getModel().get("currentPlayer")).isEqualTo("X");
      assertThat(mv.getModel().get("winner")).isNull();
      assertThat(mv.getModel().get("isDraw")).isEqualTo(false);
      assertThat(mv.getModel().get("gameName")).isEqualTo("tictactoe");
      // side effect: the freshly created board/turn are persisted to the session
      assertThat(storedBoard()).containsExactly("", "", "", "", "", "", "", "", "");
      assertThat(sessionAttributes.get(TURN_KEY)).isEqualTo("X");
    }

    @Test
    @DisplayName("reuses the existing board and turn already stored in the session")
    void reusesExistingBoardAndTurn() {
      List<String> board = emptyBoard();
      board.set(0, "X");
      seedBoard(board);
      seedTurn("O");

      ModelAndView mv = controller.showBoard(session, null);

      @SuppressWarnings("unchecked")
      List<String> returned = (List<String>) mv.getModel().get("board");
      assertThat(returned.get(0)).isEqualTo("X");
      assertThat(mv.getModel().get("currentPlayer")).isEqualTo("O");
    }

    @Test
    @DisplayName("reports the winner when the board already has a winning line")
    void reportsWinnerWhenPresent() {
      List<String> board = emptyBoard();
      board.set(0, "X");
      board.set(1, "X");
      board.set(2, "X");
      seedBoard(board);

      ModelAndView mv = controller.showBoard(session, null);

      assertThat(mv.getModel().get("winner")).isEqualTo("X");
      assertThat(mv.getModel().get("isDraw")).isEqualTo(false);
    }

    @Test
    @DisplayName("reports a draw when the board is full with no winner")
    void reportsDrawWhenBoardFullNoWinner() {
      // X O X
      // X O O
      // O X X
      seedBoard(Arrays.asList("X", "O", "X", "X", "O", "O", "O", "X", "X"));

      ModelAndView mv = controller.showBoard(session, null);

      assertThat(mv.getModel().get("winner")).isNull();
      assertThat(mv.getModel().get("isDraw")).isEqualTo(true);
    }

    @Test
    @DisplayName("does not report a draw when the board still has empty cells")
    void notDrawWhenBoardNotFull() {
      List<String> board = emptyBoard();
      board.set(0, "X");
      seedBoard(board);

      ModelAndView mv = controller.showBoard(session, null);

      assertThat(mv.getModel().get("isDraw")).isEqualTo(false);
    }

    @Test
    @DisplayName("adds the username to the model when authenticated")
    void addsUsernameWhenAuthenticated() {
      mockAuthenticatedUser("player@example.com", "player1");

      ModelAndView mv = controller.showBoard(session, authentication);

      assertThat(mv.getModel().get("username")).isEqualTo("player1");
    }

    @Test
    @DisplayName("does not add a username when authentication is null")
    void noUsernameWhenAuthenticationNull() {
      ModelAndView mv = controller.showBoard(session, null);

      assertNull(mv.getModel().get("username"));
    }

    @Test
    @DisplayName("does not add a username when not authenticated")
    void noUsernameWhenNotAuthenticated() {
      org.mockito.Mockito.when(authentication.isAuthenticated()).thenReturn(false);

      ModelAndView mv = controller.showBoard(session, authentication);

      assertNull(mv.getModel().get("username"));
    }

    static Stream<Arguments> winningLines() {
      return Stream.of(
          Arguments.of("top row", new int[] {0, 1, 2}),
          Arguments.of("middle row", new int[] {3, 4, 5}),
          Arguments.of("bottom row", new int[] {6, 7, 8}),
          Arguments.of("left column", new int[] {0, 3, 6}),
          Arguments.of("middle column", new int[] {1, 4, 7}),
          Arguments.of("right column", new int[] {2, 5, 8}),
          Arguments.of("main diagonal", new int[] {0, 4, 8}),
          Arguments.of("anti diagonal", new int[] {2, 4, 6}));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("winningLines")
    @DisplayName("detects a winner on every possible winning line")
    void detectsWinnerOnEveryLine(String description, int[] line) {
      List<String> board = emptyBoard();
      for (int index : line) {
        board.set(index, "O");
      }
      seedBoard(board);

      ModelAndView mv = controller.showBoard(session, null);

      assertThat(mv.getModel().get("winner")).isEqualTo("O");
    }
  }

  // ---------------------------------------------------------------------
  // POST /game/tictactoe/move
  // ---------------------------------------------------------------------

  @Nested
  @DisplayName("makeMove")
  class MakeMove {

    @Test
    @DisplayName("places the current player's mark in an empty cell")
    void placesMarkInEmptyCell() {
      seedBoard(emptyBoard());
      seedTurn("X");

      ModelAndView mv = controller.makeMove(4, session, null);

      assertThat(storedBoard().get(4)).isEqualTo("X");
      @SuppressWarnings("unchecked")
      List<String> returned = (List<String>) mv.getModel().get("board");
      assertThat(returned.get(4)).isEqualTo("X");
    }

    @Test
    @DisplayName("toggles the turn from X to O after a move")
    void togglesTurnFromXToO() {
      seedBoard(emptyBoard());
      seedTurn("X");

      ModelAndView mv = controller.makeMove(0, session, null);

      assertThat(sessionAttributes.get(TURN_KEY)).isEqualTo("O");
      assertThat(mv.getModel().get("currentPlayer")).isEqualTo("O");
    }

    @Test
    @DisplayName("toggles the turn from O back to X after a move")
    void togglesTurnFromOToX() {
      seedBoard(emptyBoard());
      seedTurn("O");

      controller.makeMove(0, session, null);

      assertThat(sessionAttributes.get(TURN_KEY)).isEqualTo("X");
    }

    @Test
    @DisplayName("alternates turns correctly across successive moves")
    void alternatesAcrossSuccessiveMoves() {
      seedBoard(emptyBoard());
      seedTurn("X");

      controller.makeMove(0, session, null); // X -> cell 0, turn becomes O
      controller.makeMove(1, session, null); // O -> cell 1, turn becomes X
      controller.makeMove(2, session, null); // X -> cell 2, turn becomes O

      assertThat(storedBoard()).containsExactly("X", "O", "X", "", "", "", "", "", "");
      assertThat(sessionAttributes.get(TURN_KEY)).isEqualTo("O");
    }

    @Test
    @DisplayName("ignores a move on a cell that is already occupied")
    void ignoresMoveOnOccupiedCell() {
      List<String> board = emptyBoard();
      board.set(0, "X");
      seedBoard(board);
      seedTurn("O");

      ModelAndView mv = controller.makeMove(0, session, null);

      assertThat(storedBoard().get(0)).isEqualTo("X"); // unchanged
      assertThat(mv.getModel().get("currentPlayer")).isEqualTo("O"); // turn did not advance
    }

    @Test
    @DisplayName("ignores a move once the game already has a winner")
    void ignoresMoveWhenGameAlreadyWon() {
      List<String> board = emptyBoard();
      board.set(0, "X");
      board.set(1, "X");
      board.set(2, "X"); // X has already won
      seedBoard(board);
      seedTurn("O");

      ModelAndView mv = controller.makeMove(4, session, null);

      assertThat(storedBoard().get(4)).isEqualTo(""); // move was not applied
      assertThat(mv.getModel().get("currentPlayer")).isEqualTo("O"); // turn unchanged
      assertThat(mv.getModel().get("winner")).isEqualTo("X");
    }

    @Test
    @DisplayName("reports the winner in the model immediately after the winning move")
    void reportsWinnerAfterWinningMove() {
      // X X _   -> X plays cell 2 to complete the top row
      // O O _
      // _ _ _
      List<String> board = emptyBoard();
      board.set(0, "X");
      board.set(1, "X");
      board.set(3, "O");
      board.set(4, "O");
      seedBoard(board);
      seedTurn("X");

      ModelAndView mv = controller.makeMove(2, session, null);

      assertThat(mv.getModel().get("winner")).isEqualTo("X");
      assertThat(mv.getModel().get("isDraw")).isEqualTo(false);
    }

    @Test
    @DisplayName("reports a draw in the model immediately after the final, non-winning move")
    void reportsDrawAfterFinalMove() {
      // X O X
      // X O O
      // O X _   -> X plays the last cell (8), no winner results
      List<String> board =
          new ArrayList<>(Arrays.asList("X", "O", "X", "X", "O", "O", "O", "X", ""));
      seedBoard(board);
      seedTurn("X");

      ModelAndView mv = controller.makeMove(8, session, null);

      assertThat(mv.getModel().get("winner")).isNull();
      assertThat(mv.getModel().get("isDraw")).isEqualTo(true);
    }

    @Test
    @DisplayName("creates board and turn on the fly if a move is made with no prior session state")
    void createsStateOnTheFlyWhenMissing() {
      ModelAndView mv = controller.makeMove(0, session, null);

      assertThat(storedBoard().get(0)).isEqualTo("X"); // default first turn is X
      assertThat(sessionAttributes.get(TURN_KEY)).isEqualTo("O");
      assertThat(mv.getModel().get("currentPlayer")).isEqualTo("O");
    }

    @Test
    @DisplayName("keeps the username in the model on a move")
    void keepsUsernameOnMove() {
      seedBoard(emptyBoard());
      seedTurn("X");
      mockAuthenticatedUser("player@example.com", "player1");

      ModelAndView mv = controller.makeMove(0, session, authentication);

      assertThat(mv.getModel().get("username")).isEqualTo("player1");
    }
  }

  // ---------------------------------------------------------------------
  // POST /game/tictactoe/reset
  // ---------------------------------------------------------------------

  @Nested
  @DisplayName("resetBoard")
  class ResetBoard {

    @Test
    @DisplayName("clears the prior board and turn and rebuilds a fresh empty state")
    void clearsSessionAndRebuildsFreshState() {
      List<String> board = emptyBoard();
      board.set(0, "X");
      board.set(1, "O");
      seedBoard(board);
      seedTurn("X");

      ModelAndView mv = controller.resetBoard(session, null);

      assertThat(storedBoard()).containsExactly("", "", "", "", "", "", "", "", "");
      assertThat(sessionAttributes.get(TURN_KEY)).isEqualTo("X");
      @SuppressWarnings("unchecked")
      List<String> returned = (List<String>) mv.getModel().get("board");
      assertThat(returned).containsExactly("", "", "", "", "", "", "", "", "");
      assertThat(mv.getModel().get("currentPlayer")).isEqualTo("X");
      assertThat(mv.getModel().get("winner")).isNull();
      assertThat(mv.getModel().get("isDraw")).isEqualTo(false);
    }

    @Test
    @DisplayName("adds the username to the model when authenticated")
    void addsUsernameWhenAuthenticated() {
      mockAuthenticatedUser("player@example.com", "player1");

      ModelAndView mv = controller.resetBoard(session, authentication);

      assertThat(mv.getModel().get("username")).isEqualTo("player1");
    }
  }
}
