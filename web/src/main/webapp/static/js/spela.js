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
                    state: "PLAY"
                }
            );
        },
        pause: function () {
            $.post("/api",
                {
                    update: true,
                    user: sessionStorage.getItem("user"),
                    state: "PAUSE"
                }
            );
        },
        next: function () {
            $.post("/api",
                {
                    update: true,
                    user: sessionStorage.getItem("user"),
                    next: true,
                    state: "PLAY"
                }
            );
        },
        previous: function () {
            $.post("/api",
                {
                    update: true,
                    user: sessionStorage.getItem("user"),
                    previous: true,
                    state: "PLAY"
                }
            );
        },
        device: function (id) {
            $.post("/api",
                {
                    update: true,
                    user: sessionStorage.getItem("user"),
                    device_id: id
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
                            deviceOptions += "<li><a onclick=\"window.Spela.device('"+ resp.devices[i].name +"');\">"+ resp.devices[i].name +"</a></li>";
                        }
                        devices.html(deviceOptions);
                        if ( ! resp.next) {
                            if ($("#next").children("img").attr("src") !== "static/next_disabled.svg") {
                                $("#next").children("img").attr("src", "static/next_disabled.svg");
                                $("#next").css("cursor", "default");
                            }
                        } else {
                            if ($("#next").children("img").attr("src") !== "static/next.svg") {
                                $("#next").children("img").attr("src", "static/next.svg");
                                $("#next").css("cursor", "pointer");
                            }
                        }
                        if ( ! resp.previous) {
                            if ($("#prev").children("img").attr("src") !== "static/previous_disabled.svg") {
                                $("#prev").children("img").attr("src", "static/previous_disabled.svg");
                                $("#prev").css("cursor", "default");
                            }
                        } else {
                            if ($("#prev").children("img").attr("src") !== "static/previous.svg") {
                                $("#prev").children("img").attr("src", "static/previous.svg");
                                $("#prev").css("cursor", "pointer");
                            }
                        }
                        switch (resp.state) {
                            case "PLAY":
                                var started = resp.current.started;
                                var elapsed = Math.round((new Date().getTime() - started) / 1000);
                                var total = resp.current.length;
                                $("#elapsed").css("width", (elapsed / total * 100).toFixed(0) +"%");
                                //$("#length").html(resp.current.length / 60);

                                if ($("#play").children("img").attr("src") !== "static/play_active.svg") {
                                    $("#play").children("img").attr("src", "static/play_active.svg");
                                    $("#play").css("cursor", "default");
                                }

                                if ($("#pause").children("img").attr("src") !== "static/pause.svg") {
                                    $("#pause").children("img").attr("src", "static/pause.svg");
                                    $("#pause").css("cursor", "pointer");
                                }
                                break;
                            case "PAUSE":
                                //var elapsed =
                                $("#elapsed").css("width", (resp.position / resp.current.length * 100).toFixed(0) +"px");
                                if ($("#play").children("img").attr("src") !== "static/play.svg") {
                                    $("#play").children("img").attr("src", "static/play.svg");
                                    $("#play").css("cursor", "pointer");
                                }

                                if ($("#pause").children("img").attr("src") !== "static/pause_active.svg") {
                                    $("#pause").children("img").attr("src", "static/pause_active.svg");
                                    $("#pause").css("cursor", "default");
                                }

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