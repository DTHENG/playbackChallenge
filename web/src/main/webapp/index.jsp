<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!doctype html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
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
            <div class="small-12 medium-2 large-4 columns hide-for-small">&#160;</div>
            <div class="small-12 medium-8 large-4 columns">
                <h6>&#160;</h6>
                <input type="text" placeholder="first name" id="name"/>
                <input type="text" placeholder="last initial" id="initial"/>
                <a class="button expand" onclick="window.Spela.auth($('#name').val(), $('#initial').val());">play</a>
            </div>
            <div class="small-4 columns">&#160;</div>
        </div>
        <div id="player" style="display: none;" class="row">
            <div class="small-12 medium-2 large-4 columns hide-for-small">&#160;</div>
            <div class="small-12 medium-8 large-4 columns">
                <h6>&#160;</h6>
                <h1 style="text-align:center;font-weight:800"><span id="trackTitle"></span></h1>
                <p style="text-align:center"><span id="trackArtist"></span></p>
                <h6>&#160;</h6>
                <div class="progress small-12">
                    <span id="elapsed" class="meter" style="color:#2980b8;"></span>
                </div>
                <h6>&#160;</h6>
                <div class="row">
                    <div class="small-2 columns" style="padding:0">&#160;</div>
                    <div class="small-2 columns" style="padding:0">
                        <a id="prev" onclick="window.Spela.previous();"><img src="static/previous.svg"/></a>
                    </div>
                    <div class="small-2 columns" style="padding:0">
                        <a id="play" onclick="window.Spela.play();"><img src="static/play.svg"/></a>
                    </div>
                    <div class="small-2 columns" style="padding:0">
                        <a id="pause" onclick="window.Spela.pause();"><img src="static/pause.svg"/></a>
                    </div>
                    <div class="small-2 columns" style="padding:0">
                        <a id="next" onclick="window.Spela.next();"><img src="static/next.svg"/></a>
                    </div>
                    <div class="small-12 medium-2 large-4 columns hide-for-small" style="padding:0">&#160;</div>
                </div>
                <h1>&#160;</h1>
                <p style="text-align:center;">playing on</p>
                <div class="row">
                    <div class="small-3 columns">&#160;</div>
                    <div class="small-6 columns">
                        <a href="#" data-dropdown="devices" class="button dropdown secondary expand tiny"><span id="activeDevice"></span></a><br>
                        <ul id="devices" data-dropdown-content class="f-dropdown"></ul>
                    </div>
                    <div class="small-3 columns">&#160;</div>
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
