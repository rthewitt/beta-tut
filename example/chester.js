var chester = require('chess-bot');

chester.configure(); // Not attaching to anything at this point

var randomStrategy = function(state) {
    if (!this.game_over()) {
          console.log('position: ' + this.fen());
          var moves = this.moves();
          var move = moves[Math.floor(Math.random() * moves.length)];
          this.move(move);
    
          console.log('move: ' + move);
    }
}

chester.strategy = randomStrategy;

chester.connect('ryan'); 
