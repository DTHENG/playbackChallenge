<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!doctype html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Spela</title>
        <link href="/static/css/vendor/foundation.min.css" rel="stylesheet" media="screen">
        <link href="/static/css/vendor/normalize.css" rel="stylesheet" media="screen">
        <script src="/static/js/vendor/modernizr.js"></script>
        <script src="/static/js/vendor/custom.modernizr.js"></script>
        <link href="/static/icon.png" rel="icon" type="image/ico">
    </head>
    <body>
        <div class="row">
            <div class="small-12 large-12 columns">
                <a href="/" class="navbar-brand"><h1 style="text-align:center;">Spela</h1></a>
            </div>
        </div>
        <div id="auth" style="display:none;" class="row">
            <div class="small-4 columns">&#160;</div>
            <div class="small-4 columns">
                <input type="text" placeholder="first name" id="name"/>
                <input type="text" placeholder="last initial" id="initial"/>
                <a class="button expand" onclick="window.Spela.auth($('#name').val(), $('#initial').val());">play</a>
            </div>
            <div class="small-4 columns">&#160;</div>
        </div>
        <div id="player" style="display: none;" class="row">
            <div class="small-12 medium-2 large-4 columns hide-for-small">&#160;</div>
            <div class="small-12 medium-8 large-4 columns">
                <h1 style="text-align:center;font-weight:800"><span id="trackTitle"></span></h1>
                <p style="text-align:center"><span id="trackArtist"></span></p>
                <h6>&#160;</h6>
                <div class="progress small-12">
                    <span id="elapsed" class="meter"></span>
                </div>
                <h6>&#160;</h6>
                <div class="row">
                    <div class="small-3 columns">
                        <a id="prev" class="button expand" onclick="window.Spela.previous();">prev</a>
                    </div>
                    <div class="small-3 columns">
                        <a id="play" class="button expand" onclick="window.Spela.play();">play</a>
                    </div>
                    <div class="small-3 columns">
                        <a id="pause" class="button expand" onclick="window.Spela.pause();">pause</a>
                    </div>
                    <div class="small-3 columns">
                        <a id="next" class="button expand" onclick="window.Spela.next();">next</a>
                    </div>
                </div>
                <h6>&#160;</h6>
                <p style="text-align:center;">playing on</p>
                <div class="row">
                    <div class="small-2 columns">&#160;</div>
                    <div class="small-8 columns">
                        <a href="#" data-dropdown="devices" class="button dropdown secondary expand"><span id="activeDevice"></span></a><br>
                        <ul id="devices" data-dropdown-content class="f-dropdown"></ul>
                    </div>
                    <div class="small-2 columns">&#160;</div>
                </div>
            </div>
            <div class="small-12 medium-2 large-4 columns hide-for-small">&#160;</div>
        </div>
        <script src="/static/js/vendor/jquery.js"></script>
        <script src="/static/js/vendor/foundation.min.js"></script>
        <script src="/static/js/vendor/fastclick.js"></script>
        <script src="/static/js/vendor/placeholder.js"></script>
        <script src="/static/js/vendor/jquery.autocomplete.js"></script>
        <script src="/static/js/vendor/jquery.cookie.js"></script>
        <script src="/static/js/spela.js"></script>
        <script>
            $(document).foundation();
        </script>
    </body>
</html>
