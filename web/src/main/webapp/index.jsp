<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!doctype html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>spela</title>
        <link href="/static/css/vendor/foundation.min.css" rel="stylesheet" media="screen">
        <link href="/static/css/vendor/normalize.css" rel="stylesheet" media="screen">
        <script src="/static/js/vendor/modernizr.js"></script>
        <script src="/static/js/vendor/custom.modernizr.js"></script>
    </head>
    <body>
        <div class="row">
            <div class="small-12 large-12 columns">
                <a href="/" class="navbar-brand"><h1 style="text-align:center;">spela</h1></a>
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
            <div class="small-4 columns">&#160;</div>
            <div class="small-4 columns">
                <p><span id="trackTitle"></span></p>
                <p><span id="elapsed"></span> of <span id="length"></span></p>
                <a class="button expand" onclick="window.Spela.play();">play</a>
                <a class="button expand" onclick="window.Spela.pause();">pause</a>
                <a class="button expand" onclick="window.Spela.next();">next</a>
                <a class="button expand" onclick="window.Spela.previous();">previous</a>

            </div>
            <div class="small-4 columns">&#160;</div>
        </div>
        <script src="/static/js/vendor/jquery.js"></script>
        <script src="/static/js/vendor/foundation.min.js"></script>
        <script src="/static/js/vendor/fastclick.js"></script>
        <script src="/static/js/vendor/placeholder.js"></script>
        <script src="/static/js/vendor/jquery.autocomplete.js"></script>
        <script src="/static/js/vendor/jquery.cookie.js"></script>
        <script src="/static/js/spela.js"></script>
    </body>
</html>
