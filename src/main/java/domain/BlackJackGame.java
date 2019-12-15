package domain;

import domain.function.CardDistributor;
import domain.function.OutcomeRateCalculator;
import domain.user.BlackjackUserResult;
import domain.user.Dealer;
import domain.user.Player;
import domain.user.PlayerInputData;

import java.util.List;
import java.util.Scanner;

public class BlackJackGame {
    private static final int FIRST_DISTRIBUTED_CARD_AMOUNT = 2;
    private static final int ADDABLE_CARD_AMOUNT = 1;
    private static final int MAX_SCORE_NEEDS_MORE_CARD = 16;
    private static List<Player> playerList;
    private static List<String> playerNameList;
    private static Dealer dealer;
    private static CardDistributor cardDistributor;

    public static void main(String[] args) {
        Dealer dealer = new Dealer();
        List<Player> playerList = PlayerInputData.getPlayerList();
        CardDistributor cardDistributor = new CardDistributor();
        distributeCardsToGameParticipant(dealer, playerList, cardDistributor);
        addCardToParticipant(dealer, playerList, cardDistributor);
        printGameParticipantTotalScore(dealer, playerList);
        printGameParticipantProfit(dealer, playerList);
    }

    private static void distributeCardsToGameParticipant() {
        cardDistributor = new CardDistributor();
        cardDistributor.giveCardToDealer(FIRST_DISTRIBUTED_CARD_AMOUNT, dealer);
        for (Player player : playerList) {
            cardDistributor.giveCardToPlayer(FIRST_DISTRIBUTED_CARD_AMOUNT, player);
        }
    }

    private static void printDistributedCards() {
        String playerName = String.join(",", playerNameList);
        System.out.println(String.format("딜러와 %s에게 각각 %d장의 카드를 나누어 주었습니다.", playerName, FIRST_DISTRIBUTED_CARD_AMOUNT));
        dealer.printFirstCardOnly();
        for (Player player : playerList) {
            player.printAllCards();
            System.out.println();
        }
        System.out.println();
    }

    private static void addCardToPlayer() {
        for (int i = 0; i < playerList.size(); i++) {
            askCardAddition(playerNameList.get(i), playerList.get(i));
        }
    }

    private static void askCardAddition(String playerName, Player player) {
        boolean needToAsk = true;
        while (needToAsk) {
            System.out.println(String.format("%s, 한 장의 카드를 더 받겠습니까?(예는 y, 아니오는 n)", playerName));
            Scanner scanner = new Scanner(System.in);
            String answer = scanner.nextLine();
            needToAsk = needMoreCard(answer, playerName, player);
            System.out.println();
        }
    }

    private static boolean needMoreCard(String answer, String playerName, Player player) {
        if (answer.equals("y")) {
            cardDistributor.giveCardToPlayer(ADDABLE_CARD_AMOUNT, player);
            CardPrinter.printPlayerCards(player, playerName);
            return true;
        } else if (answer.equals("n")) {
            CardPrinter.printPlayerCards(player, playerName);
            return false;
        }
        System.out.println("※ y와 n만 입력할 수 있습니다.");
        return true;
    }

    private static void addCardToDealer() {
        if (dealer.calculateTotalCardScore() <= MAX_SCORE_NEEDS_MORE_CARD) {
            cardDistributor.giveCardToDealer(ADDABLE_CARD_AMOUNT, dealer);
            System.out.println(String.format("딜러는 %d 이하라 한 장의 카드를 더 받았습니다. \n", MAX_SCORE_NEEDS_MORE_CARD));
            return;
        }
        System.out.println(String.format("딜러는 %d 초과로 카드를 더 받지 않습니다. \n", MAX_SCORE_NEEDS_MORE_CARD));
    }

    private static void printGameParticipantTotalScore() {
        dealer.printTotalScore();
        for (Player player : playerList) {
            player.printTotalScore();
        }
        System.out.println();
    }

    private static void printGameParticipantProfit(Dealer dealer, List<Player> playerList) {
        System.out.println("## 최종수익 ##");
        List<Integer> playerProfitList = getPlayerProfitList(dealer, playerList);
        int dealerProfit = getDealerProfitSum(playerProfitList);
        System.out.println("딜러: " + dealerProfit);
        for (int i = 0; i < playerList.size(); i++) {
            System.out.println(playerList.get(i).getName() + ": " + playerProfitList.get(i));
        }
    }

    private static List<Integer> getPlayerProfitList(Dealer dealer, List<Player> playerList) {
        List<Integer> playerProfitList = new ArrayList<>();
        for (Player player : playerList) {
            OutcomeRateCalculator calculator = new OutcomeRateCalculator(dealer.createBlackjackUserResult(), player.createBlackjackUserResult());
            double profitRate = calculator.getPlayerOutcomeRate();
            int profit = player.getProfit(profitRate);
            playerProfitList.add(profit);
        }
        return playerProfitList;
    }

    private static int getDealerProfitSum(List<Integer> playerProfitList) {
        int dealerProfitSum = 0;
        for (int playerProfit : playerProfitList) {
            dealerProfitSum += playerProfit;
        }
        return dealerProfitSum * -1;
    }

}
