// SPDX-License-Identifier: GPL-3.0

pragma solidity ^0.8;

contract MultiSOS {

    struct Stats {
        uint32 gamesPlayed;
        uint32 wins;
    }

    struct State{
        address player1;
        address player2;
        address nextPlayer;
        uint256 start_timer; // start time of current player's turn
        uint8[9] state;
    }

    uint256 constant LOBBY_TIMEOUT = 2 minutes;
    uint256 constant TURN_TIMEOUT = 1 minutes;
    uint256 constant GAME_TOKEN_PRICE = 1 ether;
    uint256 constant GAME_PRIZE = 1.9 ether;

    uint32 currentGameId; //only increments when a public game starts (private games manage their ids via skipIndex if necessary)
    uint32 skipIndex; //used to avoid collision between private and public games

    uint256 public game_balance;

    address owner;
    
    mapping(uint8 => bytes1) symbols;
    mapping(address => Stats) statistics;
    mapping(uint32 => State) gameStates;
    mapping(address => uint32[]) lobbyInvitations;

    event StartGame(uint32, address, address);
    event Move(uint32, uint8, uint8, address);
    event Win(uint32, address);

    modifier isPlayer(uint32 gameId){
        require(msg.sender == gameStates[gameId].player1 || msg.sender == gameStates[gameId].player2, "You must participate in the game with the specified ID to call this!");
        _;
    }

    modifier isNextPlayer(uint32 gameId) {
        require(msg.sender == gameStates[gameId].nextPlayer, "It's not your turn yet");
        _;
    }

    modifier hasGameStarted(uint32 gameId){
        require(gameStates[gameId].player1 != address(0) && gameStates[gameId].player2 != address(0), "Player 2 hasn't joined yet.");
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
        State storage currentState = gameStates[currentGameId];
        require(
            currentState.player1 == address(0) || currentState.player2 == address(0),
            "Another game is already in progress!"
        ); //check if any player slot is empty
        require(msg.sender != currentState.player1, "You can't play with yourself!");
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
        if (currentState.player1 == address(0)) {
            currentState.player1 = msg.sender;
            currentState.nextPlayer = currentState.player1;
            currentState.start_timer = block.timestamp;
            emit StartGame(currentGameId, currentState.player1, currentState.player2);
        } else {
            currentState.player2 = msg.sender;
            emit StartGame(currentGameId, currentState.player1, currentState.player2);
            if(skipIndex != 0){
                currentGameId = skipIndex + 1;
                skipIndex = 0;
            }else{
                currentGameId++;
            }
        }
    }

    /* Called any time a player wishes to play against a specific opponent
       The opponent must make a similar call setting the host's address for the private game to begin
       After the first player specifies their opponent's address, they create an invite containing their (the opponent's) address and the game's ID
       When calling this function the opponent can search through their invites to determine if there's a pending invite for the previous host
       If not, they create an invitation themselves so that their game can be found by the specified account
       Otherwise, they accept the invite and the game begins.

       IMPORTANT NOTE:
       While a person is waiting in a public lobby, any number of private lobbies may be launched before the public game starts.
       A maximum of one public games may be pending at any moment (the moment a second person joins, a new public game begins)
       However, the pending public game's ID MUST remain unmodified until it begins (otherwise other players have no means to join it)
       For this reason a skipIndex value is used. This value indicates the ID that will be associated with the next PUBLIC game, after the current pending game begins
       Any private game that starts inbetween these two public games will be given a unique serial id, starting from the next id of the pending public game
       The skipIndex and private game's ID is incremented immediately after a new private game starts
    */
    function play(address rival) external payable {
        require(rival != msg.sender, "You can't play with yourself!");
        uint32[] memory invites = lobbyInvitations[msg.sender]; //check whether this player has pending invites from their opponent
        State storage privateGame;
        bool pendingInvites = false;
        for(uint i = 0; i < invites.length; i++){
            if(gameStates[invites[i]].player1 == rival){
                pendingInvites = true;
                privateGame = gameStates[invites[i]];
                privateGame.player2 = msg.sender;
                emit StartGame(invites[i], privateGame.player1, privateGame.player2);

                //Invite accepted, delete entry
                lobbyInvitations[msg.sender][i] = lobbyInvitations[msg.sender][lobbyInvitations[msg.sender].length-1]; //overwrite element with last in array
                lobbyInvitations[msg.sender].pop(); //reduce array length
                break;
            }
        }
        if(!pendingInvites){ //No pending invites, start a new private game
            uint32 tempId = currentGameId;
            if(gameStates[currentGameId].player1 != address(0)){ //Another player is waiting in a public lobby
                skipIndex = currentGameId + skipIndex + 1; //skip to next available id for this game
                tempId = skipIndex;
            }else{
                currentGameId++;
            }
            privateGame = gameStates[tempId];
            privateGame.player1 = msg.sender;
            lobbyInvitations[rival].push(tempId);
            privateGame.nextPlayer = privateGame.player1;
            privateGame.start_timer = block.timestamp;
            emit StartGame(tempId, privateGame.player1, privateGame.player2);
        }   

        require(
            msg.value >= GAME_TOKEN_PRICE,
            "You didn't send enough tokens to play!"
        );

        game_balance += GAME_TOKEN_PRICE;

        if(msg.value > GAME_TOKEN_PRICE){
            (bool sent, ) = payable(msg.sender).call{value: msg.value - GAME_TOKEN_PRICE}("");
            require(sent, "Failed to return ether surplus");
        }
    }

    function placeS(uint32 gameId, uint8 index) external isPlayer(gameId) hasGameStarted(gameId) isNextPlayer(gameId) {
        State storage game = gameStates[gameId];
        require(index >= 1 && index <= 9, "Please give a valid tile number (1-9)" );
        require(game.state[index - 1] == 0, "This tile is taken!");
        address currentPlayer = game.nextPlayer;

        if (msg.sender == game.player1) {
            game.nextPlayer = game.player2;
        } else {
            game.nextPlayer = game.player1;
        }

        game.state[index - 1] = 1;
        emit Move(gameId, index, 1, currentPlayer);
        game.start_timer = block.timestamp;

        checkWinner(gameId, currentPlayer);
    }

    function placeO(uint32 gameId, uint8 index) external isPlayer(gameId) hasGameStarted(gameId) isNextPlayer(gameId) {
        State storage game = gameStates[gameId];
        require(index >= 1 && index <= 9, "Please give a valid tile number (1-9)" );
        require(game.state[index-1] == 0, "This tile is taken!");
        address currentPlayer = game.nextPlayer;

        if (msg.sender == game.player1) {
            game.nextPlayer = game.player2;
        } else {
            game.nextPlayer = game.player1;
        }

        game.state[index - 1] = 2;
        emit Move(gameId, index, 2, currentPlayer);
        game.start_timer = block.timestamp;

        checkWinner(gameId, currentPlayer);
    }

    function getGameState(uint32 gameId) public view returns (string memory) {
        State memory game = gameStates[gameId];
        bytes memory currentState;
        for (uint8 i = 0; i < game.state.length; i++) {
            bytes1 correspondingChar = symbols[game.state[i]];
            currentState = string.concat(currentState, correspondingChar);
        }
        string memory result = string(abi.encodePacked(currentState));
        return result;
    }

    function cancel(uint32 gameId) external {
        State memory game = gameStates[gameId];
        require(msg.sender == game.player1 && game.player2 == address(0), "You're not currently waiting in a lobby");
        require(block.timestamp - game.start_timer >= LOBBY_TIMEOUT, "Give it some more time!");

        (bool sent, ) = payable(msg.sender).call{value: GAME_TOKEN_PRICE}("");
        require(sent, "Failed to send Ether");

        reset(gameId);
    }

    function ur2slow(uint32 gameId) external isPlayer(gameId){
        State storage game = gameStates[gameId];
        address waitingPlayer;
        if (game.nextPlayer == game.player1) {
            waitingPlayer = game.player2;
        } else {
            waitingPlayer = game.player1;
        }

        require(msg.sender == waitingPlayer, "It's your turn!");
        require(block.timestamp - game.start_timer >= TURN_TIMEOUT, "The timer hasn't run out yet!");

        (bool sent, ) = payable(msg.sender).call{value: GAME_PRIZE}("");
        require(sent, "Failed to send Ether");

        statistics[game.player1].gamesPlayed++;
        statistics[game.player2].gamesPlayed++;
        statistics[waitingPlayer].wins++;

        emit Win(gameId, waitingPlayer);

        reset(gameId);
    }

    function collectProfit() public {
        uint256 profit = address(this).balance - game_balance;
        (bool sent, ) = payable(owner).call{value: profit}("");
        require(sent, "Collect profit failed");
    }

    function getPlayerStats(address player) public view returns (uint32, uint32)
    {
        return (statistics[player].wins, statistics[player].gamesPlayed);
    }

    function getGameType() public pure returns (string memory) {
        return "MultiSOS";
    }

    function kill() public onlyOwner {
        selfdestruct(payable(owner));
    }

    function validate(uint32 gameId) private view returns (bool _hasWon) {
        uint8[9] memory state = gameStates[gameId].state;
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

    function noMoreTiles(uint32 gameId) private view returns(bool _outOfTiles){
        uint8[9] memory state = gameStates[gameId].state;
        for(uint8 i = 0; i < state.length; i++){
            if(state[i] == 0){
                return false;
            }
        }
        return true;
    }

    // function whatsMyGame(address player) public view returns(uint32 _gameId){
    //     for(uint32 i = 0; i < gameStates.length; i++){
    //         if(player == gameStates[i].player1; || player == gameStates[i].player2;;){
    //             return i;
    //         }
    //     }
    // }

    function checkWinner(uint32 gameId, address player) private{
        State memory game = gameStates[gameId];
        if(validate(gameId)){
            statistics[game.player1].gamesPlayed++;
            statistics[game.player2].gamesPlayed++;
            statistics[player].wins++;

            emit Win(gameId, player);

            (bool sent,) = payable(player).call{value: GAME_PRIZE}("");
            require(sent, "Failed to send prize");

            reset(gameId);
        }else if(noMoreTiles(gameId)){
            statistics[game.player1].gamesPlayed++;
            statistics[game.player2].gamesPlayed++;

            emit Win(gameId, address(0));

            reset(gameId);
        }
    }

    function reset(uint32 gameId) private {
        State storage game = gameStates[gameId];
        game.player1 = address(0);
        game.player2 = address(0);
        game.nextPlayer = address(0);
        game.start_timer = 0;
        delete game.state; 
    }
}