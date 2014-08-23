#### Source of: [playback.dtheng.com](http://playback.dtheng.com)

`v0.3`

=======

### Usage

##### Authentication

```
$ curl -XPOST http://playback.dtheng.com/api 
        -d auth=true 
        -d first_name=alfred 
        -d last_initial=h 
        -d device_id='MacBook Pro'
```

##### Get

```
$ curl -XGET http://playback.dtheng.com/api?user=alfredh
```

##### Update

```
$ curl -XPOST http://playback.dtheng.com/api
        -d update=true
        -d state=PLAY
        -d next=false
        -d previous=false
        -d device_id='MacBook Pro'
```
