(function ($, window, document) {

    "use strict";

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
        },
        play: function () {
            $.post("/api",
                {
                    update: true,
                    user: sessionStorage.getItem("user"),
                    state: "PLAY",
                    next: false,
                    previous: false
                }
            );
        },
        pause: function () {
            $.post("/api",
                {
                    update: true,
                    user: sessionStorage.getItem("user"),
                    state: "PAUSE",
                    next: false,
                    previous: false
                }
            );
        },
        next: function () {
            $.post("/api",
                {
                    update: true,
                    user: sessionStorage.getItem("user"),
                    state: "PLAY",
                    next: true,
                    previous: false
                }
            );
        },
        previous: function () {
            $.post("/api",
                {
                    update: true,
                    user: sessionStorage.getItem("user"),
                    state: "PLAY",
                    next: false,
                    previous: true
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
                    if ( ! resp) {
                        sessionStorage.removeItem("user");
                        window.location.href = "/";
                        return;
                    }
                }
            );
            var updateView = function() {
                $.get("/api",
                    {
                        user: sessionStorage.getItem("user")
                    },
                    function (resp, status) {
                        if (status !== "success") return;
                        if ( ! resp) {
                            sessionStorage.removeItem("user");
                            window.location.href = "/";
                            return;
                        }
                        $("#trackTitle").html(resp.current.title);
                        $("#trackArtist").html(resp.current.artist);
                        var deviceOptions = "";
                        var devices = $("#devices");
                        for (var i = 0; i < resp.devices.length; i++) {
                            if (resp.devices[i].is_playing) {
                                $("#activeDevice").html(resp.devices[i].name);
                            }
                            deviceOptions += "<li><a href=\"#\">"+ resp.devices[i].name +"</a></li>";
                        }
                        devices.html(deviceOptions);
                        switch (resp.state) {
                            case "PLAY":
                                var started = resp.current.started;
                                var elapsed = Math.round((new Date().getTime() - started) / 1000);
                                var total = resp.current.length;
                                $("#elapsed").css("width", (elapsed / total * 100).toFixed(0) +"%");
                                //$("#length").html(resp.current.length / 60);
                                break;
                            case "PAUSE":
                                //var elapsed =
                                $("#elapsed").css("width", (resp.position / resp.current.length * 100).toFixed(0) +"px");

                            //$("#length").html(resp.current.length / 60);


                        }
                    }
                );
            }
            updateView();
            window.setInterval(updateView, 1000);
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