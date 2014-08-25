(function ($, window, document) {

    "use strict";

    /**
     * Spela
     * @author Daniel Thengvall
     * @type {{auth: auth, play: play, pause: pause, next: next, previous: previous, device: device}}
     */
    window.Spela = {

        /**
         * Makes authentication api request, then sets user id in client's session storage
         * @param firstName
         * @param lastInitial
         */
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

        /**
         * Make update api request to change state
         */
        play: function () {
            $.post("/api",
                {
                    update: true,
                    user: sessionStorage.getItem("user"),
                    state: "PLAY"
                }
            );
        },

        /**
         * Make update api request to pause song
         */
        pause: function () {
            $.post("/api",
                {
                    update: true,
                    user: sessionStorage.getItem("user"),
                    state: "PAUSE"
                }
            );
        },

        /**
         * Makes update api request to play next song
         */
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

        /**
         * Makes update api request to play previous song
         */
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

        /**
         * Makes update api request to change device currently playing
         * @param id
         */
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

    /**
     * On page load behavior
     */
    $(document).ready(function () {

        /**
         * If user is not authenticated
         */
        if ( ! sessionStorage.getItem("user")) {

            /**
             * Make login for visible
             */
            $("#auth").css("display","block");

            /**
             * Focus ui on First Name input field
             */
            $("#name").focus();

            /**
             * Catch return key press
             */
            $('#initial').keydown( function(e) {
                var key = e.charCode ? e.charCode : e.keyCode ? e.keyCode : 0;
                if(key != 13) return;
                e.preventDefault();
                window.Spela.auth($('#name').val(), $('#initial').val());
            });
            return;
        }

        /**
         * Make player visible
         */
        $("#player").css("display","block");

        /**
         * Confirm authentication with server
         */
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

        /**
         * Makes api request for latest data, then updates player
         */
        var updateView = function() {
            $.get("/api",
                {
                    user: sessionStorage.getItem("user")
                },
                function (resp, status) {
                    if (status !== "success") return;

                    /**
                     * If no response is returned, assume authentication has expired and reset client
                     */
                    if ( ! resp) {
                        sessionStorage.removeItem("user");
                        window.location.href = "/";
                        return;
                    }

                    /**
                     * Update title and artist in player
                     */
                    $("#trackTitle").html(resp.current.title);
                    $("#trackArtist").html(resp.current.artist);

                    /**
                     * Update list of available devices
                     */
                    var deviceOptions = "";
                    var devices = $("#devices");
                    for (var i = 0; i < resp.devices.length; i++) {
                        if (resp.devices[i].is_playing) {
                            $("#activeDevice").html(resp.devices[i].name);
                        }
                        deviceOptions += "<li><a onclick=\"window.Spela.device('"+ resp.devices[i].name +"');\">"+ resp.devices[i].name +"</a></li>";
                    }
                    devices.html(deviceOptions);

                    /**
                     * Update next and previous button states
                     */
                    if ( ! resp.next) {
                        if ($("#next").children("img").attr("src") !== "static/next_disabled.svg") {
                            $("#next").children("img").attr("src", "static/next_disabled.svg");
                            $("#next").css("cursor", "default");
                        }
                    } else {
                        if ($("#next").children("img").attr("src") !== "static/next.svg" &&
                            $("#next").children("img").attr("src") !== "static/next_active.svg") {
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
                        if ($("#prev").children("img").attr("src") !== "static/previous.svg" &&
                            $("#prev").children("img").attr("src") !== "static/previous_active.svg") {
                            $("#prev").children("img").attr("src", "static/previous.svg");
                            $("#prev").css("cursor", "pointer");
                        }
                    }

                    /**
                     * Update play and pause button states
                     */
                    switch (resp.state) {
                        case "PLAY":
                            var started = resp.current.started;
                            var elapsed = Math.round((new Date().getTime() - started) / 1000);
                            var total = resp.current.length;
                            $("#elapsed").css("width", (elapsed / total * 100).toFixed(0) +"%");
                            if ($("#play").children("img").attr("src") !== "static/play_disabled.svg") {
                                $("#play").children("img").attr("src", "static/play_disabled.svg");
                                $("#play").css("cursor", "default");
                            }
                            if ($("#pause").children("img").attr("src") !== "static/pause.svg" &&
                                $("#pause").children("img").attr("src") !== "static/pause_active.svg") {
                                $("#pause").children("img").attr("src", "static/pause.svg");
                                $("#pause").css("cursor", "pointer");
                            }
                            break;
                        case "PAUSE":
                            //var elapsed =
                            $("#elapsed").css("width", (resp.position / resp.current.length * 100).toFixed(0) +"px");
                            if ($("#play").children("img").attr("src") !== "static/play.svg" &&
                                $("#play").children("img").attr("src") !== "static/play_active.svg") {
                                $("#play").children("img").attr("src", "static/play.svg");
                                $("#play").css("cursor", "pointer");
                            }
                            if ($("#pause").children("img").attr("src") !== "static/pause_disabled.svg") {
                                $("#pause").children("img").attr("src", "static/pause_disabled.svg");
                                $("#pause").css("cursor", "default");
                            }
                    }
                }
            );
        }
        updateView();
        window.setInterval(updateView, 1000);

        /**
         * Set hover behavior for previous button
         */
        $("#prev").mouseenter(
            function() {
                if ($("#prev").children("img").attr("src") !== "static/previous_active.svg" &&
                    $("#prev").children("img").attr("src") !== "static/previous_disabled.svg") {
                    $("#prev").children("img").attr("src", "static/previous_active.svg");
                }
            }
        );
        $("#prev").mouseleave(
            function() {
                if ($("#prev").children("img").attr("src") !== "static/previous.svg" &&
                    $("#prev").children("img").attr("src") !== "static/previous_disabled.svg") {
                    $("#prev").children("img").attr("src", "static/previous.svg");
                }
            }
        );

        /**
         * Set hover behavior for next button
         */
        $("#next").mouseenter(
            function() {
                if ($("#next").children("img").attr("src") !== "static/next_active.svg" &&
                    $("#next").children("img").attr("src") !== "static/next_disabled.svg") {
                    $("#next").children("img").attr("src", "static/next_active.svg");
                }
            }
        );
        $("#next").mouseleave(
            function() {
                if ($("#next").children("img").attr("src") !== "static/next.svg" &&
                    $("#next").children("img").attr("src") !== "static/next_disabled.svg") {
                    $("#next").children("img").attr("src", "static/next.svg");
                }
            }
        );

        /**
         * Set hover behavior for play button
         */
        $("#play").mouseenter(
            function() {
                if ($("#play").children("img").attr("src") !== "static/play_active.svg" &&
                    $("#play").children("img").attr("src") !== "static/play_disabled.svg") {
                    $("#play").children("img").attr("src", "static/play_active.svg");
                }
            }
        );
        $("#play").mouseleave(
            function() {
                if ($("#play").children("img").attr("src") !== "static/play.svg" &&
                    $("#play").children("img").attr("src") !== "static/play_disabled.svg") {
                    $("#play").children("img").attr("src", "static/play.svg");
                }
            }
        );

        /**
         * Set hover behavior for pause button
         */
        $("#pause").mouseenter(
            function() {

                if ($("#pause").children("img").attr("src") !== "static/pause_active.svg" &&
                    $("#pause").children("img").attr("src") !== "static/pause_disabled.svg") {
                    $("#pause").children("img").attr("src", "static/pause_active.svg");
                }
            }
        );
        $("#pause").mouseleave(
            function() {
                if ($("#pause").children("img").attr("src") !== "static/pause.svg" &&
                    $("#pause").children("img").attr("src") !== "static/pause_disabled.svg") {
                    $("#pause").children("img").attr("src", "static/pause.svg");
                }
            }
        );
    });
})(window.jQuery, window, document);