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
            </div>
            <div class="small-4 columns">&#160;</div>
        </div>
        <script src="/static/js/vendor/jquery.js"></script>
        <script src="/static/js/vendor/foundation.min.js"></script>
        <script src="/static/js/vendor/fastclick.js"></script>
        <script src="/static/js/vendor/placeholder.js"></script>
        <script src="/static/js/vendor/jquery.autocomplete.js"></script>
        <script src="/static/js/vendor/jquery.cookie.js"></script>
        <script type="text/javascript">
            (function ($, window, document) {

                window.Spela = {
                    auth: function (firstName, lastInitial) {
                        $.post("/api",
                            {
                                auth: true,
                                first_name:firstName,
                                last_initial:lastInitial,
                                device_id: "MacBook Pro"
                            },
                            function (resp, status) {
                                sessionStorage.setItem("user", firstName + lastInitial);
                                window.location.href = "/";
                            }
                        );
                    }
                };

                $(document).ready(function () {
                    if (sessionStorage.getItem("user")) {
                        $("#player").css("display","block");
                        $.get("/api",
                            {
                                user: sessionStorage.getItem("user")
                            },
                            function (resp, status) {
                                if (status !== "success") return;
                                $("#trackTitle").html(resp.current.title);
                            }
                        );
                        function refresh() {
                            $.get("/api",
                                {
                                    user: sessionStorage.getItem("user")
                                },
                                function (resp, status) {
                                    if (status !== "success") return;
                                    $("#trackTitle").html(resp.current.title);
                                    switch (resp.state) {
                                        case "PLAY":
                                            var started = resp.current.started;
                                            $("#elapsed").html(Math.round((new Date().getTime() - started) / 1000));
                                            $("#length").html(resp.current.length / 60);
                                            break;
                                        case "PAUSE":
                                            $("#elapsed").html(resp.position);
                                            $("#length").html(resp.current.length / 60);


                                    }
                                }
                            );
                        }
                        refresh();
                        window.setInterval(refresh, 1000);
                        return;
                    }
                    $("#auth").css("display","block");
                    $("#name").focus();
                    $('#initial').keydown( function(e) {
                        var key = e.charCode ? e.charCode : e.keyCode ? e.keyCode : 0;
                        if(key != 13) return;
                        e.preventDefault();
                        window.Spela.auth($('#name').val(), $('#initial').val());
                    });
                });
            })(window.jQuery, window, document);
        </script>
    </body>
</html>
