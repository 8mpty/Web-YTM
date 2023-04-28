(function() {
    'use strict';

    const oriPlayBtn = document.querySelector("div.left-controls-buttons tp-yt-paper-icon-button#play-pause-button");
    console.log(oriPlayBtn.innerHTML);

    setTimeout(() => {
        const oriPlayBtn = document.querySelector("div.left-controls-buttons tp-yt-paper-icon-button#play-pause-button");
        const newBtn = document.querySelector("div.left-controls-buttons tp-yt-paper-icon-button.next-button.style-scope.ytmusic-player-bar");

        const newElement = document.createElement(newBtn.tagnname);

        newBtn.innerHTML = oriPlayBtn.innerHTML;
        newBtn.onclick = oriPlayBtn.onclick;

        //     newElement.classList.add('FUCKYOUNI');
        //     newElement.id = 'FUCKYOUNI';

        newElement.classList = newBtn.classList;
        newBtn.replaceWith(newElement);

        console.log("DONE");
    }, 1000);


      const msg = "test"
      JS.showDebug(msg);
})();