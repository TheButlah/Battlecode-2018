const PLAYER_EXCLUDES = [ ".git"
                        , "bin"
                        , "FuzzyWuzzy"
                        , "examplefuncsplayer-python"
                        ];

const redPlayer = red.options[red.selectedIndex].value;
const blueOptions = Array.map(blue.options, (o, idx) => [idx, o.value]).filter(([idx, value]) => PLAYER_EXCLUDES.indexOf(value) === -1);
const replayName = document.querySelector("#replay");

let blueIndex = 0;

function runGame() {
    if (blueIndex < blueOptions.length) {
        blue.selectedIndex = blueOptions[blueIndex++][0];
        replayName.value = `${redPlayer}-vs-${blue.options[blue.selectedIndex].value}`;
        submit.click();
    }
}

// override callback
function wrap_trigger_end_game(_trigger_end_game) {
    return function(winner) {
        _trigger_end_game(winner);
        // run next game
        runGame();
    }
}
trigger_end_game = wrap_trigger_end_game(trigger_end_game);
eel.expose(trigger_end_game, "trigger_end_game");

runGame();

// vim: et tabstop=4 shiftwidth=4
