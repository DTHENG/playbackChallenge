#### Source of: [playback.dtheng.com](http://playback.dtheng.com)

`v0.3`

=======

### Usage

##### Auth

```
$ curl -XPOST http://playback.dtheng.com/api -d auth=true -d first_name=alfred -d last_initial=h -d device_id='MacBook Pro'
```

##### Get

```
$ curl -XGET http://playback.dtheng.com/api?user=alfredh
```

##### Update

```
$ curl -XPOST http://playback.dtheng.com/api -d update=true -d state=PLAY -d next=false -d previous=false -d device_id='MacBook Pro'
```


=======

### Responses

#### Get

```
{
    "previous": {
        "id": 0,
        "title": "Black Sugar",
        "artist": "The Rolling Stones",
        "album": "Sticky Fingers",
        "length": 229
    },
    "current": {
        "id": 1,
        "title": "Paint It, Black",
        "artist": "The Rolling Stones",
        "album": "AFTERMATH",
        "length": 202
    },
    "next": {
        "id": 2,
        "title": "Sympathy For The Devil",
        "artist": "The Rolling Stones",
        "album": "BEGGARS BANQUET",
        "length": 378
    },
    "state": "PLAY", // or "PAUSE"
    "position": 62,
    "devices": [
        {
            "id": 1001,
            "name": "MacBook Pro",
            "is_playing": false
        },
        {
            "id": 1002,
            "name": "iPhone 5s",
            "is_playing": true
        },
        {
            "id": 1003,
            "name": "Galaxy S3",
            "is_playing": false
        }
    ]
}
```