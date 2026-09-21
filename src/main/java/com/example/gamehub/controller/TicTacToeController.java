package com.example.gamehub.controller;

import com.example.gamehub.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TicTacToeController {
  private final UserRepository userRepository;

  public TicTacToeController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  private static final String BOARD_KEY = "ticTacToeBoard";
  private static final String TURN_KEY = "ticTacToeTurn";

  @GetMapping("/game/tictactoe")
  public ModelAndView showBoard(HttpSession session, Authentication authentication) {
    return buildBoardView(session, authentication);
  }

  @PostMapping("/game/tictactoe/move")
  public ModelAndView makeMove(
      @RequestParam int cellIndex, HttpSession session, Authentication authentication) {
    List<String> board = getOrCreateBoard(session);
    String currentPlayer = getOrCreateTurn(session);

    if (board.get(cellIndex).isEmpty() && checkWinner(board) == null) {
      board.set(cellIndex, currentPlayer);
      session.setAttribute(BOARD_KEY, board);

      String nextPlayer = currentPlayer.equals("X") ? "O" : "X";
      session.setAttribute(TURN_KEY, nextPlayer);
    }

    return buildBoardView(session, authentication);
  }

  @PostMapping("/game/tictactoe/reset")
  public ModelAndView resetBoard(HttpSession session, Authentication authentication) {
    session.removeAttribute(BOARD_KEY);
    session.removeAttribute(TURN_KEY);
    return buildBoardView(session, authentication);
  }

  private ModelAndView buildBoardView(HttpSession session, Authentication authentication) {
    List<String> board = getOrCreateBoard(session);
    String winner = checkWinner(board);
    boolean isDraw = winner == null && isBoardFull(board);

    ModelAndView modelAndView = new ModelAndView("tictactoe");
    modelAndView.addObject("gameName", "tictactoe");
    modelAndView.addObject("board", board);
    modelAndView.addObject("currentPlayer", getOrCreateTurn(session));
    modelAndView.addObject("winner", winner);
    modelAndView.addObject("isDraw", isDraw);
    addUsernameToModel(modelAndView, authentication);
    return modelAndView;
  }

  private void addUsernameToModel(ModelAndView modelAndView, Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
      String email = authentication.getName();
      userRepository
          .findByEmail(email)
          .ifPresent(user -> modelAndView.addObject("username", user.getUsername()));
    }
  }

  private List<String> getOrCreateBoard(HttpSession session) {
    @SuppressWarnings("unchecked")
    List<String> board = (List<String>) session.getAttribute(BOARD_KEY);
    if (board == null) {
      board = new ArrayList<>(Arrays.asList("", "", "", "", "", "", "", "", ""));
      session.setAttribute(BOARD_KEY, board);
    }
    return board;
  }

  private String getOrCreateTurn(HttpSession session) {
    String turn = (String) session.getAttribute(TURN_KEY);
    if (turn == null) {
      turn = "X";
      session.setAttribute(TURN_KEY, turn);
    }
    return turn;
  }

  private String checkWinner(List<String> board) {
    int[][] winningLines = {
      {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // rows
      {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // columns
      {0, 4, 8}, {2, 4, 6} // diagonals
    };

    for (int[] line : winningLines) {
      String first = board.get(line[0]);
      if (!first.isEmpty()
          && first.equals(board.get(line[1]))
          && first.equals(board.get(line[2]))) {
        return first;
      }
    }
    return null;
  }

  private boolean isBoardFull(List<String> board) {
    return board.stream().noneMatch(String::isEmpty);
  }
}
