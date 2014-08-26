`v1.1`

=======

### Usage

##### Auth

```
$ curl -XPOST {endpoint}/api -d auth=true -d first_name=alfred -d last_initial=h -d device_id='MacBook Pro'
```

##### Get

```
$ curl -XGET {endpoint}/api?user=alfredh
```

##### Update

```
$ curl -XPOST {endpoint}/api -d update=true -d user=alfredh -d state=PLAY -d next=false -d previous=false -d device_id='MacBook Pro'
```


=======

#### Get response

```json
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
        "length": 202,
        "started": 1408817868681
    },
    "next": {
        "id": 2,
        "title": "Sympathy For The Devil",
        "artist": "The Rolling Stones",
        "album": "BEGGARS BANQUET",
        "length": 378
    },
    "state": "PLAY",
    "position": 62,
    "current_time": 1408817868681,
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