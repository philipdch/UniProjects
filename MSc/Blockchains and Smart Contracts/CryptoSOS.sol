// SPDX-License-Identifier: GPL-3.0

pragma solidity ^0.8;

contract CryptoSOS {
    struct Stats {
        uint32 gamesPlayed;
        uint32 wins;
    }

    uint256 constant LOBBY_TIMEOUT = 2 minutes;
    uint256 constant TURN_TIMEOUT = 1 minutes;
    uint256 constant GAME_TOKEN_PRICE = 1 ether;
    uint256 constant GAME_PRIZE = 1.9 ether;

    uint256 start_timer; // start time of current player's turn
    uint256 public game_balance;

    address owner;
    address player1;
    address player2;

    address nextPlayer;

    uint8[9] state;

    mapping(uint8 => bytes1) symbols;
    mapping(address => Stats) statistics;

    event StartGame(address, address);
    event Move(uint8, uint8, address);
    event Win(address);

    modifier isPlayer(){
        require(msg.sender == player1 || msg.sender == player2, "You must participate in a game to call this!");
        _;
    }

    modifier hasGameStarted(){
        require(player1 != address(0) && player2 != address(0), "Player 2 hasn't joined yet.");
        _;
    }

    modifier isNextPlayer() {
        require(msg.sender == nextPlayer, "It's not your turn yet");
        _;
    }

    modifier onlyOwner() {
        require(msg.sender == owner, "Only the owner can call this!");
        _;
    }

    constructor() {
        owner = msg.sender;
        game_balance = 0;
        symbols[0] = "-";
        symbols[1] = "S";
        symbols[2] = "O";
    }

    function play() external payable {
        require(
            player1 == address(0) || player2 == address(0),
            "Another game is already in progress!"
        ); //check if any player slot is empty
        require(msg.sender != player1, "You can't play with yourself!");
        require(
            msg.value >= GAME_TOKEN_PRICE,
            "You didn't send enough tokens to play!"
        );

        game_balance += GAME_TOKEN_PRICE;

        if(msg.value > GAME_TOKEN_PRICE){
            (bool sent, ) = payable(msg.sender).call{value: msg.value - GAME_TOKEN_PRICE}("");
            require(sent, "Failed to return ether surplus");
        }
        //Always assign the address that starts the game to player1.
        //If another player joins, they will be assigned to player2 and the game will begin
        //Contract logic ensures that the above assignments will always be done in this order
        if (player1 == address(0)) {
            player1 = msg.sender;
            nextPlayer = player1;
            start_timer = block.timestamp;
        } else {
            player2 = msg.sender;
        }
        emit StartGame(player1, player2);
    }

    function placeS(uint8 index) external isPlayer hasGameStarted isNextPlayer {
        require(index >= 1 && index <= 9, "Please give a valid tile number (1-9)" );
        require(state[index - 1] == 0, "This tile is taken!");
        address currentPlayer = nextPlayer;

        if (msg.sender == player1) {
            nextPlayer = player2;
        } else {
            nextPlayer = player1;
        }

        state[index - 1] = 1;
        emit Move(index, 1, currentPlayer);
        start_timer = block.timestamp;

        checkWinner(currentPlayer);
    }

    function placeO(uint8 index) external isPlayer hasGameStarted isNextPlayer {
        require(index >= 1 && index <= 9, "Please give a valid tile number (1-9)" );
        require(state[index-1] == 0, "This tile is taken!");
        address currentPlayer = nextPlayer;

        if (msg.sender == player1) {
            nextPlayer = player2;
        } else {
            nextPlayer = player1;
        }

        state[index - 1] = 2;
        emit Move(index, 2, currentPlayer);
        start_timer = block.timestamp;

        checkWinner(currentPlayer);
    }

    function getGameState() public view returns (string memory) {
        bytes memory currentState;
        for (uint8 i = 0; i < state.length; i++) {
            bytes1 correspondingChar = symbols[state[i]];
            currentState = string.concat(currentState, correspondingChar);
        }
        string memory result = string(abi.encodePacked(currentState));
        return result;
    }

    function cancel() external {
        require(msg.sender == player1 && player2 == address(0), "You're not currently waiting in a lobby");
        require(block.timestamp - start_timer >= LOBBY_TIMEOUT, "Give it some more time!");

        (bool sent, ) = payable(msg.sender).call{value: GAME_TOKEN_PRICE}("");
        require(sent, "Failed to send Ether");

        reset();
    }

    function ur2slow() external isPlayer{
        address waitingPlayer;
        if (nextPlayer == player1) {
            waitingPlayer = player2;
        } else {
            waitingPlayer = player1;
        }

        require(msg.sender == waitingPlayer, "It's your turn!");
        require(block.timestamp - start_timer >= TURN_TIMEOUT, "The timer hasn't run out yet!");

        (bool sent, ) = payable(msg.sender).call{value: GAME_PRIZE}("");
        require(sent, "Failed to send Ether");

        statistics[player1].gamesPlayed++;
        statistics[player2].gamesPlayed++;
        statistics[waitingPlayer].wins++;

        emit Win(waitingPlayer);

        reset();
    }

    function collectProfit() public {
        uint256 profit = address(this).balance - game_balance;
        (bool sent, ) = payable(owner).call{value: profit}("");
        require(sent, "Collect profit failed");
    }

    function getPlayerStats(address player)
        public
        view
        returns (uint32, uint32)
    {
        return (statistics[player].wins, statistics[player].gamesPlayed);
    }

    function getGameType() public pure returns (string memory) {
        return "CryptoSOS";
    }

    function kill() public onlyOwner {
        selfdestruct(payable(owner));
    }

    function validate() private view returns (bool) {
        if (
            (state[0] == 1 && state[1] == 2 && state[2] == 1) ||
            (state[3] == 1 && state[4] == 2 && state[5] == 1) ||
            (state[6] == 1 && state[7] == 2 && state[8] == 1)
        ) {
            return true;
        } else if (
            (state[0] == 1 && state[3] == 2 && state[6] == 1) ||
            (state[1] == 1 && state[4] == 2 && state[7] == 1) ||
            (state[2] == 1 && state[5] == 2 && state[8] == 1)
        ) {
            return true;
        } else if (
            (state[0] == 1 && state[4] == 2 && state[8] == 1) ||
            (state[2] == 1 && state[4] == 2 && state[6] == 1)
        ) {
            return true;
        }
        return false;
    }

    function noMoreTiles() private view returns(bool){
        for(uint8 i = 0; i < state.length; i++){
            if(state[i] == 0){
                return false;
            }
        }
        return true;
    }

    function checkWinner(address player) private{
        if(validate()){
            statistics[player1].gamesPlayed++;
            statistics[player2].gamesPlayed++;
            statistics[player].wins++;

            emit Win(player);

            (bool sent,) = payable(player).call{value: GAME_PRIZE}("");
            require(sent, "Failed to send prize");

            reset();
        }else if(noMoreTiles()){
            statistics[player1].gamesPlayed++;
            statistics[player2].gamesPlayed++;

            emit Win(address(0));

            reset();
        }
    }

    function reset() private {
        player1 = address(0);
        player2 = address(0);
        nextPlayer = address(0);
        start_timer = 0;
        delete state; 
    }
}
