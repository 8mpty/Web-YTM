(function() {
    'use strict';
//    setInterval(() => {
//        const title = document.getElementsByClassName('title style-scope ytmusic-player-bar')[0];
//        const act = title.innerText;
//        console.log(act);
//        JS.showTitle(act);
//        console.clear();
//    }, 1000);

    function updateTitle(){
        const title = document.getElementsByClassName('title style-scope ytmusic-player-bar')[0];
        const act = title.innerText;
        JS.showTitle(act);
        setTimeout(updateTitle, 1000);
    }
    updateTitle();
})();